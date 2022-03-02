package io.renren.modules.activiti.service.impl;

import io.renren.common.service.impl.BaseServiceImpl;
import io.renren.common.utils.ConvertUtils;
import io.renren.modules.activiti.dao.ProcessBizRouteDao;
import io.renren.modules.activiti.dto.ProcessBizRouteDTO;
import io.renren.modules.activiti.entity.ProcessBizRouteEntity;
import io.renren.modules.activiti.service.ProcessBizRouteService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Author:Jone
 */
@Service
public class ProcessBizRouteServiceImpl  extends BaseServiceImpl<ProcessBizRouteDao, ProcessBizRouteEntity> implements ProcessBizRouteService {

    @Override
    public ProcessBizRouteDTO getProcDefBizRoute(String id) {
        ProcessBizRouteEntity entity = baseDao.getProcDefBizRoute(id);
        ProcessBizRouteDTO dto = ConvertUtils.sourceToTarget(entity, ProcessBizRouteDTO.class);
        return dto;
    }

    @Override
    public void save(ProcessBizRouteDTO processBizRouteDTO) {
        ProcessBizRouteEntity entity = ConvertUtils.sourceToTarget(processBizRouteDTO, ProcessBizRouteEntity.class);
        this.insert(entity);
    }

    @Override
    public void updateProcBizRoute(ProcessBizRouteDTO processBizRouteDTO) {
        ProcessBizRouteEntity entity = ConvertUtils.sourceToTarget(processBizRouteDTO, ProcessBizRouteEntity.class);
        this.updateById(entity);
    }

    @Override
    public ProcessBizRouteDTO getLatestProcDefBizRoute(String procDefKey) {
        List<ProcessBizRouteEntity> list = baseDao.getLatestProcDefBizRoute(procDefKey);
        if(list.isEmpty()){
            return null;
        }
        ProcessBizRouteEntity entity = list.get(0);
        return ConvertUtils.sourceToTarget(entity, ProcessBizRouteDTO.class);
    }
}
