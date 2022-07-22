package io.renren.modules.spider.oocl.service;

import io.renren.common.service.BaseService;
import io.renren.modules.spider.oocl.dao.MonitorDataDao;
import io.renren.modules.spider.oocl.entity.MonitorData;

import java.util.List;

public interface MonitorDataService extends BaseService<MonitorData> {
    List<MonitorData> getListByLineId(Long jobId);
    void deleteByJobIds (Long[] ids);
}
