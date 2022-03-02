package io.renren.modules.job.task;


import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import io.renren.modules.job.entity.ScheduleJobEntity;
import io.renren.modules.job.service.ScheduleJobService;
import io.renren.modules.spider.dao.OrderDao;
import io.renren.modules.spider.entity.*;
import io.renren.modules.spider.service.LineService;
import io.renren.modules.spider.utils.HttpUtils;
import io.renren.modules.spider.utils.MyUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * @author kee
 * @version 1.0
 * @date 2022/2/16 14:18
 */
@Component("huanji")
public class HuanJi implements ITask {
    private List<String> references = new ArrayList<>();
    private Integer etdDays;
    private Boolean isNeedLineName;
    private Boolean isNeedSupplierName;
    private String vesselName;
    private String voyage;
    private String supplierName;
    private String equipment;
    private Integer quantity;
    private Map<String, Object> fclQuoteRequest = new HashMap<>();
    private String orderedDate = ""; //新的ETD航线时间必须大于这个时间
    private static Boolean isProxy;
    private static Integer orderSleepTime;
    private String authorization;
    private Map<String, Object> consignorBookingAddress = new HashMap<>(); //发货人信息
    private Map<String, Object> consigneeBookingAddress = new HashMap<>(); //收货人信息
    private Map<String, Object> notifyBookingAddress = new HashMap<>(); //通知人信息
    private Map<String, Object> brokerageAddress = new HashMap<>(); //货代人信息
    private Map<String, Object> bookedByAddress = new HashMap<>();
    private Boolean isNeedBrokerAddress;
    private String messageText = ""; //发送邮箱信息正文
    private Port startPort;
    private Port endPort;
    private Address notifyAddress;
    private Address consigneeAddress;
    private Double maxPrice;
    private String scac;
    @Autowired
    LineService lineService;
    @Autowired
    ScheduleJobService scheduleJobService;
    @Autowired
    JavaMailSender javaMailSender;
    @Autowired
    OrderDao orderDao;
    private FormParams formParams;
    private int referenceIndex = 0;
    private Map<String, Object> paramsMap;
    private Line paramsLine;

    @Override
    public void run(String params) {
        paramsMap = JSONObject.parseObject(params);
        Long id = Long.parseLong(paramsMap.get("id").toString());
        paramsLine = lineService.selectById(id);
        formParams = JSONObject.parseObject(paramsLine.getParams(), FormParams.class);
        formParams.setId(id);
        if (formParams.getBooked() == 1) {
            Long[] ids = new Long[10];
            ids[0] = paramsLine.getJobId();
            stopTask(ids);
            return;
        }
        try {
            //初始化数据
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
            init();
            //刷新身份码
            getToken();
            if (references.size() > 0) {
                String showText = "总共" + references.size() + "个小提单号，当前时间：" + sdf.format(new Date()) + "查询的是【" + startPort.getMatched() + "-" + endPort.getMatched() + "】 type:" + equipment + " ETD间隔时间:" + etdDays;
                if (isNeedLineName) {
                    showText += "【航名】:" + vesselName + "【航次】:" + voyage;
                } else {
                    showText += "未指定航名航次";
                }
                if (isNeedSupplierName) {
                    showText += " 【航线代码】: " + supplierName;
                } else {
                    showText += " 未指定航线代码 ";
                }
                System.out.println(showText);
                Map<String, Object> fclScheduleWithRatesData = getFclScheduleWithRates();
                if (null != fclScheduleWithRatesData) {
                    System.out.println("查询到所需的航线信息开始下单 " + sdf.format(new Date()));
                    int successNum = 0;
                    while (referenceIndex < references.size()) {
                        String bookId = submitBookings(fclScheduleWithRatesData);
                        if (bookId != null && !"error".equals(bookId)) {
                            // 确定提交，修改状态
                            updateStatus(bookId);
                            int tempProxy = 0;
                            int tempSupplierName = 0;
                            int tempLineName = 0;
                            if (isProxy){
                                tempProxy = 0;
                            }else {
                                tempProxy = 1;
                            }
                            if (isNeedSupplierName){
                                tempSupplierName = 0;
                            }else {
                                tempSupplierName = 1;
                            }
                            if (isNeedLineName){
                                tempLineName = 0;
                            }else {
                                tempLineName = 1;
                            }

                            OrderEntity order = OrderEntity.builder()
                                    .from(startPort.getMatched())
                                    .to(endPort.getMatched())
                                    .equipment(equipment)
                                    .quantity(quantity)
                                    .orderSleepTime(orderSleepTime)
                                    .orderDate(new Date())
                                    .isProxy(tempProxy)
                                    .isNeedLineName(tempLineName)
                                    .isNeedSupplierName(tempSupplierName)
                                    .vessel(vesselName)
                                    .voyage(voyage)
                                    .supplierName(supplierName)
                                    .account(formParams.getAccount())
                                    .etd(formParams.getEtdDays())
                                    .reference(references.get(referenceIndex))
                                    .build();
                            orderDao.saveOrder(order);
                            referenceIndex++;
                            successNum++;
                        } else if (bookId == null) {
//                    小提单号已存在
                            referenceIndex++;
                        }
                        TimeUnit.SECONDS.sleep(formParams.getOrderSleepTime());
                    }
                    //下完单吧下单状态修改为1

                    ScheduleJobEntity scheduleJob = scheduleJobService.selectById(paramsLine.getJobId());
                    formParams.setBooked(1);
                    paramsLine.setParams(formParams.toString());
                    scheduleJob.setParams(formParams.toString());
                    //同时修改定时任务的参数 和 Line的参数
                    scheduleJobService.updateById(scheduleJob);
                    lineService.updateById(paramsLine);
                    sendMail(successNum);
                }
            }

        } catch (NullPointerException nullPointerException) {

        } catch (Exception e) {
            sendErrorMail("程序出现异常了" + e, "程序停止了，请手动重新运行");
        }
    }

    private void updateStatus(String bookId) {
        String url = "https://cetusapi-prod.kontainers.io/booking/api/v1/customer/booking/tick/" + bookId;
        Map<String, String> params = new HashMap<>();
        params.put("Authorization", authorization);

        Map<String, Object> resultMap = new HashMap<>();
        String result = HttpUtils.sendPut(url, resultMap, params, isProxy);

        System.out.println("【确定提交，修改状态】:" + result);
    }

    Map<String, Object> submitBookingsData(Map<String, Object> fclScheduleWithRatesData) {

        Map<String, Object> booking = JSONObject.parseObject(MyUtils.readJsonFile("submitBookData.json"));
        Map<String, Object> info = JSONObject.parseObject(booking.get("booking").toString());
        Map<String, Object> customs = JSONObject.parseObject(info.get("customs").toString());
        List<Map<String, Object>> countryConfigs = new ArrayList<>();
        Map<String, Object> map = new HashMap<>();
        map.put("scac", scac);
        map.put("code", "2");
        map.put("country", "US");
        countryConfigs.add(map);
        customs.put("countryConfigs", countryConfigs);
        info.put("customs", customs);
        info.put("fmc", endPort.getFmc());
        List<Map<String, Object>> referencesTemp;
        String port = formParams.getStartPort().getMatched();
        if ("NANSHA, GUANGDONG".equals(port) || "YANTIAN, GUANGDONG".equals(port) || "XIAMEN, FUJIAN".equals(port)) {
            //只有Yantian和xiamen 是 填空 其他都要填
            referencesTemp = new ArrayList<>();
        } else {
            //可变参数，小提单号
            referencesTemp = JSONObject.parseObject(info.get("references").toString(), new TypeReference<List<Map<String, Object>>>() {
            });
            referencesTemp.get(0).put("reference", references.get(referenceIndex));
        }

        info.put("references", referencesTemp);
        info.put("customerReference", references.get(referenceIndex));

        //可变参数，型号
        List<Map<String, Object>> bookingItems = JSONObject.parseObject(info.get("bookingItems").toString(), new TypeReference<List<Map<String, Object>>>() {
        });
        bookingItems.get(0).put("containerType", equipment);
//        判断数量，是否大于1,一票多个高柜
        if (quantity > 1) {
            for (int i = 1; i < quantity; i++) {
                bookingItems.add(bookingItems.get(0));
            }
        }
        info.put("bookingItems", bookingItems);
        info.put("bookingCosts", fclScheduleWithRatesData.get("bookingCosts"));
        info.put("logisticsDetails", fclScheduleWithRatesData.get("logisticsDetails"));
        info.put("consignorBookingAddress", consignorBookingAddress);
        info.put("bookedByAddress", bookedByAddress);
        info.put("consigneeBookingAddress", consigneeBookingAddress);
        info.put("notifyBookingAddress", notifyBookingAddress);
        if (isNeedBrokerAddress) {
            info.put("brokerageAddress", brokerageAddress);
        }
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("booking", info);
        return resultMap;
    }

    private String submitBookings(Map<String, Object> fclScheduleWithRatesData) {
        String url = "https://cetusapi-prod.kontainers.io/booking/api/v1/customer/bookings";
        Map<String, String> params = new HashMap<>();
        params.put("Authorization", authorization);

//        请求数据处理
        Map<String, Object> resultMap = submitBookingsData(fclScheduleWithRatesData);
        System.out.println("【预提交】请求数据处理:" + resultMap);
//        return null;
        String result = HttpUtils.sendPost(url, resultMap, params, isProxy);

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
    private Map<String, Object> getFclScheduleWithRates() throws ParseException {

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

        String url = "https://cetusapi-prod.kontainers.io/trip-ui/api/v1/customer/fclScheduleWithRates";
        Map<String, String> params = new HashMap<>();
        params.put("Authorization", authorization);


        messageText = "【从" + fclQuoteRequest.get("from") + "到" + fclQuoteRequest.get("to") + "的航线】";


        Map<String, Object> fclScheduleDataResultMap = new HashMap<>();
        fclScheduleDataResultMap.put("fclQuoteRequest", fclQuoteRequest);
        String result = HttpUtils.sendPost(url, fclScheduleDataResultMap, params, false);

        if ("".equals(result)) {
            System.out.println("获取航线接口的数据为空");
            return null;
        }

        JSONObject object = JSONObject.parseObject(result);
        if (object == null) {
            System.out.println("getFclScheduleWithRates  Object为空");
            return null;
        }
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

//                   未显示的航班，过滤掉
                    List<Map<String, Object>> issues = JSONObject.parseObject(legs.get(0).get("issues").toString(), new TypeReference<List<Map<String, Object>>>() {
                    });
                    if (issues.size() > 0) {
                        continue;
                    }
//                   禁止下单的航班过滤掉
                    if (Boolean.parseBoolean(legs.get(0).get("isSoldOut").toString())) {
                        continue;
                    }

                    // 如果航线的终点是 new york 需要制定 其他都不需要
                    if ("NEW YORK, NY".equals(legs.get(0).get("to").toString()) && "NINGBO, ZHEJIANG".equals(legs.get(0).get("from").toString())) {
                        if (!"EAST COAST 2".equals(legs.get(0).get("supplierName").toString())) {
                            continue;
                        }
                    }
                    if (isNeedSupplierName) {
                        if (!supplierName.equals(legs.get(0).get("supplierName").toString())) {
                            continue;
                        }
                    }
                    if (isNeedLineName) {
                        if ((!vesselName.equals(legs.get(0).get("vesselName"))) && !(voyage.equals(legs.get(0).get("voyage")))) {
                            continue;
                        }
                    }
//                   判断航线条件
                    if (etdDays != 0 && maxPrice != 0) {
//                       即判断时间，也判断价格
                        if (MyUtils.dateCompare(dateFormat.parse(orderedDate), dateFormat.parse(legs.get(0).get("etd").toString())) && Float.parseFloat(temp.get("totalCost").toString()) < maxPrice) {
                            multiLegScheduleWithRate = legs.get(0);
                        }
                    } else if (etdDays != 0) {
                        //                  判断时间是否大于orderedDate起始时间
                        if (MyUtils.dateCompare(dateFormat.parse(orderedDate), dateFormat.parse(legs.get(0).get("etd").toString()))) {
                            multiLegScheduleWithRate = legs.get(0);
                        }
                    } else if (maxPrice != 0) {
                        //                       根据钱作为判断,小于满足条件的价格
                        if (Float.parseFloat(temp.get("totalCost").toString()) < maxPrice) {
                            multiLegScheduleWithRate = legs.get(0);
                        }
                    } else {
                        multiLegScheduleWithRate = legs.get(0);
                    }

                }

                if (multiLegScheduleWithRate == null) {
                    String printText = "";
                    if (etdDays != 0) {
                        printText = "【目前最新ETD时间】：" + orderedDate + ";";
                    }
                    if (maxPrice != 0) {
                        printText = printText + "【可接受金额必须小于】：" + maxPrice + ";";
                    }
//                    System.out.println(printText + "【获取航线的接口数据】" + result);
                    return null;
                }

//               保留时间
//               orderedDate = multiLegScheduleWithRate.get("etd").toString();
                return fclScheduleWithRatesHandleData(multiLegScheduleWithRate, supplierSummaries);
            }
        }
//        System.out.println("目前没有可下单的航线");
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

    private void init() {
        startPort = formParams.getStartPort();
        endPort = formParams.getEndPort();
        notifyAddress = formParams.getNotifyAddress();
        consigneeAddress = formParams.getConsigneeAddress();
        Map<String, Object> fclScheduleDataResultMap = JSONObject.parseObject(MyUtils.readJsonFile("fclScheduleData.json"));
        fclQuoteRequest = JSONObject.parseObject(fclScheduleDataResultMap.get("fclQuoteRequest").toString());
        String referencesTemp = formParams.getReferences().replace("，", ",");
        references = Arrays.asList(referencesTemp.split(","));
        etdDays = formParams.getEtdDays();
        maxPrice = formParams.getPrice();
        isNeedLineName = formParams.getIsNeedLineName();
        isNeedSupplierName = formParams.getIsNeedSupplierName();
        if (isNeedLineName) {
            vesselName = formParams.getVesselName();
            voyage = formParams.getVoyage();
        }
        if (isNeedSupplierName) {
            supplierName = formParams.getSupplierName();
        }
        //初始化商品型号和数量
        equipment = formParams.getEquipment();
        quantity = formParams.getQuantity();
        Map<String, Object> map = new HashMap<>(2);
        List<Map<String, Object>> equipmentRequest = new ArrayList<>();
        map.put("equipment", equipment);
        map.put("quantity", quantity);
        equipmentRequest.add(map);
        fclQuoteRequest.put("equipmentRequest", equipmentRequest);
        orderedDate = MyUtils.getNowDate(etdDays, "yyyy-MM-dd");
        isProxy = formParams.getIsProxy();
        orderSleepTime = formParams.getOrderSleepTime();
        fclQuoteRequest.put("schedulesAfterDate", MyUtils.getNowDate(7, "yyyy-MM-dd"));
        authorization = formParams.getAuthorization();
        isNeedBrokerAddress = formParams.getBrokerageAddress();
        scac = formParams.getScac();
        initPort(startPort, endPort);
        getAddress(notifyAddress, consigneeAddress);
    }

    private void initPort(Port startPort, Port endPort) {
        fclQuoteRequest.put("from", startPort.getMatched());
        fclQuoteRequest.put("fromCountry", startPort.getMatchedCountry());
        fclQuoteRequest.put("fromUNCode", startPort.getUnCode());
        if(startPort.getIata() == null){
            fclQuoteRequest.remove("fromIata");
        }else {
            fclQuoteRequest.put("fromIata", startPort.getIata());
        }
        fclQuoteRequest.put("to", endPort.getMatched());
        fclQuoteRequest.put("toCountry", endPort.getMatchedCountry());
        fclQuoteRequest.put("toUNCode", endPort.getUnCode());
        fclQuoteRequest.put("fmc", endPort.getFmc());
        if (endPort.getIata() != null) {
            fclQuoteRequest.put("toIata", endPort.getIata());
        }
    }
    private void stopTask(Long[] ids){
        scheduleJobService.pause(ids);
    }

    private void getToken() {
        String url = "https://cetusapi-prod.kontainers.io/tenancy/api/v1/customer/users/self";
        Map<String, String> params = new HashMap<>();
        params.put("Authorization", authorization);
        params.put("Referer", "https://instantquote.one-line.com/");
        String result = HttpUtils.sendGet(url, null, params, false);
        if ("".equals(result)) {
            System.out.println("getToken result结果为空值 停止任务");
            Long[] ids = new Long[10];
            ids[0] = paramsLine.getJobId();
            stopTask(ids);
            return;
        }
        JSONObject object = JSONObject.parseObject(result);
        if (object == null) {
            System.out.println("getToken object为空");
            return;
        }
        getTokenHandleData(object);
    }

    private void getTokenHandleData(JSONObject object) {
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
        if(formParams.getAccount() == 1){
            addressInfo.put("address3", companyAddress.get("address3").toString());
        }
        addressInfo.put("cityTown", companyAddress.get("cityTown").toString());
        addressInfo.put("country", companyAddress.get("country").toString());
        if (companyAddress.get("vatNo") != null) {
            addressInfo.put("vatNo", companyAddress.get("vatNo").toString());
        }
        addressInfo.put("postCode", companyAddress.get("postCode").toString());
        addressText = addressText + "," + companyAddress.get("address1").toString() + "," + companyAddress.get("address2").toString() + "," + companyAddress.get("cityTown").toString() + "," + companyAddress.get("postCode").toString() + ",CN";
        addressInfo.put("addressText", addressText);
        consignorBookingAddress.putAll(addressInfo);
        bookedByAddress.putAll(addressInfo);

        brokerageAddress.putAll(addressInfo);
        if(authorization != info.get("token").toString()){
            authorization = info.get("token").toString();
            System.out.println("更新authorization" + formParams.getEquipment());
        }
        Long id = formParams.getId();
        if (null != id) {
            formParams.setAuthorization(authorization);
            paramsLine.setParams(formParams.toString());
            lineService.updateById(paramsLine);
            ScheduleJobEntity scheduleJobEntity = scheduleJobService.selectById(paramsLine.getJobId());
            scheduleJobEntity.setParams(paramsLine.getParams());
            scheduleJobService.updateById(scheduleJobEntity);

        }
    }

    private void getAddress(Address notifyAddress, Address consigneeAddress) {
        if (notifyAddress != null && consigneeAddress != null) {
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
            addressMap.put("vatNo", notifyAddress.getVatNo());
            notifyBookingAddress.putAll(addressMap);
            consigneeBookingAddress.putAll(addressMap);
        }
    }

    //发送邮箱
    private void sendMail(int successNum) {
        // 构建一个邮件对象
        SimpleMailMessage message = new SimpleMailMessage();
        // 设置邮件主题
        String accountStr = "";
        if(formParams.getAccount() == 0){
            accountStr = "环集美线";
        }else if (formParams.getAccount() == 1){
            accountStr = "泰博美线";
        }else if(formParams.getAccount() == 2){
            accountStr = "附属账号";
        }
        message.setSubject(messageText + accountStr +"抢单成功");
        // 设置邮件发送者，这个跟application.yml中设置的要一致
        message.setFrom("1059308740@qq.com");
        // 设置邮件接收者，可以有多个接收者，中间用逗号隔开，以下类似
        // message.setTo("10*****16@qq.com","12****32*qq.com");
//        message.setTo("1973432033@qq.com","Zhibo_Tang@zjou.edu.cn","771829811@qq.com"); one-ebooking@nb-hj.com 公司邮箱 ,"1973432033@qq.com","1610531743@qq.com","one-ebooking@nb-hj.com"
        message.setTo("1059308740@qq.com");
        // 设置邮件发送日期
        message.setSentDate(new Date());
        // 设置邮件的正文
        if (referenceIndex > successNum) {
//            代表有小提单号已被使用过
            messageText = messageText + "，实际成功下了" + successNum + "单，其中小提单号有被使用过而下单失败" +
                    "下单的小提单号：【" + String.join("，", references) + "】";
        } else {
            if (isNeedLineName) {
                messageText = messageText + "，成功下了" + successNum + "单。" + "柜子型号：" + equipment + "航名:" + vesselName + "航次:" + voyage + " ETD间隔时间:" + etdDays +
                        "下单的小提单号：【" + String.join("，", references) + "】";
            } else {
                if (isNeedSupplierName) {
                    messageText = messageText + "，成功下了" + successNum + "单。" + "柜子型号：" + equipment + " ETD间隔时间:" + etdDays + " 航线代码" + etdDays +
                            "下单的小提单号：【" + String.join("，", references) + "】";
                } else {
                    messageText = messageText + "，成功下了" + successNum + "单。" + "柜子型号：" + equipment + " ETD间隔时间:" + etdDays +
                            "下单的小提单号：【" + String.join("，", references) + "】";
                }

            }


        }

        message.setText(messageText);
        // 发送邮件
        javaMailSender.send(message);
    }

    //发送邮箱
    private void sendErrorMail(String errorMessage, String title) {
        // 构建一个邮件对象
        SimpleMailMessage message = new SimpleMailMessage();
        // 设置邮件主题
        message.setSubject(title);
        // 设置邮件发送者，这个跟application.yml中设置的要一致
        message.setFrom("1059308740@qq.com");
        // 设置邮件接收者，可以有多个接收者，中间用逗号隔开，以下类似
        // message.setTo("10*****16@qq.com","12****32*qq.com");
//        message.setTo("1973432033@qq.com","Zhibo_Tang@zjou.edu.cn","771829811@qq.com");
        message.setTo("1059308740@qq.com");
        // 设置邮件发送日期
        message.setSentDate(new Date());

        message.setText(messageText + errorMessage);
        // 发送邮件
        javaMailSender.send(message);
    }
}
