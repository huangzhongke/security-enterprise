package io.renren.modules.job.task;

import cn.hutool.json.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import io.renren.modules.job.entity.ScheduleJobEntity;
import io.renren.modules.job.service.ScheduleJobService;
import io.renren.modules.spider.menu.entity.Account;
import io.renren.modules.spider.menu.entity.Line;
import io.renren.modules.spider.menu.service.AccountService;
import io.renren.modules.spider.one.entity.FormParams;
import io.renren.modules.spider.one.entity.Port;
import io.renren.modules.spider.one.service.LineService;
import io.renren.modules.spider.oocl.dto.OOCLDataFormDTO;
import io.renren.modules.spider.oocl.entity.ChildAccount;
import io.renren.modules.spider.oocl.service.ChildAccountService;
import io.renren.modules.spider.utils.LoginUtils;
import io.renren.modules.spider.utils.MyUtils;
import io.renren.modules.spider.utils.OclUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * @author kee
 * @version 1.0
 * @date 2022/7/6 16:45
 */
@Component("oocl")
public class OOCL implements ITask {
    @Autowired
    AccountService accountService;
    @Autowired
    LineService lineService;
    @Autowired
    ChildAccountService childAccountService;
    @Autowired
    JavaMailSender javaMailSender;
    @Autowired
    ScheduleJobService scheduleJobService;
    private SimpleDateFormat sdf = new SimpleDateFormat("YYYY-MM-dd HH:mm");

    @Override
    public void run(String params) {
        int successNumber = 0;
        String errorMsg;

        SimpleDateFormat sdf2 = new SimpleDateFormat("YYYY-MM-dd HH:mm:ss");
        OOCLDataFormDTO dataFormDTO = JSONObject.parseObject(params, OOCLDataFormDTO.class);
        Line line = lineService.getByJobId(dataFormDTO.getId());
        ScheduleJobEntity scheduleJobEntity = scheduleJobService.selectById(line.getJobId());
        Account orderAccount = accountService.getAccountByUserName(dataFormDTO.getAccount());
        Map<String, Object> accountMap = new HashMap<>();
        accountMap.put("username", orderAccount.getUser());
        accountMap.put("password", orderAccount.getPassword());
        List<ChildAccount> childAccounts = childAccountService.getListByLineId(line.getId());

        String title = "【" + dataFormDTO.getStartPort().getCityFullNameCn() + "-" + dataFormDTO.getStartPort().getCityFullNameEn()
                + "】 —— 【" + dataFormDTO.getEndPort().getCityFullNameCn() + "-" + dataFormDTO.getEndPort().getCityFullNameEn() +
                "】航名：" + dataFormDTO.getVesselName() + " 航次：" + dataFormDTO.getVoyage() + " 箱型：" + dataFormDTO.getEquipment() + " 数量:" + dataFormDTO.getQuantity();

        //
        try {
            for (ChildAccount childAccount : childAccounts) {
                // 如果子账号未登录 就先登录
                if (childAccount.getIp() == null && !childAccount.getStatus()) {
                    String username = childAccount.getUsername();
                    Account childAccountByUserName = accountService.getAccountByUserName(username);
                    Map<String, Object> map = new HashMap<>();
                    map.put("username", childAccountByUserName.getUser());
                    map.put("password", childAccountByUserName.getPassword());
                    String ip = MyUtils.getIp();
                    System.out.println("当前登录的账号是：" + username);
                    Map<String, Object> login = LoginUtils.Login(map, ip);
                    if (login != null) {
                        System.out.println(username + "登录成功");
                        childAccount.setCookie(login.get("cookie").toString());
                        childAccount.setToken(login.get("token").toString());
                        childAccount.setIp(ip);
                        childAccount.setStatus(true);
                        childAccountService.updateById(childAccount);

                    } else {
                        System.out.println(username + "登录失败");
                    }
                    TimeUnit.MILLISECONDS.sleep(1000);
                }
                // 如果已经登录子账号去搜索
                if (childAccount.getStatus()) {
                    // ip 有效
                    if (!MyUtils.testIpExceed(childAccount.getIp())) {
                        // OclUtils.ip = childAccount.getIp();
                        List<Map<String, Object>> content = new ArrayList<>();
                        try {
                            System.out.println("当前时间是:" + sdf2.format(new Date()) + " 账号:" + childAccount.getUsername() + "目前正在搜索的是：" + title);
                            content = getContent(dataFormDTO, childAccount);
                        } catch (NullPointerException e) {
                            System.out.println("content 空指针");
                        }
                        if (null != content && content.size() > 0) {
                            Map<String, Object> fitOrderMap = judgeIsNeedLine(content, dataFormDTO);
                            if (fitOrderMap != null) {
                                System.out.println("当前时间:" + sdf2.format(new Date()) + "搜寻到符合下单的航线开始下单");
                                // status 默认false 表示未下单
                                if (!line.getOrderStatus()) {
                                    List<Map<String, Object>> searchFeeList = getSearchFeeData(fitOrderMap);
                                    Map<String, Object> orderMap = searchFee(searchFeeList, dataFormDTO, childAccount, fitOrderMap);
                                    // 下单前判断价格
                                    int price = Integer.parseInt(orderMap.get("price").toString());
                                    if (dataFormDTO.getPrice() != 0) {
                                        if (price > dataFormDTO.getPrice()) {
                                            System.out.println("该航线能下单但是价格不符合需求");
                                            sendMail(title, "价格过高不符合下单要求, 该条航线价格为:" + price);
                                            return;
                                        }
                                    }
                                    orderMap.remove("price");
                                    Map<String, Object> orderAccountMap = new HashMap<>();
                                    if (MyUtils.testIpExceed(orderAccount.getAgentIp())) {
                                        // 如果ip过期
                                        String ip = MyUtils.getIp();
                                        Map<String, Object> loginMap = LoginUtils.Login(accountMap, ip);
                                        if (loginMap != null) {
                                            orderAccount.setAgentIp(ip);
                                            accountService.updateById(orderAccount);
                                            orderAccountMap = loginMap;
                                            dataFormDTO.setToken(loginMap.get("token").toString());
                                            dataFormDTO.setCookie(loginMap.get("cookie").toString());
                                            scheduleJobEntity.setParams(dataFormDTO.toString());
                                            scheduleJobService.updateById(scheduleJobEntity);

                                        } else {
                                            System.out.println("主账号登录失败无法下单");
                                            return;
                                        }

                                    } else {
                                        orderAccountMap.put("cookie", dataFormDTO.getCookie());
                                        orderAccountMap.put("token", dataFormDTO.getToken());
                                    }

                                    for (Integer i = 0; i < dataFormDTO.getQuantity(); i++) {
                                        // 切换主账号下单 支付
                                        Map<String, Object> payDataMap = getOrder(orderMap, orderAccountMap, orderAccount);
                                        if (payDataMap != null) {
                                            Map<String, Object> resultMap = pay(payDataMap, orderAccountMap, orderAccount);
                                            //
                                            if (resultMap.get("orderInfoResultMap") != null) {
                                                System.out.println(resultMap.get("orderInfoResultMap").toString());
                                                successNumber++;
                                            } else {
                                                System.out.println(resultMap);
                                                errorMsg = resultMap.get("error") + "   " + resultMap.get("message") + ",status:" + resultMap.get("status");
                                                sendMail(title + "下单失败", "失败原因：" + errorMsg);
                                            }
                                        }

                                    }
                                    line.setOrderStatus(true);
                                    lineService.updateById(line);
                                    stopTask(dataFormDTO.getId());
                                    if (successNumber != 0) {
                                        sendMail(title + "下单成功", "一共下了" + successNumber + "单");
                                    }
                                }
                            } else {
                                System.out.println("当前子账号没有搜寻到符合需求的航线");
                            }
                        } else {
                            System.out.println("content 搜索异常");
                        }
                        try {
                            int times = (int) (Math.random() * 400 + 800);
                            System.out.println("当前时间：" + sdf2.format(new Date()) + "开始休眠时间：" + times + "秒");
                            TimeUnit.MILLISECONDS.sleep(times);
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                    }
                    // ip 失效 就换 ip
                    else {
                        String ip = MyUtils.getIp();
                        childAccount.setIp(ip);
                        childAccountService.updateById(childAccount);
                    }

                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void stopTask(Long id) {
        Long[] ids = new Long[10];
        ids[0] = id;
        scheduleJobService.pause(ids);
    }

    private Map<String, Object> pay(Map<String, Object> payDataMap, Map<String, Object> orderAccountMap, Account orderAccount) {
        String url = "https://freightsmart.oocl.com/api/product/client/order-payment/pay";

        Map<String, Object> payMap = JSONObject.parseObject(MyUtils.readJsonFile("oocl/pay.json"));
        Map<String, Object> accountInfo = JSONObject.parseObject(payMap.get("accountInfo").toString());
        Map<String, Object> sailingProdOrderInfo = JSONObject.parseObject(payMap.get("sailingProdOrderInfo").toString());
        accountInfo.put("password", orderAccount.getPayPassword());
        sailingProdOrderInfo.put("orderNo", payDataMap.get("orderNo"));
        sailingProdOrderInfo.put("amount", payDataMap.get("amount"));
        sailingProdOrderInfo.put("paymentOrderType", payDataMap.get("orderType"));
        payMap.put("accountInfo", accountInfo);
        payMap.put("sailingProdOrderInfo", sailingProdOrderInfo);

        Map<String, String> headers = new HashMap<>();
        headers.put("Referer", "https://freightsmart.oocl.com/order/unpaidTotalCharge?orderNo=" + payDataMap.get("orderNo"));
        headers.put("Cookie", orderAccountMap.get("cookie").toString());
        headers.put("X-Auth-Token", orderAccountMap.get("token").toString());

        String response = OclUtils.sendPut(url, payMap, headers,orderAccount.getAgentIp());
        Map<String, Object> responseMap = JSONObject.parseObject(response);
        return responseMap;
    }

    private Map<String, Object> getOrder(Map<String, Object> orderMap, Map<String, Object> orderAccountMap, Account orderAccount) {
        String url = "https://freightsmart.oocl.com/api/product/client/order";
        Map<String, String> headers = new HashMap<>();
        headers.put("Referer", "https://freightsmart.oocl.com/product/order/preview");
        headers.put("Cookie", orderAccountMap.get("cookie").toString());
        headers.put("X-Auth-Token", orderAccountMap.get("token").toString());
        String response = OclUtils.sendPost(url, orderMap, headers,orderAccount.getAgentIp());
        Map<String, Object> responseMap = JSONObject.parseObject(response);
        Map<String, Object> sailingProdMap = getPriceDetail(responseMap, orderAccountMap,orderAccount);
        return sailingProdMap;
    }

    private Map<String, Object> getPriceDetail(Map<String, Object> responseMap, Map<String, Object> orderAccountMap,Account orderAccount) {
        String url = "https://freightsmart.oocl.com/api/product/client/order-payment/detail";
        Map<String, Object> paramsMap = new HashMap<>();
        Map<String, String> headers = new HashMap<>();
        paramsMap.put("productOrderType", "SAILING_PROD");
        paramsMap.put("orderNo", responseMap.get("orderNo"));
        headers.put("Referer", "https://freightsmart.oocl.com/order/unpaidTotalCharge?orderNo=" + responseMap.get("orderNo"));
        headers.put("Cookie", orderAccountMap.get("cookie").toString());
        headers.put("X-Auth-Token", orderAccountMap.get("token").toString());
        String result = OclUtils.sendGet(url, paramsMap, headers,orderAccount.getAgentIp());
        Map<String, Object> resultMap = JSONObject.parseObject(result);
        Map<String, Object> sailingMap = JSONObject.parseObject(resultMap.get("SAILING_PROD").toString());
        return sailingMap;
    }

    private Map<String, Object> searchFee(List<Map<String, Object>> searchFeeList, OOCLDataFormDTO dataFormDTO, ChildAccount childAccount, Map<String, Object> fitOrderMap) {
        String url = "https://freightsmart.oocl.com/api/common/feeUpload/searchfee";
        Map<String, String> headers = new HashMap<>();
        headers.put("Cookie", childAccount.getCookie());
        headers.put("X-Auth-Token", childAccount.getToken());
        headers.put("Referer", "https://freightsmart.oocl.com/product/order/pre-booking/detail" + fitOrderMap.get("id"));
        JSONArray searchFeeJSONArr = new JSONArray();
        searchFeeJSONArr.add(searchFeeList.get(0));
        String response = OclUtils.sendPost(url, headers, searchFeeJSONArr,childAccount.getIp());
        if (response.equals("")) {
            return null;
        } else {
            List<Map<String, Object>> responseList = JSONObject.parseObject(response, new TypeReference<List<Map<String, Object>>>() {
            });
            return handleSearchFeeData(responseList, dataFormDTO, fitOrderMap);
        }
    }

    private Map<String, Object> handleSearchFeeData(List<Map<String, Object>> responseList, OOCLDataFormDTO dataFormDTO, Map<String, Object> fitOrderMap) {
        Integer price = 0;
        Map<String, Object> orderTemp = JSONObject.parseObject(MyUtils.readJsonFile("oocl/order.json"));
        Map<String, Object> order = JSONObject.parseObject(orderTemp.get("order").toString());
        Map<String, Object> key1 = responseList.get(0);
        List<Map<String, Object>> chargeInfo = JSONObject.parseObject(key1.get("chargeInfo").toString(), new TypeReference<List<Map<String, Object>>>() {
        });
        // 用来存放order中的orderItems
        List<Map<String, Object>> orderItems = new ArrayList<>();

        // 各个箱型价格的map
        Map<String, Object> containerOceanFeeMap = JSONObject.parseObject(fitOrderMap.get("containerOceanFeeMap").toString());

        for (Map<String, Object> map : chargeInfo) {
            Map<String, Object> addMap = new HashMap<>();
            if (map.get("chargeModel").equals("BL")) {
                addMap.put("itemType", map.get("chargeModel"));
                addMap.put("quantity", 1);
            } else {
                addMap.put("itemType", map.get("chargeModel"));
                addMap.put("cntrType", map.get("cntrSize"));
                if (dataFormDTO.getEquipment().equals(map.get("cntrSize").toString())) {
                    addMap.put("quantity", 1);
                } else {
                    addMap.put("quantity", 0);
                    addMap.put("cargoValue", 0);
                }
            }
            List<Map<String, Object>> chargeDetails = JSONObject.parseObject(map.get("chargeDetail").toString(), new TypeReference<List<Map<String, Object>>>() {
            });
            List<Map<String, Object>> orderItemCharges = new ArrayList<>();
            if (map.get("cntrSize") != null) {
                Map<String, Object> oceanFeeMap = new HashMap<>();
                oceanFeeMap.put("chargeName", "海运费");
                oceanFeeMap.put("chargeCode", null);
                oceanFeeMap.put("chargeType", "OCEAN_FEE");
                oceanFeeMap.put("currency", "USD");
                oceanFeeMap.put("toCurrency", "USD");
                oceanFeeMap.put("transitPortId", null);
                oceanFeeMap.put("paymentTermsType", "P");
                oceanFeeMap.put("price", containerOceanFeeMap.get(map.get("cntrSize").toString()));
                orderItemCharges.add(oceanFeeMap);
            }
            for (Map<String, Object> chargeDetail : chargeDetails) {
                if (chargeDetail.get("paymentTerms").toString().equals("C")) {
                    continue;
                }
                Map<String, Object> temp = new HashMap<>();
                temp.put("chargeName", chargeDetail.get("chargeName"));
                temp.put("chargeCode", chargeDetail.get("chargeCode"));
                temp.put("chargeType", chargeDetail.get("chargeType"));
                temp.put("chargeHisId", chargeDetail.get("chargeHisId"));
                temp.put("price", Integer.parseInt(chargeDetail.get("price").toString()));
                temp.put("currency", chargeDetail.get("currency"));
                temp.put("toCurrency", chargeDetail.get("currency"));
                temp.put("transitPortId", null);
                temp.put("paymentTermsType", chargeDetail.get("paymentTerms"));
                temp.put("include", chargeDetail.get("include").toString());
                orderItemCharges.add(temp);
            }
            addMap.put("orderItemCharges", orderItemCharges);
            orderItems.add(addMap);
        }
        order.put("prodId", fitOrderMap.get("id"));
        order.put("tradeLaneCode", fitOrderMap.get("tradeLaneCode"));
        order.put("channelCode", fitOrderMap.get("channelCode"));
        order.put("cargoCategory", fitOrderMap.get("cargoType"));
        order.put("orderItems", orderItems);
        orderTemp.put("order", order);
        // 计算价格
        for (String type : containerOceanFeeMap.keySet()) {
            if (type.equals(dataFormDTO.getEquipment())) {
                price += Integer.parseInt(containerOceanFeeMap.get(dataFormDTO.getEquipment()).toString());
            }
        }
        for (Map<String, Object> map : chargeInfo) {
            if (map.get("chargeModel").toString().equals("BL")) {
                List<Map<String, Object>> chargeDetails = JSONObject.parseObject(map.get("chargeDetail").toString(), new TypeReference<List<Map<String, Object>>>() {
                });
                for (Map<String, Object> chargeDetail : chargeDetails) {
                    if ("P".equals(chargeDetail.get("paymentTerms").toString()) && "USD".equals(chargeDetail.get("currency").toString())) {
                        price += Integer.parseInt(chargeDetail.get("price").toString());

                    }
                }
            }
            Object cntrSize = map.get("cntrSize");
            if (cntrSize != null) {
                if (cntrSize.toString().equals(dataFormDTO.getEquipment())) {
                    List<Map<String, Object>> chargeDetails = JSONObject.parseObject(map.get("chargeDetail").toString(), new TypeReference<List<Map<String, Object>>>() {
                    });
                    for (Map<String, Object> chargeDetail : chargeDetails) {
                        if ("P".equals(chargeDetail.get("paymentTerms").toString()) && "USD".equals(chargeDetail.get("currency").toString())) {
                            price += Integer.parseInt(chargeDetail.get("price").toString());

                        }
                    }
                }
            }
        }
        orderTemp.put("price", price);
        return orderTemp;
    }

    private Map<String, Object> judgeIsNeedLine(List<Map<String, Object>> content, OOCLDataFormDTO dataFormDTO) {
        Map<String, Object> fitLineMap = null;
        for (Map<String, Object> map : content) {
            List<Map<String, Object>> sailingProductDTOS = JSONObject.parseObject(map.get("sailingProductDTOS").toString(), new TypeReference<List<Map<String, Object>>>() {
            });
            for (Map<String, Object> sailingProductDTO : sailingProductDTOS) {
                int inventory = calculateTeu(dataFormDTO.getEquipment());
                if ((int) sailingProductDTO.get("inventory") < inventory) {
                    continue;
                }
                if (dataFormDTO.getIsNeedVesselName()) {
                    if (sailingProductDTO.get("vesselName").equals(dataFormDTO.getVesselName()) && sailingProductDTO.get("voyageNo").equals(dataFormDTO.getVoyage())) {
                        fitLineMap = sailingProductDTO;
                        break;
                    }
                } else {
                    fitLineMap = sailingProductDTO;
                }
            }
        }
        if (fitLineMap != null) {
            return fitLineMap;
        }
        return null;
    }

    private List<Map<String, Object>> getSearchFeeData(Map<String, Object> fitLineMap) {
        String searchFeeStr = MyUtils.readJsonFile("oocl/searchfee.json");
        List<Map<String, Object>> searchFeeList = JSONObject.parseObject(searchFeeStr, new TypeReference<List<Map<String, Object>>>() {
        });
        Map<String, Object> key1 = searchFeeList.get(0);
        JSONObject porCity = JSONObject.parseObject(fitLineMap.get("porCity").toString());
        JSONObject podPort = JSONObject.parseObject(fitLineMap.get("podPort").toString());
        JSONObject fndCity = JSONObject.parseObject(fitLineMap.get("fndCity").toString());
        JSONObject containerOceanFeeMap = JSONObject.parseObject(fitLineMap.get("containerOceanFeeMap").toString());
        List<Map<String, Object>> cntrInfo = new ArrayList<>();
        for (String key : containerOceanFeeMap.keySet()) {
            Map<String, Object> temp = new HashMap<>();
            temp.put("cntrSize", key);
            temp.put("amount", 1);
            cntrInfo.add(temp);
        }
        String effectiveTime = MyUtils.changeTimeFormat(fitLineMap.get("ltd").toString(), "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        key1.put("effectiveTime", effectiveTime);
        key1.put("tradeAreaCode", fitLineMap.get("tradeArea"));
        key1.put("tradeLaneCode", fitLineMap.get("tradeLaneCode"));
        key1.put("svcLoopCode", fitLineMap.get("serviceCode"));
        key1.put("direction", fitLineMap.get("direction"));
        key1.put("areaCode", fitLineMap.get("areaCode"));
        key1.put("regionCode", fitLineMap.get("regionCode"));
        key1.put("por", porCity.get("id"));
        key1.put("pod", podPort.get("id"));
        key1.put("fnd", fndCity.get("id"));
        key1.put("channelCode", fitLineMap.get("channelCode"));
        key1.put("cargoCategory", fitLineMap.get("cargoType"));
        key1.put("transhipmentPortIds", new ArrayList<>());
        List<Map<String, Object>> list = new ArrayList<>();
        list.add(key1);
        return list;
    }

    private List<Map<String, Object>> getContent(OOCLDataFormDTO dataFormDTO, ChildAccount childAccount) {
        Map<String, Object> listMap = JSONObject.parseObject(MyUtils.readJsonFile("oocl/list.json"));
        String url = "https://freightsmart.oocl.com/api/product/client/prebooking/group/list";
        Map<String, String> headers = new HashMap<>();
        listMap.put("startDate", sdf.format(dataFormDTO.getStartDate()));
        if (dataFormDTO.getEndDate() != null) {
            listMap.put("endDate", sdf.format(dataFormDTO.getEndDate()));
        }

        List<String> fndCityIdsList = new ArrayList<>();
        List<String> porCityIds = new ArrayList<>();
        fndCityIdsList.add(dataFormDTO.getEndPort().getId());
        porCityIds.add(dataFormDTO.getStartPort().getId());
        listMap.put("fndCityIds", fndCityIdsList);
        listMap.put("porCityIds", porCityIds);
        if (dataFormDTO.getIsColdType()){
            listMap.put("cargoCategory","NOR");
            listMap.put("channelCode","REEFER");
        }
        headers.put("Cookie", childAccount.getCookie());
        headers.put("X-Auth-Token", childAccount.getToken());
        headers.put("Referer", "https://freightsmart.oocl.com/prebooking");
        String result = OclUtils.sendPost(url, listMap, headers,childAccount.getIp());
        if (result.contains("meta") || "".equals(result)) {
            return null;
        }
        Map<String, Object> resultMap = JSONObject.parseObject(result);
        return JSONObject.parseObject(resultMap.get("content").toString(), new TypeReference<List<Map<String, Object>>>() {
        });
    }

    private int calculateTeu(String type) {
        switch (type) {
            case "20GP":
                return 1;
            case "40GP":
            case "40HQ":
                return 2;
        }
        return 0;
    }


    private void sendMail(String title, String text) {
        // 构建一个邮件对象
        SimpleMailMessage message = new SimpleMailMessage();
        // 设置邮件主题
        message.setSubject(title);
        // 设置邮件发送者，这个跟application.yml中设置的要一致
        message.setFrom("1059308740@qq.com");

//        message.setTo("1973432033@qq.com","Zhibo_Tang@zjou.edu.cn","771829811@qq.com"); one-ebooking@nb-hj.com 公司邮箱 "
        message.setTo("1059308740@qq.com");
        // 设置邮件发送日期
        message.setSentDate(new Date());
        // 设置邮件的正文

        message.setText(text);
        // 发送邮件
        javaMailSender.send(message);
    }
}
