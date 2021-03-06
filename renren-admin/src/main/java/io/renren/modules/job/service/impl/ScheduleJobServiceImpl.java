/**
 * Copyright (c) 2018 人人开源 All rights reserved.
 * <p>
 * https://www.renren.io
 * <p>
 * 版权所有，侵权必究！
 */

package io.renren.modules.job.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import io.renren.common.constant.Constant;
import io.renren.common.page.PageData;
import io.renren.common.service.impl.BaseServiceImpl;
import io.renren.common.utils.ConvertUtils;
import io.renren.modules.job.dao.ScheduleJobDao;
import io.renren.modules.job.dto.ScheduleJobDTO;
import io.renren.modules.job.entity.ScheduleJobEntity;
import io.renren.modules.job.service.ScheduleJobService;
import io.renren.modules.job.utils.ScheduleUtils;
import io.renren.modules.spider.one.dto.DataFormDto;
import io.renren.modules.spider.oocl.dto.MonitorDTO;
import io.renren.modules.spider.oocl.dto.OOCLDataFormDTO;
import io.renren.modules.spider.menu.entity.Line;
import io.renren.modules.spider.one.entity.SpiderReference;
import io.renren.modules.spider.one.service.LineService;
import io.renren.modules.spider.one.service.SpiderReferenceService;
import io.renren.modules.spider.oocl.entity.ChildAccount;
import io.renren.modules.spider.oocl.service.ChildAccountService;
import org.apache.commons.lang3.StringUtils;
import org.quartz.Scheduler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ScheduleJobServiceImpl extends BaseServiceImpl<ScheduleJobDao, ScheduleJobEntity> implements ScheduleJobService {
    @Autowired
    private Scheduler scheduler;
    @Autowired
    LineService lineService;
    @Autowired
    SpiderReferenceService spiderReferenceService;
    @Autowired
    ChildAccountService childAccountService;

    @Override
    public PageData<ScheduleJobDTO> page(Map<String, Object> params) {
        IPage<ScheduleJobEntity> page = baseDao.selectPage(
                getPage(params, Constant.CREATE_DATE, false),
                getWrapper(params)
        );
        return getPageData(page, ScheduleJobDTO.class);
    }

    @Override
    public ScheduleJobDTO get(Long id) {
        ScheduleJobEntity entity = baseDao.selectById(id);

        return ConvertUtils.sourceToTarget(entity, ScheduleJobDTO.class);
    }

    private QueryWrapper<ScheduleJobEntity> getWrapper(Map<String, Object> params) {
        // 根据定时任务的type进行查询，0为one 1 oocl
        String beanName = (String) params.get("beanName");
        String type = (String) params.get("type");

        QueryWrapper<ScheduleJobEntity> wrapper = new QueryWrapper<>();
        wrapper.like(StringUtils.isNotBlank(beanName), "bean_name", beanName);
        wrapper.like(StringUtils.isNotBlank(type), "type", type);

        return wrapper;
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public void save(DataFormDto dataFormDto) {
        // 存三张表 schedule_job 主表 -> line子表
        // line为主表 -> references 为子表
        // 1 先存 schedule_job表
        // 防止输入中文的逗号
        String references = dataFormDto.getReferences().replace("，", ",");
        Map<String, Object> params = new HashMap<>();
        params.put("id", null);
        params.put("startPort", dataFormDto.getStartPort());
        params.put("endPort", dataFormDto.getEndPort());
        params.put("notifyAddress", dataFormDto.getNotifyAddress());
        params.put("consigneeAddress", dataFormDto.getConsigneeAddress());
        params.put("isNeedBrokerageAddress", dataFormDto.getIsNeedBrokerageAddress());
        params.put("isNeedNotifyAddress", dataFormDto.getIsNeedNotifyAddress());
        params.put("isNeedConsigneeAddress", dataFormDto.getIsNeedConsigneeAddress());
        params.put("etdDays", dataFormDto.getEtdDays());
        params.put("isProxy", dataFormDto.getIsProxy());
        params.put("isNeedLineName", dataFormDto.getIsNeedLineName());
        params.put("isNeedSupplierName", dataFormDto.getIsNeedSupplierName());
        params.put("equipment", dataFormDto.getEquipment());
        params.put("quantity", dataFormDto.getQuantity());
        params.put("vesselName", dataFormDto.getVesselName());
        params.put("voyage", dataFormDto.getVoyage());
        params.put("supplierName", dataFormDto.getSupplierName());
        params.put("orderSleepTime", dataFormDto.getOrderSleepTime());
        params.put("authorization", dataFormDto.getAuthorization());
        params.put("references", references);
        params.put("scac", dataFormDto.getScac());
        params.put("price", dataFormDto.getPrice());
        params.put("account", dataFormDto.getAccount());
        params.put("weight", dataFormDto.getWeight());
        params.put("type", dataFormDto.getType());

        ScheduleJobEntity entity = new ScheduleJobEntity();
        entity.setBeanName(dataFormDto.getBeanName());
        entity.setCronExpression(dataFormDto.getCronExpression());
        entity.setRemark(dataFormDto.getRemark());
        entity.setParams(JSONObject.toJSONString(params));
        // ScheduleJobEntity entity = ConvertUtils.sourceToTarget(dto, ScheduleJobEntity.class);
        entity.setStatus(Constant.ScheduleStatus.NORMAL.getValue());
        entity.setType(dataFormDto.getType());
        this.insert(entity);
        // 2 存 子表 spider_line
        Line line = new Line();
        line.setParams(JSONObject.toJSONString(params));
        line.setJobId(entity.getId());
        line.setAuthorization(dataFormDto.getAuthorization());
        Long lineId = lineService.save(line);
        // 3 存 子子表 Spider_references
        String[] reference = references.split(",");
        for (String r : reference) {
            SpiderReference sr = SpiderReference.builder()
                    .reference(r)
                    .used(false)
                    .lineId(lineId)
                    .account(dataFormDto.getAccount())
                    .success(false)
                    .build();
            spiderReferenceService.insert(sr);

        }
        params.put("id", lineId);
        entity.setParams(JSONObject.toJSONString(params));
        baseDao.updateById(entity);
        ScheduleUtils.createScheduleJob(scheduler, entity);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(DataFormDto dataFormDto) {
        Map<String, Object> map = JSONObject.parseObject(dataFormDto.getParams());
        Long lineId = 0L;
        if (map.get("id") != null) {
            lineId = Long.parseLong(map.get("id").toString());
        }

        String references = dataFormDto.getReferences().replace("，", ",");
        Map<String, Object> params = new HashMap<>();
        params.put("id", lineId);
        params.put("startPort", dataFormDto.getStartPort());
        params.put("endPort", dataFormDto.getEndPort());
        params.put("notifyAddress", dataFormDto.getNotifyAddress());
        params.put("consigneeAddress", dataFormDto.getConsigneeAddress());
        params.put("isNeedBrokerageAddress", dataFormDto.getIsNeedBrokerageAddress());
        params.put("isNeedNotifyAddress", dataFormDto.getIsNeedNotifyAddress());
        params.put("isNeedConsigneeAddress", dataFormDto.getIsNeedConsigneeAddress());
        params.put("etdDays", dataFormDto.getEtdDays());
        params.put("isProxy", dataFormDto.getIsProxy());
        params.put("isNeedLineName", dataFormDto.getIsNeedLineName());
        params.put("isNeedSupplierName", dataFormDto.getIsNeedSupplierName());
        params.put("equipment", dataFormDto.getEquipment());
        params.put("quantity", dataFormDto.getQuantity());
        params.put("vesselName", dataFormDto.getVesselName());
        params.put("voyage", dataFormDto.getVoyage());
        params.put("supplierName", dataFormDto.getSupplierName());
        params.put("orderSleepTime", dataFormDto.getOrderSleepTime());
        params.put("authorization", dataFormDto.getAuthorization());
        params.put("references", references);
        params.put("scac", dataFormDto.getScac());
        params.put("price", dataFormDto.getPrice());
        params.put("account", dataFormDto.getAccount());
        params.put("weight", dataFormDto.getWeight());
        params.put("type", dataFormDto.getType());

        ScheduleJobEntity entity = new ScheduleJobEntity();
        entity.setId(dataFormDto.getId());
        entity.setBeanName(dataFormDto.getBeanName());
        entity.setCronExpression(dataFormDto.getCronExpression());
        entity.setRemark(dataFormDto.getRemark());
        entity.setParams(JSONObject.toJSONString(params));
        entity.setStatus(dataFormDto.getStatus());
        entity.setType(dataFormDto.getType());
        // ScheduleJobEntity entity = ConvertUtils.sourceToTarget(dto, ScheduleJobEntity.class);
        ScheduleUtils.updateScheduleJob(scheduler, entity);
        this.updateById(entity);
        Line line = new Line();
        line.setId(lineId);
        line.setParams(JSONObject.toJSONString(params));
        line.setJobId(entity.getId());
        line.setAuthorization(dataFormDto.getAuthorization());
        lineService.updateById(line);
        spiderReferenceService.deleteByLineId(lineId);
        String[] reference = references.split(",");
        for (String r : reference) {
            SpiderReference sr = SpiderReference.builder()
                    .reference(r)
                    .used(false)
                    .lineId(lineId)
                    .account(dataFormDto.getAccount())
                    .success(false)
                    .build();
            spiderReferenceService.insert(sr);

        }

    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void save(OOCLDataFormDTO dataFormDto) {
        ScheduleJobEntity entity = new ScheduleJobEntity();
        Map<String, Object> paramsMap = new HashMap<>();
        paramsMap.put("id", null);
        paramsMap.put("startPort", dataFormDto.getStartPort());
        paramsMap.put("endPort", dataFormDto.getEndPort());
        paramsMap.put("equipment", dataFormDto.getEquipment());
        paramsMap.put("vesselName", dataFormDto.getVesselName());
        paramsMap.put("voyage", dataFormDto.getVoyage());
        paramsMap.put("quantity", dataFormDto.getQuantity());
        paramsMap.put("price", dataFormDto.getPrice());
        paramsMap.put("account", dataFormDto.getAccount());
        paramsMap.put("cookie", dataFormDto.getCookie());
        paramsMap.put("token", dataFormDto.getToken());
        paramsMap.put("startDate", dataFormDto.getStartDate());
        paramsMap.put("endDate", dataFormDto.getEndDate());
        paramsMap.put("childAccount", dataFormDto.getChildAccount());
        paramsMap.put("type", dataFormDto.getType());
        paramsMap.put("isNeedVesselName", dataFormDto.getIsNeedVesselName());
        paramsMap.put("isColdType", dataFormDto.getIsColdType());
        entity.setBeanName(dataFormDto.getBeanName());
        entity.setCronExpression(dataFormDto.getCronExpression());
        entity.setRemark(dataFormDto.getRemark());
        entity.setParams(JSONObject.toJSONString(paramsMap));
        // ScheduleJobEntity entity = ConvertUtils.sourceToTarget(dto, ScheduleJobEntity.class);
        entity.setStatus(Constant.ScheduleStatus.NORMAL.getValue());
        entity.setType(dataFormDto.getType());
        scheduleJobService.insert(entity);
        paramsMap.put("id", entity.getId());
        entity.setParams(JSONObject.toJSONString(paramsMap));
        baseDao.updateById(entity);
        // 1 设置下单账号定时任务 存完之后 给子账号也存一张表 存子账号的cookie 和 token以及数据
        // schedule_job 表 oocl line  id params  status    id params line id 删定时任务先删子表 然后主表
        // 1 先存 Line 表
        Line line = new Line();
        line.setJobId(entity.getId());
        line.setParams(JSONObject.toJSONString(paramsMap));
        line.setOrderStatus(false);
        lineService.save(line);
        // 2 存子账号 并且吧 每个子账号登录 获取 token,cookie
        childAccountService.saveByUserList(dataFormDto.getChildAccount(), line.getId());
        ScheduleUtils.createScheduleJob(scheduler, entity);

    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(OOCLDataFormDTO dataFormDto) {
        ScheduleJobEntity entity = new ScheduleJobEntity();
        Map<String, Object> paramsMap = new HashMap<>();
        paramsMap.put("id", dataFormDto.getId());
        paramsMap.put("startPort", dataFormDto.getStartPort());
        paramsMap.put("endPort", dataFormDto.getEndPort());
        paramsMap.put("equipment", dataFormDto.getEquipment());
        paramsMap.put("vesselName", dataFormDto.getVesselName());
        paramsMap.put("voyage", dataFormDto.getVoyage());
        paramsMap.put("quantity", dataFormDto.getQuantity());
        paramsMap.put("price", dataFormDto.getPrice());
        paramsMap.put("account", dataFormDto.getAccount());
        paramsMap.put("cookie", dataFormDto.getCookie());
        paramsMap.put("token", dataFormDto.getToken());
        paramsMap.put("startDate", dataFormDto.getStartDate());
        paramsMap.put("endDate", dataFormDto.getEndDate());
        paramsMap.put("childAccount", dataFormDto.getChildAccount());
        paramsMap.put("isNeedVesselName", dataFormDto.getIsNeedVesselName());
        paramsMap.put("isColdType", dataFormDto.getIsColdType());

        entity.setId(dataFormDto.getId());
        entity.setBeanName(dataFormDto.getBeanName());
        entity.setCronExpression(dataFormDto.getCronExpression());
        entity.setRemark(dataFormDto.getRemark());
        entity.setParams(JSONObject.toJSONString(paramsMap));
        entity.setStatus(dataFormDto.getStatus());
        // ScheduleJobEntity entity = ConvertUtils.sourceToTarget(dto, ScheduleJobEntity.class);
        Line line = lineService.getByJobId(entity.getId());
        childAccountService.deleteByLineId(line.getId());
        childAccountService.saveByUserList(dataFormDto.getChildAccount(),line.getId());
        ScheduleUtils.updateScheduleJob(scheduler, entity);
        this.updateById(entity);
    }

    @Override
    public void save(MonitorDTO dto) {
        ScheduleJobEntity entity = new ScheduleJobEntity();
        Map<String, Object> paramsMap = new HashMap<>();
        paramsMap.put("id", null);
        paramsMap.put("startPort", dto.getStartPort());
        paramsMap.put("endPort", dto.getEndPort());
        paramsMap.put("account", dto.getAccount());
        paramsMap.put("cookie", dto.getCookie());
        paramsMap.put("token", dto.getToken());
        paramsMap.put("startDate", dto.getStartDate());
        paramsMap.put("endDate", dto.getEndDate());
        paramsMap.put("type", dto.getType());
        paramsMap.put("flag", dto.getFlag());
        entity.setBeanName(dto.getBeanName());
        entity.setCronExpression(dto.getCronExpression());
        entity.setRemark(dto.getRemark());
        entity.setParams(JSONObject.toJSONString(paramsMap));
        entity.setStatus(Constant.ScheduleStatus.NORMAL.getValue());
        entity.setType(dto.getType());
        scheduleJobService.insert(entity);
        paramsMap.put("id", entity.getId());
        entity.setParams(JSONObject.toJSONString(paramsMap));
        baseDao.updateById(entity);

        ScheduleUtils.createScheduleJob(scheduler, entity);
    }

    @Override
    public void update(MonitorDTO dto) {
        ScheduleJobEntity entity = new ScheduleJobEntity();
        Map<String, Object> paramsMap = new HashMap<>();
        paramsMap.put("id", dto.getId());
        paramsMap.put("startPort", dto.getStartPort());
        paramsMap.put("endPort", dto.getEndPort());
        paramsMap.put("account", dto.getAccount());
        paramsMap.put("cookie", dto.getCookie());
        paramsMap.put("token", dto.getToken());
        paramsMap.put("startDate", dto.getStartDate());
        paramsMap.put("endDate", dto.getEndDate());
        paramsMap.put("type", dto.getType());
        paramsMap.put("flag", dto.getFlag());
        entity.setId(dto.getId());
        entity.setBeanName(dto.getBeanName());
        entity.setCronExpression(dto.getCronExpression());
        entity.setRemark(dto.getRemark());
        entity.setParams(JSONObject.toJSONString(paramsMap));
        entity.setStatus(dto.getStatus());
        entity.setType(dto.getType());
        ScheduleUtils.updateScheduleJob(scheduler, entity);
        this.updateById(entity);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteBatch(Long[] ids) {
        for (Long id : ids) {
            ScheduleUtils.deleteScheduleJob(scheduler, id);
        }

        // 删除数据
        this.deleteBatchIds(Arrays.asList(ids));
    }

    @Override
    public int updateBatch(Long[] ids, int status) {
        Map<String, Object> map = new HashMap<>(2);
        map.put("ids", ids);
        map.put("status", status);
        return baseDao.updateBatch(map);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void run(Long[] ids) {
        for (Long id : ids) {
            ScheduleUtils.run(scheduler, this.selectById(id));
        }
    }

    @Autowired
    ScheduleJobService scheduleJobService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void pause(Long[] ids) {
        for (Long id : ids) {
            ScheduleUtils.pauseJob(scheduler, id);
        }
        updateBatch(ids, Constant.ScheduleStatus.PAUSE.getValue());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void resume(Long[] ids) {
        for (Long id : ids) {
            ScheduleUtils.resumeJob(scheduler, id);
        }

        updateBatch(ids, Constant.ScheduleStatus.NORMAL.getValue());
    }

}