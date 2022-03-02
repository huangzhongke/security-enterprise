package io.renren.modules.activiti.demo.dao;

import io.renren.common.dao.BaseDao;
import io.renren.modules.activiti.demo.entity.CorrectionEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * 转正申请
 *
 * @author Mark sunlightcs@gmail.com
 */
@Mapper
public interface CorrectionDao extends BaseDao<CorrectionEntity> {

    void updateInstanceId(String instanceId, Long id);
}