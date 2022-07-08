package io.renren.modules.job.task;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import io.renren.modules.spider.menu.entity.Account;
import io.renren.modules.spider.menu.service.AccountService;
import io.renren.modules.spider.oocl.dto.OOCLDataFormDTO;
import io.renren.modules.spider.utils.HttpUtils;
import io.renren.modules.spider.utils.MyUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author kee
 * @version 1.0
 * @date 2022/7/6 16:45
 */
@Component("oocl")
public class OOCL implements ITask {
    @Autowired
    AccountService accountService;

    private SimpleDateFormat sdf = new SimpleDateFormat("YYYY-MM-dd HH:mm");

    @Override
    public void run(String params) {
        OOCLDataFormDTO dataFormDTO = JSONObject.parseObject(params, OOCLDataFormDTO.class);
        //搜寻到的航线结果
        List<Map<String, Object>> content = content(dataFormDTO);
        if (content.size() > 0) {
            Map<String, Object> orderLine = judgeIsNeedLine(content,dataFormDTO);
            if (orderLine != null){

            }else {
                System.out.println("当前搜索结果没有符合需求的航线。");
            }
        }
        System.out.println("当先航线数据列表为空");
    }

    private Map<String, Object> judgeIsNeedLine(List<Map<String, Object>> content,OOCLDataFormDTO dataFormDTO) {
        for (Map<String, Object> map : content) {
            Map<String, Object> fitLineMap = null;
            List<Map<String, Object>> sailingProductDTOS = JSONObject.parseObject(map.get("sailingProductDTOS").toString(),new TypeReference<List<Map<String, Object>>>(){});
            for (Map<String, Object> sailingProductDTO : sailingProductDTOS) {
                    if ((int)sailingProductDTO.get("inventory") == 0){
                        continue;
                    }
                    if(sailingProductDTO.get("vesselName").equals(dataFormDTO.getVesselName()) && sailingProductDTO.get("voyageNo").equals(dataFormDTO.getVoyage())){
                        fitLineMap = map;
                        break;
                    }
            }
        }
        //handlerLineData(fitLineMap);
        return null;
    }

    private List<Map<String, Object>> content(OOCLDataFormDTO dataFormDTO) {
        Account account = accountService.getAccountByUserName(dataFormDTO.getAccount());
        Map<String, Object> ip = JSONObject.parseObject(account.getAgentIp());
        Map<String, Object> listMap = JSONObject.parseObject(MyUtils.readJsonFile("ooc/list.json"));
        String url = "https://freightsmart.oocl.com/api/product/client/prebooking/group/list";
        Map<String, String> headers = new HashMap<>();
        listMap.put("startDate", sdf.format(dataFormDTO.getStartDate()));
        listMap.put("endDate", sdf.format(dataFormDTO.getEndDate()));
        List<String> fndCityIdsList = new ArrayList<>();
        List<String> porCityIds = new ArrayList<>();
        fndCityIdsList.add(dataFormDTO.getEndPort().getId());
        porCityIds.add(dataFormDTO.getStartPort().getId());
        listMap.put("fndCityIds", fndCityIdsList);
        listMap.put("porCityIds", porCityIds);
        headers.put("Cookie", dataFormDTO.getCookie());
        headers.put("X-Auth-Token", dataFormDTO.getToken());
        headers.put("Referer", "https://freightsmart.oocl.com/prebooking");
        String result = HttpUtils.sendPost(url, listMap, headers, ip);
        return JSONObject.parseObject(result, new TypeReference<List<Map<String, Object>>>() {
        });
    }
}
