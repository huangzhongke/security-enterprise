package io.renren.modules.job.task;

import com.alibaba.fastjson.JSONObject;
import io.renren.modules.spider.oocl.dto.OOCLDataFormDTO;
import org.springframework.stereotype.Component;

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

    private SimpleDateFormat sdf = new SimpleDateFormat("YYYY-MM-dd HH:mm");

    @Override
    public void run(String params) {

        OOCLDataFormDTO dataFormDTO = JSONObject.parseObject(params, OOCLDataFormDTO.class);
        Line line = lineService.getByJobId(dataFormDTO.getId());
        // 1 查出子账号
        List<ChildAccount> childAccounts = childAccountService.getListByLineId(line.getId());

        for (ChildAccount childAccount : childAccounts) {
            OclUtils.ip = childAccount.getIp();
            List<Map<String, Object>> content = content(dataFormDTO, childAccount);
            if (content.size() > 0) {
                Map<String, Object> fitOrderMap = judgeIsNeedLine(content, dataFormDTO);
                if (fitOrderMap != null) {
                    // status 默认false 表示未下单
                    if (!line.getOrderStatus()) {
                        List<Map<String, Object>> searchFeeList = getSearchFeeData(fitOrderMap);
                        Map<String, Object> orderMap = searchFee(searchFeeList, dataFormDTO, childAccount, fitOrderMap);
                        // 现在表示可以下单
                        for (Integer i = 0; i < dataFormDTO.getQuantity(); i++) {
                            Map<String,Object>  payDataMap = getOrder(orderMap,childAccount);
                            Map<String,Object> resultMap = pay(payDataMap,childAccount);
                            //
                        }
                        line.setOrderStatus(true);
                        lineService.updateById(line);
                    }
                } else {
                    System.out.println("当前子账号没有搜寻到结果");
                }
            }
        }
        // 搜寻到的航线结果
    }

    private Map<String, Object> pay(Map<String, Object> payDataMap, ChildAccount childAccount) {
        return null;
    }

    private Map<String, Object> getOrder(Map<String, Object> orderMap,ChildAccount childAccount) {
        String url = "https://freightsmart.oocl.com/api/product/client/order";
        Map<String, String> headers = new HashMap<>();
        headers.put("Referer","https://freightsmart.oocl.com/product/order/preview");
        headers.put("Cookie",childAccount.getCookie());
        headers.put("X-Auth-Token",childAccount.getToken());
        return null;
    }

    private Map<String, Object> searchFee(List<Map<String, Object>> searchFeeList, OOCLDataFormDTO dataFormDTO, ChildAccount childAccount, Map<String, Object> fitOrderMap) {
        String url = "https://freightsmart.oocl.com/api/common/feeUpload/searchfee";
        Map<String, String> headers = new HashMap<>();
        headers.put("Cookie", childAccount.getCookie());
        headers.put("X-Auth-Token", childAccount.getToken());
        headers.put("Referer", "https://freightsmart.oocl.com/product/order/pre-booking/detail" + fitOrderMap.get("id"));
        JSONArray searchFeeJSONArr = new JSONArray();
        searchFeeJSONArr.add(searchFeeList.get(0));
        String response = OclUtils.sendPost(url, headers, searchFeeJSONArr);
        if (response.equals("")) {
            return null;
        } else {
            List<Map<String, Object>> responseList = JSONObject.parseObject(response, new TypeReference<List<Map<String, Object>>>() {
            });
            return handleSearchFeeData(responseList, dataFormDTO, fitOrderMap);
        }
    }

    private Map<String, Object> handleSearchFeeData(List<Map<String, Object>> responseList, OOCLDataFormDTO dataFormDTO, Map<String, Object> fitOrderMap) {
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
                if (sailingProductDTO.get("vesselName").equals(dataFormDTO.getVesselName()) && sailingProductDTO.get("voyageNo").equals(dataFormDTO.getVoyage())) {
                    fitLineMap = sailingProductDTO;
                    break;
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

    private List<Map<String, Object>> content(OOCLDataFormDTO dataFormDTO, ChildAccount childAccount) {
        Account account = accountService.getAccountByUserName(dataFormDTO.getAccount());
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
        headers.put("Cookie", childAccount.getCookie());
        headers.put("X-Auth-Token", childAccount.getToken());
        headers.put("Referer", "https://freightsmart.oocl.com/prebooking");
        String result = OclUtils.sendPost(url, listMap, headers);
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
}
