package io.renren.modules.job.task;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import io.renren.modules.job.dto.ScheduleJobDTO;
import io.renren.modules.job.entity.ScheduleJobEntity;
import io.renren.modules.job.service.ScheduleJobService;
import io.renren.modules.spider.menu.dao.AccountDao;
import io.renren.modules.spider.menu.entity.Account;
import io.renren.modules.spider.menu.service.AccountService;
import io.renren.modules.spider.one.service.LineService;
import io.renren.modules.spider.oocl.dao.MonitorDataDao;
import io.renren.modules.spider.oocl.dto.MonitorDTO;
import io.renren.modules.spider.oocl.dto.OOCLDataFormDTO;
import io.renren.modules.spider.oocl.entity.ChildAccount;
import io.renren.modules.spider.oocl.entity.MonitorData;
import io.renren.modules.spider.oocl.service.ChildAccountService;
import io.renren.modules.spider.oocl.service.MonitorDataService;
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

@Component("monitor")
public class Monitor implements ITask {
    @Autowired
    MonitorDataService monitorDataService;
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
    @Autowired
    AccountDao accountDao;
    private SimpleDateFormat sdf = new SimpleDateFormat("YYYY-MM-dd HH:mm:ss");

    @Override
    public void run(String params) {

        try {

            MonitorDTO monitorDTO = null;
            monitorDTO = JSONObject.parseObject(params, MonitorDTO.class);
            String username = monitorDTO.getAccount();
            Account account = accountService.getAccountByUserName(username);
            ScheduleJobEntity scheduleJobEntity = scheduleJobService.selectById(monitorDTO.getId());
            monitorDTO = JSONObject.parseObject(scheduleJobEntity.getParams(), MonitorDTO.class);
            boolean flag = monitorDTO.getFlag();
            // ?????? ?????????ip ???????????????????????????
            if (account.getAgentIp() == null) {
                // ??????ip???null ????????????
                changeIp(account);
            }
            if (MyUtils.testIpExceed(account.getAgentIp())) {
                // ??????ip?????? ????????????
                changeIp(account);
            }
            List<Map<String, Object>> content = getContent(monitorDTO, account);
            if (content != null && content.size() > 0) {
                judgeInventory(content, monitorDTO, flag);
                if (!flag) {
                    monitorDTO.setFlag(true);
                    scheduleJobEntity.setParams(JSONObject.toJSONString(monitorDTO));
                    scheduleJobService.updateById(scheduleJobEntity);
                }
            } else {
                System.out.println("content ???null");
            }

        } catch (Exception e) {
            System.out.println(e);
        }
    }

    private void changeIp(Account account) {
        String ip = MyUtils.getIp();
        account.setAgentIp(ip);
        accountDao.updateById(account);
    }

    private List<Map<String, Object>> getContent(MonitorDTO monitorDTO, Account account) {
        Map<String, Object> listMap = JSONObject.parseObject(MyUtils.readJsonFile("oocl/list.json"));
        String url = "https://freightsmart.oocl.com/api/product/client/prebooking/group/list";
        Map<String, String> headers = new HashMap<>();
        listMap.put("startDate", sdf.format(monitorDTO.getStartDate()));
        if (monitorDTO.getEndDate() != null) {
            listMap.put("endDate", sdf.format(monitorDTO.getEndDate()));
        }

        List<String> fndCityIdsList = new ArrayList<>();
        List<String> porCityIds = new ArrayList<>();
        fndCityIdsList.add(monitorDTO.getEndPort().getId());
        porCityIds.add(monitorDTO.getStartPort().getId());
        listMap.put("fndCityIds", fndCityIdsList);
        listMap.put("porCityIds", porCityIds);
        headers.put("Cookie", monitorDTO.getCookie());
        headers.put("X-Auth-Token", monitorDTO.getToken());
        headers.put("Referer", "https://freightsmart.oocl.com/prebooking");

        String result = OclUtils.sendPost(url, listMap, headers, account.getAgentIp());
        if (result.contains("meta") || "".equals(result)) {
            System.out.println(result);
            String ip = MyUtils.getIp();
            account.setAgentIp(ip);
            accountService.updateById(account);
            return null;
        }
        Map<String, Object> resultMap = JSONObject.parseObject(result);
        return JSONObject.parseObject(resultMap.get("content").toString(), new TypeReference<List<Map<String, Object>>>() {
        });
    }

    private void sendMail(String title, String text) {
        // ????????????????????????
        SimpleMailMessage message = new SimpleMailMessage();
        // ??????????????????
        message.setSubject(title);
        // ?????????????????????????????????application.yml?????????????????????
        message.setFrom("1059308740@qq.com");

//        message.setTo("1973432033@qq.com","Zhibo_Tang@zjou.edu.cn","771829811@qq.com"); one-ebooking@nb-hj.com ???????????? "
        message.setTo("1059308740@qq.com", "2083774374@qq.com");
        // ????????????????????????
        message.setSentDate(new Date());
        // ?????????????????????

        message.setText(text);
        // ????????????
        javaMailSender.send(message);
    }

    private void judgeInventory(List<Map<String, Object>> content, MonitorDTO monitorDTO, boolean flag) {
        List<MonitorData> monitorDataList = null;
        if (flag) {
            monitorDataList = monitorDataService.getListByLineId(monitorDTO.getId());
        }
        for (Map<String, Object> map : content) {
            List<Map<String, Object>> sailingProductDTOS = JSONObject.parseObject(map.get("sailingProductDTOS").toString(), new TypeReference<List<Map<String, Object>>>() {
            });
            for (Map<String, Object> sailingProductDTO : sailingProductDTOS) {
                if (!flag) {
                    MonitorData data = MonitorData.builder()
                            .jobId(monitorDTO.getId())
                            .vesselName(sailingProductDTO.get("vesselName").toString())
                            .voyage(sailingProductDTO.get("voyageNo").toString())
                            .inventory(Integer.parseInt(sailingProductDTO.get("inventory").toString()))
                            .build();
                    try {
                        monitorDataService.insert(data);
                    } catch (Exception e) {
                        System.out.println("????????????????????????????????????");
                    }

                } else {
                    Integer inventory = Integer.parseInt(sailingProductDTO.get("inventory").toString());
                    assert monitorDataList != null;
                    for (MonitorData monitorData : monitorDataList) {
                        if (monitorData.getVesselName().equals(sailingProductDTO.get("vesselName").toString()) && monitorData.getVoyage().equals(sailingProductDTO.get("voyageNo").toString())) {
                            String text = "???" + monitorDTO.getStartPort().getCityFullNameCn()
                                    + "??? ?????? ???" + monitorDTO.getEndPort().getCityFullNameCn() +
                                    "????????????" + monitorData.getVesselName() + " ?????????" + monitorData.getVoyage();
                            if (!Objects.equals(monitorData.getInventory(), inventory)) {
                                sendMail(text + "?????????????????????", "???????????????" + monitorData.getInventory() + "???????????????" + inventory + "?????????" + JSONObject.toJSONString(sailingProductDTO.get("containerOceanFeeMap")));
                                monitorData.setInventory(inventory);
                                monitorDataService.updateById(monitorData);
                            } else {
                                System.out.println("???????????????" + sdf.format(new Date()) + text + "?????????" + inventory + " ????????????");
                            }
                        }
                    }
                }
            }
        }
    }
}
