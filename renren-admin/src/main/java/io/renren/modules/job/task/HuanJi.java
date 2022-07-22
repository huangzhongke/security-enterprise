package io.renren.modules.job.task;


import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import io.renren.modules.job.entity.ScheduleJobEntity;
import io.renren.modules.job.service.ScheduleJobService;
import io.renren.modules.spider.one.dao.OrderDao;
import io.renren.modules.spider.one.entity.*;
import io.renren.modules.spider.menu.entity.*;
import io.renren.modules.spider.one.exception.TokenDisabledException;
import io.renren.modules.spider.one.service.LineService;
import io.renren.modules.spider.one.service.SpiderReferenceService;
import io.renren.modules.spider.utils.HttpUtils;
import io.renren.modules.spider.utils.MyUtils;
import lombok.extern.slf4j.Slf4j;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.PersistJobDataAfterExecution;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author kee
 * @version 1.0
 * @date 2022/2/16 14:18
 */

@Slf4j
@PersistJobDataAfterExecution
@DisallowConcurrentExecution
@Component("one")
public class HuanJi implements ITask {
    @Autowired
    LineService lineService;
    @Autowired
    ScheduleJobService scheduleJobService;
    @Autowired
    JavaMailSender javaMailSender;
    @Autowired
    OrderDao orderDao;
    @Autowired
    SpiderReferenceService spiderReferenceService;


    @Override
    public void run(String params) {
        try {

            FormParams formParams = JSONObject.parseObject(params, FormParams.class);
            Port startPort = formParams.getStartPort();
            Port endPort = formParams.getEndPort();
            Line line = lineService.selectById(formParams.getId());
            //刷新身份码
            Map<String, Object> dataMap = getToken(formParams, line, startPort, endPort);
            //初始化数据
            List<SpiderReference> references = spiderReferenceService.getListByLineId(formParams.getId());
            System.out.println("更新authorization 账号: " + formParams.getAccount() + "(0:环集,1:泰博,2:附属)");
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
            if (references.size() > 0) {
                String showText = "总共" + references.size() + "个小提单号，当前时间：" + sdf.format(new Date()) + "查询的是【" + startPort.getMatched() + "-" + endPort.getMatched() + "】 type:" + formParams.getEquipment() + " ETD间隔时间:" + formParams.getEtdDays();
                if (formParams.getIsNeedLineName()) {
                    showText += "【航名】:" + formParams.getVesselName() + "【航次】:" + formParams.getVoyage();
                } else {
                    showText += "未指定航名航次";
                }
                if (formParams.getIsNeedSupplierName()) {
                    showText += " 【航线代码】: " + formParams.getSupplierName();
                } else {
                    showText += " 未指定航线代码 ";
                }
                log.debug(showText);
                System.out.println(showText);
                Map<String, Object> fclScheduleWithRatesData = getFclScheduleWithRates(formParams, line, startPort, endPort);
                if (null != fclScheduleWithRatesData) {
                    System.out.println("查询到所需的航线信息开始下单 " + sdf.format(new Date()));
                    getAddress(formParams, dataMap);
                    List<String> successList = new ArrayList<>();
                    List<String> failList = new ArrayList<>();
                    for (SpiderReference sr : references) {
                        if (!sr.getUsed()) {
                            String bookId = submitBookings(fclScheduleWithRatesData, sr.getReference(), formParams, line, startPort, endPort, dataMap);
                            if (bookId != null && !"error".equals(bookId)) {
                                // 确定提交，修改状态
                                updateStatus(bookId, formParams, line);
                                sr.setUsed(true);
                                sr.setSuccess(true);
                                spiderReferenceService.updateById(sr);
                                saveOrder(sr.getReference(), formParams, startPort, endPort);
                                successList.add(sr.getReference());
                            } else if (bookId == null) {
                                sr.setUsed(true);
                                sr.setSuccess(false);
                                spiderReferenceService.updateById(sr);
                                failList.add(sr.getReference());
                            }
                            Thread.sleep(formParams.getOrderSleepTime());

                        }
                    }

                    stopJob(line);
                    sendMail(successList, failList, formParams, startPort, endPort);
                }
            }
        } catch (ParseException p) {
            System.out.println(p);
        } catch (InterruptedException e) {
            System.out.println(e);
        }
    }

    private void stopJob(Line line) {
        Long[] ids = new Long[1];
        ids[0] = line.getJobId();
        scheduleJobService.pause(ids);
    }

    private void saveOrder(String reference, FormParams formParams, Port startPort, Port endPort) {
        OrderEntity order = OrderEntity.builder()
                .startPort(startPort.getMatched())
                .endPort(endPort.getMatched())
                .equipment(formParams.getEquipment())
                .quantity(formParams.getQuantity())
                .orderSleepTime(formParams.getOrderSleepTime())
                .orderDate(new Date())
                .isProxy(formParams.getIsProxy())
                .isNeedLineName(formParams.getIsNeedLineName())
                .isNeedSupplierName(formParams.getIsNeedSupplierName())
                .vessel(formParams.getVesselName())
                .voyage(formParams.getVoyage())
                .supplierName(formParams.getSupplierName())
                .account(formParams.getAccount())
                .etd(formParams.getEtdDays())
                .reference(reference)
                .build();
        orderDao.insert(order);
    }

    private void updateStatus(String bookId, FormParams formParams, Line line) {
        String url = "https://cetusapi-prod.kontainers.io/booking/api/v1/customer/booking/tick/" + bookId;
        Map<String, String> params = new HashMap<>();
        params.put("Authorization", line.getAuthorization());

        Map<String, Object> resultMap = new HashMap<>();
        String result = HttpUtils.sendPut(url, resultMap, params, formParams.getIsProxy());

        //System.out.println("【确定提交，修改状态】:" + result);
        log.info("【确定提交，修改状态】:" + result);
    }

    Map<String, Object> submitBookingsData(Map<String, Object> fclScheduleWithRatesData, String reference, FormParams formParams, Port startPort, Port endPort, Map<String, Object> dataMap) {
        Map<String, Object> booking = JSONObject.parseObject(MyUtils.readJsonFile("one/submitBookData.json"));
        Map<String, Object> info = JSONObject.parseObject(booking.get("booking").toString());

        info.put("fmc", endPort.getFmc());
        List<Map<String, Object>> referencesTemp = JSONObject.parseObject(info.get("references").toString(), new TypeReference<List<Map<String, Object>>>() {
        });
        if ("NANSHA, GUANGDONG".equals(startPort.getMatched()) || "YANTIAN, GUANGDONG".equals(startPort.getMatched()) || "XIAMEN, FUJIAN".equals(startPort.getMatched())) {
            //只有Yantian和xiamen 是 填空 其他都要填
            referencesTemp.remove(1);
        } else {
            //可变参数，小提单号

            referencesTemp.get(1).put("reference", reference);
        }

        info.put("references", referencesTemp);
        info.put("customerReference", reference);

        //可变参数，型号
        List<Map<String, Object>> bookingItems = JSONObject.parseObject(info.get("bookingItems").toString(), new TypeReference<List<Map<String, Object>>>() {
        });
        bookingItems.get(0).put("containerType", formParams.getEquipment());
        bookingItems.get(0).put("weight", formParams.getWeight().toString());
//        判断数量，是否大于1,一票多个高柜
        if (formParams.getQuantity() > 1) {
            for (int i = 1; i < formParams.getQuantity(); i++) {
                Map<String, Object> map = new HashMap<>();
                map.putAll(bookingItems.get(0));
                List<Map<String, Object>> bookingItemDetails = new ArrayList<>();
                Map<String, Object> map2 = new HashMap<>();
                map2.put("commodityCode", "940330");
                map2.put("description", "OFFICE FURNITURE, OF WOOD");
                map2.put("distanceUnit", "cms");
                map2.put("valueCurrency", "USD");
                map2.put("volumeUnit", "CubicMeters");
                map2.put("weightUnit", "KG");
                bookingItemDetails.add(map2);
                map.put("bookingItemDetails", bookingItemDetails);
                bookingItems.add(map);
            }
        }
        //如果是美线会加customs
        if (endPort.getFmc() && endPort.getMatched().equals("VANCOUVER, BC")) {
            Map<String, Object> customs = new HashMap<>();
            List<Map<String, Object>> countryConfigs = new ArrayList<>();
            Map<String, Object> map = new HashMap<>();
            map.put("code", "2");
            map.put("country", "US");
            map.put("scac", formParams.getScac());
            countryConfigs.add(map);
            customs.put("countryConfigs", countryConfigs);
            info.put("customs", customs);
        }
        info.put("moveType", null);
        info.put("bookingItems", bookingItems);
        info.put("bookingCosts", fclScheduleWithRatesData.get("bookingCosts"));
        info.put("logisticsDetails", fclScheduleWithRatesData.get("logisticsDetails"));
        if (formParams.getIsNeedConsigneeAddress()) {
            Map<String, Object> consigneeBookingAddress = (Map<String, Object>) dataMap.get("consigneeBookingAddress");
            info.put("consigneeBookingAddress", consigneeBookingAddress);
        }
        if (formParams.getIsNeedNotifyAddress()) {
            Map<String, Object> notifyBookingAddress = (Map<String, Object>) dataMap.get("notifyBookingAddress");
            info.put("notifyBookingAddress", notifyBookingAddress);
        }

        if (formParams.getIsNeedBrokerageAddress()) {
            Map<String, Object> brokerageAddress = (Map<String, Object>) dataMap.get("brokerageAddress");
            info.put("brokerageAddress", brokerageAddress);
        }
        Map<String, Object> consignorBookingAddress = (Map<String, Object>) dataMap.get("consignorBookingAddress");
        Map<String, Object> bookedByAddress = (Map<String, Object>) dataMap.get("bookedByAddress");
        info.put("consignorBookingAddress", consignorBookingAddress);
        info.put("bookedByAddress", bookedByAddress);
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("booking", info);
        return resultMap;
    }

    private String submitBookings(Map<String, Object> fclScheduleWithRatesData, String reference, FormParams formParams, Line line, Port startPort, Port endPort, Map<String, Object> dataMap) {
        String url = "https://cetusapi-prod.kontainers.io/booking/api/v1/customer/bookings";
        Map<String, String> params = new HashMap<>();
        params.put("Authorization", line.getAuthorization());

//        请求数据处理
        Map<String, Object> resultMap = submitBookingsData(fclScheduleWithRatesData, reference, formParams, startPort, endPort, dataMap);
        System.out.println("【预提交】请求数据处理:" + resultMap);
//        return null;
        String result = HttpUtils.sendPost(url, resultMap, params, formParams.getIsProxy());

        JSONObject object = JSONObject.parseObject(result);
        if (object.get("booking") != null) {
            Map<String, Object> info = JSONObject.parseObject(object.get("booking").toString());
            System.out.println("【预提交成功】:" + info.get("id"));
            return info.get("id").toString();
        } else {
            List<Map<String, Object>> errors = JSONObject.parseObject(object.get("errors").toString(), new TypeReference<List<Map<String, Object>>>() {
            });
            if ("422".equals(errors.get(0).get("status"))) {
                System.out.println("【小提单号已存在】:" + result);
                return null;
            } else {
                System.out.println("【预提交失败】:" + result);
                return "error";
            }
        }
    }

    //    获取航线的接口
    private Map<String, Object> getFclScheduleWithRates(FormParams formParams, Line line, Port startPort, Port endPort) throws ParseException {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        //读取数据文件
        Map<String, Object> fclScheduleDataResultMapJSON = JSONObject.parseObject(MyUtils.readJsonFile("one/fclScheduleData.json"));
        Map<String, Object> fclQuoteRequest = JSONObject.parseObject(fclScheduleDataResultMapJSON.get("fclQuoteRequest").toString());

        //添加柜子型号和数量
        Map<String, Object> map = new HashMap<>(2);
        List<Map<String, Object>> equipmentRequest = new ArrayList<>();
        map.put("equipment", formParams.getEquipment());
        map.put("quantity", formParams.getQuantity());
        equipmentRequest.add(map);
        fclQuoteRequest.put("equipmentRequest", equipmentRequest);

        //搜索日期今天的日期加7天
        fclQuoteRequest.put("schedulesAfterDate", MyUtils.getNowDate(7, "yyyy-MM-dd"));
        //港口信息
        fclQuoteRequest.put("from", startPort.getMatched());
        fclQuoteRequest.put("fromCountry", startPort.getMatchedCountry());
        fclQuoteRequest.put("fromUNCode", startPort.getUnCode());
        if (startPort.getIata() == null) {
            fclQuoteRequest.remove("fromIata");
        } else {
            fclQuoteRequest.put("fromIata", startPort.getIata());
        }
        fclQuoteRequest.put("to", endPort.getMatched());
        fclQuoteRequest.put("toCountry", endPort.getMatchedCountry());
        fclQuoteRequest.put("toUNCode", endPort.getUnCode());
        fclQuoteRequest.put("fmc", endPort.getFmc());
        if (endPort.getIata() != null) {
            fclQuoteRequest.put("toIata", endPort.getIata());
        }
        Map<String, Object> fclScheduleDataResultMap = new HashMap<>();
        String url = "https://cetusapi-prod.kontainers.io/trip-ui/api/v1/customer/fclScheduleWithRates";
        Map<String, String> params = new HashMap<>();
        params.put("Authorization", line.getAuthorization());
        fclScheduleDataResultMap.put("fclQuoteRequest", fclQuoteRequest);
        String result = HttpUtils.sendPost(url, fclScheduleDataResultMap, params, false);

        if ("".equals(result)) {
            System.out.println("获取航线接口的数据为空");
            throw new TokenDisabledException("获取航线接口的数据为空");
        }

        JSONObject object = JSONObject.parseObject(result);

        //下单时间 今天日期加上指定的etd天数
        String orderedDate = MyUtils.getNowDate(formParams.getEtdDays(), "yyyy-MM-dd");
        if (object.get("multiLegScheduleWithRate") != null) {
//            有航线,取最新的一条航线
            List<Map<String, Object>> multiLegScheduleWithRateList = JSONObject.parseObject(object.get("multiLegScheduleWithRate").toString(), new TypeReference<List<Map<String, Object>>>() {
            });
            List<Map<String, Object>> supplierSummaries = JSONObject.parseObject(object.get("supplierSummaries").toString(), new TypeReference<List<Map<String, Object>>>() {
            });
            if (multiLegScheduleWithRateList.size() > 0) {
//               循环，取最新的(etd最大的)，以及判断与当前时间的差在11天以上（目前测试保证在15+7天）
                Map<String, Object> multiLegScheduleWithRate = null;

                for (Map<String, Object> temp : multiLegScheduleWithRateList) {
                    //  获取变量
                    List<Map<String, Object>> legs = JSONObject.parseObject(temp.get("legs").toString(), new TypeReference<List<Map<String, Object>>>() {
                    });
                    Map<String, Object> leg = legs.get(0);
//                   未显示的航班，过滤掉
                    List<Map<String, Object>> issues = JSONObject.parseObject(leg.get("issues").toString(), new TypeReference<List<Map<String, Object>>>() {
                    });
                    if (issues.size() > 0) {
                        continue;
                    }
//                   禁止下单的航班过滤掉
                    if (Boolean.parseBoolean(leg.get("isSoldOut").toString())) {
                        continue;
                    }
                    if (formParams.getIsNeedSupplierName()) {
                        if (!formParams.getSupplierName().equals(leg.get("supplierName").toString())) {
                            continue;
                        }
                    }
                    if (formParams.getIsNeedLineName()) {
                        if (!(formParams.getVesselName().equals(leg.get("vesselName")) && formParams.getVoyage().equals(leg.get("voyage")))) {
                            continue;
                        }
                    }
                    //                   判断航线条件
                    if (formParams.getEtdDays() != 0 && formParams.getPrice() != 0) {
//                       即判断时间，也判断价格
                        if (MyUtils.dateCompare(dateFormat.parse(orderedDate), dateFormat.parse(leg.get("etd").toString()))
                                && Float.parseFloat(temp.get("totalCost").toString()) < formParams.getPrice()) {
                            multiLegScheduleWithRate = leg;
                        }
                    } else if (formParams.getEtdDays() != 0) {
                        //                  判断时间是否大于orderedDate起始时间
                        if (MyUtils.dateCompare(dateFormat.parse(orderedDate), dateFormat.parse(legs.get(0).get("etd").toString()))) {
                            multiLegScheduleWithRate = leg;
                        }
                    } else if (formParams.getPrice() != 0) {
                        //                       根据钱作为判断,小于满足条件的价格
                        if (Float.parseFloat(temp.get("totalCost").toString()) < formParams.getPrice()) {
                            multiLegScheduleWithRate = leg;
                        }
                    }else if(formParams.getEtdDays() == 0){
                        multiLegScheduleWithRate = leg;
                    }
                }

                if (multiLegScheduleWithRate == null) {
                    String printText = "";
                    if (formParams.getEtdDays() != 0) {
                        printText = "【目前最新ETD时间】：" + orderedDate + ";";
                    }
                    if (formParams.getPrice() != 0) {
                        printText = printText + "【可接受金额必须小于】：" + formParams.getPrice() + ";";
                    }
                    log.info(printText);
                    return null;
                }
                return fclScheduleWithRatesHandleData(multiLegScheduleWithRate, supplierSummaries);
            }
        }
        return null;
    }

    //    获取航线的接口返回的数据处理
    private Map<String, Object> fclScheduleWithRatesHandleData(Map<String, Object> multiLegScheduleWithRate, List<Map<String, Object>> supplierSummaries) {

//        1、收集logisticsDetails里面的信息，完成预提交接口
        List<Map<String, Object>> logisticsDetails = new ArrayList<>();
        Map<String, Object> logisticsDetailsTemp = new HashMap<>();
        Map<String, Object> seaLegData = new HashMap<>();
        seaLegData.put("billType", "negotiable_received");
        seaLegData.put("lloydsNumber", multiLegScheduleWithRate.get("lloydsNumber"));
        seaLegData.put("vesselImo", multiLegScheduleWithRate.get("vesselImo"));

        logisticsDetailsTemp.put("seaLegData", seaLegData);
        logisticsDetailsTemp.put("eta", multiLegScheduleWithRate.get("eta"));
        logisticsDetailsTemp.put("etd", multiLegScheduleWithRate.get("etd"));
        logisticsDetailsTemp.put("vgmCutOffTime", multiLegScheduleWithRate.get("vgmCutOffTime"));
        logisticsDetailsTemp.put("siDocCutOffTime", multiLegScheduleWithRate.get("siDocCutOffTime"));
        logisticsDetailsTemp.put("latestPortArrivalTime", multiLegScheduleWithRate.get("latestPortArrivalTime"));
        logisticsDetailsTemp.put("fromLocation", multiLegScheduleWithRate.get("from"));
        logisticsDetailsTemp.put("fromCountry", multiLegScheduleWithRate.get("fromCountry"));
        logisticsDetailsTemp.put("fromIata", multiLegScheduleWithRate.get("fromIata"));
        logisticsDetailsTemp.put("fromUNCode", multiLegScheduleWithRate.get("fromUNCode"));
        logisticsDetailsTemp.put("toLocation", multiLegScheduleWithRate.get("to"));
        logisticsDetailsTemp.put("toCountry", multiLegScheduleWithRate.get("toCountry"));
        logisticsDetailsTemp.put("toUNCode", multiLegScheduleWithRate.get("toUNCode"));
        logisticsDetailsTemp.put("toIata", multiLegScheduleWithRate.get("toIata"));
        logisticsDetailsTemp.put("machineName", multiLegScheduleWithRate.get("vesselName"));
        logisticsDetailsTemp.put("voyageNumber", multiLegScheduleWithRate.get("voyage"));
        logisticsDetailsTemp.put("mode", multiLegScheduleWithRate.get("mode"));

        for (Map<String, Object> temp : supplierSummaries) {
            if (multiLegScheduleWithRate.get("supplierName").equals(temp.get("fullName"))) {
                logisticsDetailsTemp.put("supplier", temp.get("scac"));
            }
        }
        logisticsDetails.add(logisticsDetailsTemp);

//        2、收集bookingCosts
        List<Map<String, Object>> rates = JSONObject.parseObject(multiLegScheduleWithRate.get("rates").toString(), new TypeReference<List<Map<String, Object>>>() {
        });
        for (Map<String, Object> ratesTemp : rates) {
            ratesTemp.remove("id");
            ratesTemp.put("description", ratesTemp.get("desc"));
            ratesTemp.remove("desc");
            ratesTemp.put("oriAmount", ratesTemp.get("originAmount"));
            ratesTemp.remove("originAmount");
            ratesTemp.put("paymentTerm", "collect");
        }
//        3、放入到一个map中
        Map<String, Object> resultMap = new HashMap<>(2);
        resultMap.put("bookingCosts", rates);
        resultMap.put("logisticsDetails", logisticsDetails);
        System.out.println("【获取航线的接口】成功");
        return resultMap;

    }


    private Map<String, Object> getToken(FormParams formParams, Line line, Port startPort, Port endPort) {
        String url = "https://cetusapi-prod.kontainers.io/tenancy/api/v1/customer/users/self";
        Map<String, String> params = new HashMap<>();
        params.put("Authorization", line.getAuthorization());
        params.put("Referer", "https://instantquote.one-line.com/");
        String result = HttpUtils.sendGet(url, null, params, false);
        if ("".equals(result)) {
            log.info("token身份码失效停止任务");
            sendErrorMail("token身份码失效停止任务,请更换身份码再重新启动任务,账号:" + formParams.getAccount(), formParams, startPort, endPort);
            stopJob(lineService.selectById(formParams.getId()));
        }
        JSONObject object = JSONObject.parseObject(result);
        return getTokenHandleData(object, formParams, line);
    }

    private Map<String, Object> getTokenHandleData(JSONObject object, FormParams formParams, Line line) {
        Map<String, Object> consignorBookingAddress = new HashMap<>();
        Map<String, Object> bookedByAddress = new HashMap<>();
        Map<String, Object> brokerageAddress = new HashMap<>();
        Map<String, Object> addressInfo = new HashMap<>();
        JSONObject info = JSONObject.parseObject(object.get("user").toString());
        addressInfo.put("lastName", info.get("lastName").toString());
        addressInfo.put("firstName", info.get("firstName").toString());
        addressInfo.put("email", info.get("email").toString());

        JSONObject company = JSONObject.parseObject(info.get("company").toString());
        String addressText = company.get("name").toString();
        addressInfo.put("companyName", addressText);
        addressInfo.put("customerCode", company.get("companyCode").toString());
        JSONObject companyAddress = company.getJSONArray("addresses").getJSONObject(0);
        addressInfo.put("address1", companyAddress.get("address1").toString());
        addressInfo.put("address2", companyAddress.get("address2").toString());
        if (!"".equals(companyAddress.get("address3").toString())) {
            addressInfo.put("address3", companyAddress.get("address3").toString());
        }
        addressInfo.put("cityTown", companyAddress.get("cityTown").toString());
        addressInfo.put("country", companyAddress.get("country").toString());
        addressInfo.put("vatNo", companyAddress.get("vatNo").toString());
        addressInfo.put("postCode", companyAddress.get("postCode").toString());
        addressText = addressText + "," + companyAddress.get("address1").toString() + "," + companyAddress.get("address2").toString() + "," + companyAddress.get("cityTown").toString() + "," + companyAddress.get("postCode").toString() + ",CN";
        addressInfo.put("addressText", addressText);
        consignorBookingAddress.putAll(addressInfo);
        bookedByAddress.putAll(addressInfo);
        bookedByAddress.put("companyRole", company.get("role").toString());
        brokerageAddress.putAll(addressInfo);
        String token = info.get("token").toString();

        //修改数据库的身份码
        //修改航线的params
        formParams.setAuthorization(token);
        line.setParams(formParams.toString());
        line.setAuthorization(token);
        lineService.updateById(line);
        ScheduleJobEntity scheduleJobEntity = scheduleJobService.selectById(line.getJobId());
        scheduleJobEntity.setParams(formParams.toString());
        scheduleJobService.updateById(scheduleJobEntity);
        Map<String, Object> returnMap = new HashMap<>();
        returnMap.put("bookedByAddress", bookedByAddress);
        returnMap.put("brokerageAddress", brokerageAddress);
        returnMap.put("consignorBookingAddress", consignorBookingAddress);
        return returnMap;
    }

    private void getAddress(FormParams formParams, Map<String, Object> dataMap) {
        if (formParams.getIsNeedNotifyAddress() && formParams.getIsNeedConsigneeAddress()) {
            Map<String, Object> notifyBookingAddress = new HashMap<>();
            Map<String, Object> consigneeBookingAddress = new HashMap<>();
            Address notifyAddress = formParams.getNotifyAddress();
            Address consigneeAddress = formParams.getConsigneeAddress();
            notifyAddress = formParams.getNotifyAddress();
            consigneeAddress = formParams.getConsigneeAddress();
            Map<String, Object> addressMap = new HashMap<>();
            addressMap.put("address1", notifyAddress.getAddress1());
            addressMap.put("addressText", notifyAddress.getSingleLine());
            addressMap.put("cityTown", notifyAddress.getCityTown());
            addressMap.put("companyName", notifyAddress.getCompanyName());
            addressMap.put("country", notifyAddress.getCountry());
            addressMap.put("email", notifyAddress.getEmail());
            addressMap.put("firstName", notifyAddress.getFirstName());
            addressMap.put("lastName", notifyAddress.getLastName());
            addressMap.put("phone", notifyAddress.getPhone());
            if (notifyAddress.getVatNo() != null) {
                addressMap.put("vatNo", notifyAddress.getVatNo());
            }
            if (notifyAddress.getPostCode() != null) {
                addressMap.put("postCode", notifyAddress.getPostCode());
            }
            notifyBookingAddress.putAll(addressMap);
            consigneeBookingAddress.putAll(addressMap);
            dataMap.put("notifyBookingAddress", notifyBookingAddress);
            dataMap.put("consigneeBookingAddress", consigneeBookingAddress);

        }

    }

    //发送邮箱
    private void sendMail(List successList, List failList, FormParams formParams, Port startPort, Port endPort) {
        // 构建一个邮件对象
        SimpleMailMessage message = new SimpleMailMessage();
        // 设置邮件主题
        String accountStr = "";
        if (formParams.getAccount().equals(0)) {
            accountStr = "前端环集";
        }
        if (formParams.getAccount().equals(1)) {
            accountStr = "前端泰博";
        }
        if (formParams.getAccount().equals(2)) {
            accountStr = "前端附属";
        }

        String messageText = "【从" + startPort.getMatched() + "到" + endPort.getMatched() + "的航线】 " + formParams.getEquipment();
        message.setSubject(messageText + accountStr + "抢单成功");
        // 设置邮件发送者，这个跟application.yml中设置的要一致
        message.setFrom("1059308740@qq.com");

//        message.setTo("1973432033@qq.com","Zhibo_Tang@zjou.edu.cn","771829811@qq.com"); one-ebooking@nb-hj.com 公司邮箱 ,"1973432033@qq.com","1610531743@qq.com","one-ebooking@nb-hj.com"
        message.setTo("1059308740@qq.com","one-ebooking@nb-hj.com");
        // 设置邮件发送日期
        message.setSentDate(new Date());
        // 设置邮件的正文

        if (failList.size() > 0) {
            message.setSubject(messageText + accountStr + "抢单失败");
//            代表有小提单号已被使用过
            messageText = messageText + "，下失败了" + failList.size() + "单，其中小提单号有被使用过而下单失败" +
                    "下单的小提单号：【" + String.join("，", failList) + "】";

        } else {
            String text = "，成功下了" + successList.size() + "单。" + "柜子型号：" + formParams.getEquipment();
            if (formParams.getIsNeedLineName()) {
                messageText = messageText + text + "航名:" + formParams.getVesselName() + "航次:" + formParams.getVoyage() + " ETD间隔时间: 0" +
                        "下单的小提单号：【" + String.join("，", successList) + "】";
            } else {
                if (formParams.getIsNeedSupplierName()) {
                    messageText = messageText + text + " ETD间隔时间:" + formParams.getEtdDays() + " 航线代码" + formParams.getSupplierName() +
                            "下单的小提单号：【" + String.join("，", successList) + "】";
                } else {
                    messageText = messageText + text + " ETD间隔时间:" + formParams.getEtdDays() +
                            "下单的小提单号：【" + String.join("，", successList) + "】";
                }
            }
        }
        message.setText(messageText);
        log.info(messageText);
        // 发送邮件
        javaMailSender.send(message);
    }

    //发送邮箱
    private void sendErrorMail(String errorMessage, FormParams formParams, Port startPort, Port endPort) {
        String title = "【从" + startPort.getMatched() + "到" + endPort.getMatched() + "的航线】 " + formParams.getEquipment();
        // 构建一个邮件对象
        SimpleMailMessage message = new SimpleMailMessage();
        // 设置邮件主题
        message.setSubject(title);
        // 设置邮件发送者，这个跟application.yml中设置的要一致
        message.setFrom("1059308740@qq.com");

//       message.setTo("1973432033@qq.com","Zhibo_Tang@zjou.edu.cn","771829811@qq.com");
        message.setTo("1059308740@qq.com","one-ebooking@nb-hj.com");
        // 设置邮件发送日期
        message.setSentDate(new Date());

        message.setText(errorMessage);
        // 发送邮件
        javaMailSender.send(message);
    }
}
