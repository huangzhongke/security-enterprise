package io.renren.modules.activiti.demo.service;


import io.renren.common.service.CrudService;
import io.renren.modules.activiti.demo.dto.CorrectionDTO;
import io.renren.modules.activiti.demo.entity.CorrectionEntity;

/**
 * 转正申请
 *
 * @author Mark sunlightcs@gmail.com
 */
public interface CorrectionService extends CrudService<CorrectionEntity, CorrectionDTO> {

    void updateInstanceId(String instanceId, Long id);
}