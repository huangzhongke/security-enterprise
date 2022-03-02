package io.renren.modules.activiti.service;

import io.renren.common.service.BaseService;
import io.renren.modules.activiti.dto.ProcessBizRouteDTO;
import io.renren.modules.activiti.entity.ProcessBizRouteEntity;

/**
 * @Author:Jone
 */
public interface ProcessBizRouteService  extends BaseService<ProcessBizRouteEntity> {

    ProcessBizRouteDTO getProcDefBizRoute(String id);

    void save(ProcessBizRouteDTO processBizRouteDTO);

    void updateProcBizRoute(ProcessBizRouteDTO processBizRouteDTO);

    ProcessBizRouteDTO getLatestProcDefBizRoute(String procDefKey);
}
