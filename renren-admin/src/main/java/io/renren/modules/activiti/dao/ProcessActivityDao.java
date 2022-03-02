/**
 * Copyright (c) 2018 人人开源 All rights reserved.
 *
 * https://www.renren.io
 *
 * 版权所有，侵权必究！
 */

package io.renren.modules.activiti.dao;

import io.renren.common.dao.BaseDao;
import io.renren.modules.activiti.entity.HistoryDetailEntity;
import io.renren.modules.activiti.entity.ProcessActivityEntity;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.context.annotation.Primary;

import java.util.List;
import java.util.Map;

/**
 * @author Jone
 */
@Mapper
@Primary
public interface ProcessActivityDao extends BaseDao<ProcessActivityEntity> {

    List<ProcessActivityEntity> getMyProcessInstancePage(Map<String, Object> params);

    List<HistoryDetailEntity> getTaskHandleDetailInfo(String processInstanceId);
}
