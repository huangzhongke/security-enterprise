/**
 * Copyright (c) 2019 人人开源 All rights reserved.
 *
 * https://www.renren.io
 *
 * 版权所有，侵权必究！
 */

package io.renren.modules.activiti.service;

import io.renren.common.page.PageData;
import io.renren.common.service.BaseService;
import io.renren.modules.activiti.dto.HistoryDetailDTO;
import io.renren.modules.activiti.dto.ProcessActivityDTO;
import io.renren.modules.activiti.entity.ProcessActivityEntity;

import java.util.List;
import java.util.Map;

/**
 * 流程自定义查询
 *
 * @author Jone
 */
public interface ActivitiService  extends BaseService<ProcessActivityEntity> {

    PageData<ProcessActivityDTO> getMyProcessInstancePage(Map<String, Object> params);

    List<HistoryDetailDTO> getTaskHandleDetailInfo(String processInstanceId);
}
