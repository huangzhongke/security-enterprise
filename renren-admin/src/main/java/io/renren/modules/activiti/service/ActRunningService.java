/**
 * Copyright (c) 2018 人人开源 All rights reserved.
 *
 * https://www.renren.io
 *
 * 版权所有，侵权必究！
 */

package io.renren.modules.activiti.service;

import io.renren.common.constant.Constant;
import io.renren.common.exception.ErrorCode;
import io.renren.common.exception.RenException;
import io.renren.common.page.PageData;
import io.renren.modules.activiti.dto.ProcessInstanceDTO;
import io.renren.modules.activiti.dto.ProcessStartDTO;
import io.renren.modules.security.user.SecurityUser;
import org.activiti.engine.IdentityService;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.impl.RepositoryServiceImpl;
import org.activiti.engine.impl.persistence.entity.ProcessDefinitionEntity;
import org.activiti.engine.impl.pvm.process.ActivityImpl;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.runtime.ProcessInstanceQuery;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 运行中的流程
 *
 * @author Mark sunlightcs@gmail.com
 */
@Service
public class ActRunningService {
    @Autowired
    private RuntimeService runtimeService;

    @Autowired
    private IdentityService identityService;

    @Autowired
    protected RepositoryService repositoryService;

    /**
     * 流程定义列表
     */
    public PageData<Map<String, Object>> page(Map<String, Object> params) {
        String id = (String)params.get("id");
        String definitionKey = (String)params.get("definitionKey");

        //分页参数
        int curPage = 1;
        int limit = 10;
        if(params.get(Constant.PAGE) != null){
            curPage = Integer.parseInt((String)params.get(Constant.PAGE));
        }
        if(params.get(Constant.LIMIT) != null){
            limit = Integer.parseInt((String)params.get(Constant.LIMIT));
        }

        ProcessInstanceQuery processInstanceQuery = runtimeService.createProcessInstanceQuery();
        if (StringUtils.isNotBlank(id)){
            processInstanceQuery.processInstanceId(id);
        }
        if (StringUtils.isNotBlank(definitionKey)){
            processInstanceQuery.processDefinitionKey(definitionKey);
        }

        List<ProcessInstance> processInstanceList = processInstanceQuery.listPage((curPage - 1) * limit, limit);
        List<Map<String, Object>> objectList = new ArrayList<>();
        for (ProcessInstance processInstance : processInstanceList) {
            objectList.add(processInstanceConvert(processInstance));
        }
        return new PageData<>(objectList, (int)processInstanceQuery.count());
    }

    /**
     * 流程实例信息
     */
    private Map<String, Object> processInstanceConvert(ProcessInstance processInstance) {
        Map<String, Object> map = new HashMap<>(9);
        map.put("id", processInstance.getId());
        map.put("processInstanceId", processInstance.getProcessInstanceId());
        map.put("processDefinitionId", processInstance.getProcessDefinitionId());
        map.put("processDefinitionName", processInstance.getProcessDefinitionName());
        map.put("processDefinitionKey", processInstance.getProcessDefinitionKey());
        map.put("businessKey", processInstance.getBusinessKey());
        map.put("activityId", processInstance.getActivityId());

        ProcessDefinitionEntity definition = (ProcessDefinitionEntity)((RepositoryServiceImpl)repositoryService).getDeployedProcessDefinition(processInstance.getProcessDefinitionId());
        ActivityImpl activity = definition.findActivity(processInstance.getActivityId());
        map.put("activityName", activity.getProperty("name"));
        map.put("suspended", processInstance.isSuspended());

        return map;
    }

    /**
     * 删除实例
     * @param id  实例ID
     */
    public void delete(String id){
        runtimeService.deleteProcessInstance(id, null);
    }

    /**
     * 启动流程实例
     * @param key 流程定义标识key
     */
    public ProcessInstanceDTO startProcess(String key){
        String userId = SecurityUser.getUserId().toString();
        identityService.setAuthenticatedUserId(userId);
        ProcessDefinitionEntity definition = (ProcessDefinitionEntity)repositoryService.createProcessDefinitionQuery().processDefinitionKey(key).latestVersion().singleResult();
        if(definition.isSuspended()){
            throw new RenException(ErrorCode.PROCESS_START_ERROR);
        }
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey(key);
        ProcessInstanceDTO dto = new ProcessInstanceDTO();
        this.convertInstance(processInstance, dto);
        return dto;
    }

    private void convertInstance(ProcessInstance processInstance, ProcessInstanceDTO dto) {
        dto.setBusinessKey(processInstance.getBusinessKey());
        dto.setDeploymentId(processInstance.getDeploymentId());
        dto.setDescription(processInstance.getDescription());
        dto.setName(processInstance.getName());
        dto.setEnded(processInstance.isEnded());
        dto.setSuspended(processInstance.isSuspended());
        dto.setProcessDefinitionId(processInstance.getProcessDefinitionId());
        dto.setProcessDefinitionKey(processInstance.getProcessDefinitionKey());
        dto.setProcessDefinitionName(processInstance.getProcessDefinitionName());
        dto.setProcessDefinitionVersion(processInstance.getProcessDefinitionVersion());
        dto.setProcessInstanceId(processInstance.getProcessInstanceId());
    }

    /**
     * 根据流程Key，启动实例
     * @param processStartDTO
     * @return
     */
    public ProcessInstanceDTO startOfBusinessKey(ProcessStartDTO processStartDTO) {
        String userId = SecurityUser.getUserId().toString();
        identityService.setAuthenticatedUserId(userId);
        ProcessDefinitionEntity definition = (ProcessDefinitionEntity)repositoryService.createProcessDefinitionQuery().processDefinitionKey(processStartDTO.getProcessDefinitionKey()).latestVersion().singleResult();
        if(definition.isSuspended()){
            throw new RenException(ErrorCode.PROCESS_START_ERROR);
        }
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey(processStartDTO.getProcessDefinitionKey(), processStartDTO.getBusinessKey(),processStartDTO.getVariables());
        ProcessInstanceDTO dto = new ProcessInstanceDTO();
        this.convertInstance(processInstance, dto);
        return dto;
    }
}
