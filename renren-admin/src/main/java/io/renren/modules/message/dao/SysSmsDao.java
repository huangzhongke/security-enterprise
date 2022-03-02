/**
 * Copyright (c) 2018 人人开源 All rights reserved.
 *
 * https://www.renren.io
 *
 * 版权所有，侵权必究！
 */

package io.renren.modules.message.dao;

import io.renren.common.dao.BaseDao;
import io.renren.modules.message.entity.SysSmsEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * 短信
 * 
 * @author Mark sunlightcs@gmail.com
 */
@Mapper
public interface SysSmsDao extends BaseDao<SysSmsEntity> {
	
}
