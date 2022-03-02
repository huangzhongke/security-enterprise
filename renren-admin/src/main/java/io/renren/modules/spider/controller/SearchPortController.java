package io.renren.modules.spider.controller;


import io.renren.modules.spider.utils.HttpUtils;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * @author kee
 * @version 1.0
 * @date 2022/2/16 16:13
 */
@CrossOrigin(origins = {"http://localhost:8001"})
@RestController
@RequestMapping("/one")
public class SearchPortController {


    @GetMapping("startPort")
    public String getStartPort(@RequestParam Map<String, Object> params) {
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("fromCountry", "");
        paramMap.put("from", "");
        paramMap.put("loadType", "fcl");
        String startPort = params.get("startPort").toString();
        if ("".equals(startPort)) {
            return null;
        }
        paramMap.put("query", startPort);
        String url = "https://cetusapi-prod.kontainers.io/trip-ui/api/v1/customer/locations";
        Map<String, String> headers = new HashMap<>();
        headers.put("Authorization", params.get("authorization").toString());
        headers.put("Referer", "https://instantquote.one-line.com/");
        String result = HttpUtils.sendGet(url, paramMap, headers, false);
        return result;

    }

    @GetMapping("endPort")
    public String getEndPort(@RequestParam Map<String, Object> params) {
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("fromCountry", params.get("matchedCountry").toString());
        paramMap.put("from", params.get("matched").toString());
        paramMap.put("pickupMode", params.get("mode").toString());
        paramMap.put("loadType", "fcl");
        String endPort = params.get("query").toString();
        if ("".equals(endPort)) {
            return null;
        }
        paramMap.put("query", endPort);
        String url = "https://cetusapi-prod.kontainers.io/trip-ui/api/v1/customer/locations";
        Map<String, String> headers = new HashMap<>();
        headers.put("Authorization", params.get("authorization").toString());
        headers.put("Referer", "https://instantquote.one-line.com/");
        String result = HttpUtils.sendGet(url, paramMap, headers, false);
        return result;

    }

    @GetMapping("addresses")
    public String getAddress(@RequestParam Map<String, Object> params) {
        String url = "https://cetusapi-prod.kontainers.io/tenancy/api/v1/customer/addresses";
        Map<String, String> map = new HashMap<>();
        Map<String, String> headers = new HashMap<>();
        headers.put("Authorization", params.get("authorization").toString());
        headers.put("Referer", "https://instantquote.one-line.com/");
        map.put("page", "0");
        map.put("size", "25");
        map.put("typeahead", params.get("query").toString());
        String response = HttpUtils.sendGet(url, map, headers, false);
        return response;
    }

}
