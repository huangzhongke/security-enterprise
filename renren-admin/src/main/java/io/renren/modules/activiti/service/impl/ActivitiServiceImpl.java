/**
 * Copyright (c) 2019 人人开源 All rights reserved.
 *
 * https://www.renren.io
 *
 * 版权所有，侵权必究！
 */

package io.renren.modules.activiti.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import io.renren.common.page.PageData;
import io.renren.common.service.impl.BaseServiceImpl;
import io.renren.common.utils.ConvertUtils;
import io.renren.modules.activiti.dao.ProcessActivityDao;
import io.renren.modules.activiti.dto.HistoryDetailDTO;
import io.renren.modules.activiti.dto.ProcessActivityDTO;
import io.renren.modules.activiti.entity.HistoryDetailEntity;
import io.renren.modules.activiti.entity.ProcessActivityEntity;
import io.renren.modules.activiti.service.ActivitiService;
import io.renren.modules.security.user.SecurityUser;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * 工作流
 *
 * @author Jone
 */
@Service
public class ActivitiServiceImpl extends BaseServiceImpl<ProcessActivityDao, ProcessActivityEntity> implements ActivitiService {

    @Override
    public PageData<ProcessActivityDTO> getMyProcessInstancePage(Map<String, Object> params) {
        params.put("userId", SecurityUser.getUserId().toString());
        IPage<ProcessActivityEntity> page = getPage(params, null, false);
        List<ProcessActivityEntity> list = baseDao.getMyProcessInstancePage(params);
        return getPageData(list, page.getTotal(), ProcessActivityDTO.class);
    }

    @Override
    public List<HistoryDetailDTO> getTaskHandleDetailInfo(String processInstanceId) {
        List<HistoryDetailEntity> listEntity = baseDao.getTaskHandleDetailInfo(processInstanceId);
        for(HistoryDetailEntity entity : listEntity){
            if(entity.getEndTime() != null && entity.getStartTime() != null){
                long diff = entity.getEndTime().getTime() - entity.getStartTime().getTime();
                entity.setDurationInSeconds(diff/1000);
            }
        }
        return ConvertUtils.sourceToTarget(listEntity, HistoryDetailDTO.class);
    }
}
