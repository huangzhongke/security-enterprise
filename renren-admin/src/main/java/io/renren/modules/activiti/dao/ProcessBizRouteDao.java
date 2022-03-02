package io.renren.modules.activiti.dao;

import io.renren.common.dao.BaseDao;
import io.renren.modules.activiti.entity.ProcessBizRouteEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Author:Jone
 */
@Mapper
public interface ProcessBizRouteDao extends BaseDao<ProcessBizRouteEntity> {
    ProcessBizRouteEntity getProcDefBizRoute(@Param("proDefId") String id);

    List<ProcessBizRouteEntity> getLatestProcDefBizRoute(@Param("procDefKey") String procDefKey);
}
