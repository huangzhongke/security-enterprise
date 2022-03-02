package io.renren.modules.message.dao;

import io.renren.common.dao.BaseDao;
import io.renren.modules.message.entity.SysSmsLogEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * 短信日志
 *
 * @author Mark sunlightcs@gmail.com
 */
@Mapper
public interface SysSmsLogDao extends BaseDao<SysSmsLogEntity> {
	
}