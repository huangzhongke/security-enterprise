/**
 * Copyright (c) 2018 人人开源 All rights reserved.
 *
 * https://www.renren.io
 *
 * 版权所有，侵权必究！
 */

package io.renren.modules.message.dao;

import io.renren.common.dao.BaseDao;
import io.renren.modules.message.entity.SysMailTemplateEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * 邮件模板
 * 
 * @author Mark sunlightcs@gmail.com
 */
@Mapper
public interface SysMailTemplateDao extends BaseDao<SysMailTemplateEntity> {
	
}
