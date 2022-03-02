package io.renren.modules.activiti.demo.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.renren.common.service.impl.CrudServiceImpl;
import io.renren.modules.activiti.demo.dao.CorrectionDao;
import io.renren.modules.activiti.demo.dto.CorrectionDTO;
import io.renren.modules.activiti.demo.entity.CorrectionEntity;
import io.renren.modules.activiti.demo.service.CorrectionService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * 转正申请
 *
 * @author Mark sunlightcs@gmail.com
 */
@Service
public class CorrectionServiceImpl extends CrudServiceImpl<CorrectionDao, CorrectionEntity, CorrectionDTO> implements CorrectionService {

    @Override
    public QueryWrapper<CorrectionEntity> getWrapper(Map<String, Object> params){
        String id = (String)params.get("id");

        QueryWrapper<CorrectionEntity> wrapper = new QueryWrapper<>();
        wrapper.eq(StringUtils.isNotBlank(id), "id", id);

        return wrapper;
    }


    @Override
    public void updateInstanceId(String instanceId, Long id) {
        baseDao.updateInstanceId(instanceId, id);
    }
}