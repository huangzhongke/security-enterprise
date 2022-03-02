package io.renren.modules.activiti.service;

import io.renren.common.constant.Constant;
import io.renren.common.page.PageData;
import io.renren.modules.activiti.dto.ProcessInstanceDTO;
import io.renren.modules.activiti.dto.TaskDTO;
import io.renren.modules.security.user.SecurityUser;
import io.renren.modules.sys.dto.SysUserDTO;
import io.renren.modules.sys.service.SysUserService;
import org.activiti.bpmn.model.BpmnModel;
import org.activiti.engine.*;
import org.activiti.engine.history.HistoricActivityInstance;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.history.HistoricProcessInstanceQuery;
import org.activiti.engine.impl.cfg.ProcessEngineConfigurationImpl;
import org.activiti.engine.impl.context.Context;
import org.activiti.engine.impl.persistence.entity.ExecutionEntity;
import org.activiti.engine.impl.persistence.entity.ProcessDefinitionEntity;
import org.activiti.engine.impl.pvm.PvmTransition;
import org.activiti.engine.impl.pvm.process.ActivityImpl;
import org.activiti.engine.task.Task;
import org.activiti.image.ProcessDiagramGenerator;
import org.activiti.spring.ProcessEngineFactoryBean;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 工作流
 *
 * @author Jone
 */
@Service
public class ActHistoryService {

    @Autowired
    protected RepositoryService repositoryService;

    @Autowired
    protected RuntimeService runtimeService;

    @Autowired
    protected HistoryService historyService;

    @Autowired
    protected ProcessEngineConfiguration processEngineConfiguration;

    @Autowired
    protected ProcessEngineFactoryBean processEngine;

    @Autowired
    protected TaskService taskService;

    @Autowired
    private SysUserService sysUserService;



    public void getProcessInstanceDiagram(String processInstanceId, HttpServletResponse response) throws Exception {
        if(StringUtils.isEmpty(processInstanceId)){
            return;
        }
        HistoricProcessInstance processInstance =  historyService.createHistoricProcessInstanceQuery().processInstanceId(processInstanceId).singleResult();
        BpmnModel bpmnModel = repositoryService.getBpmnModel(processInstance.getProcessDefinitionId());
        processEngineConfiguration = processEngine.getProcessEngineConfiguration();
        Context.setProcessEngineConfiguration((ProcessEngineConfigurationImpl) processEngineConfiguration);

        ProcessDiagramGenerator diagramGenerator = processEngineConfiguration.getProcessDiagramGenerator();
        ProcessDefinitionEntity definitionEntity = (ProcessDefinitionEntity)repositoryService.getProcessDefinition(processInstance.getProcessDefinitionId());

        List<HistoricActivityInstance> highLightedActivitList =  historyService.createHistoricActivityInstanceQuery().processInstanceId(processInstanceId).orderByHistoricActivityInstanceStartTime()
                .asc().list();
        //高亮环节id集合
        List<String> highLightedActivitis = new ArrayList<String>();
        for (HistoricActivityInstance tempActivity : highLightedActivitList) {
            String activityId = tempActivity.getActivityId();
            highLightedActivitis.add(activityId);
        }

        //高亮线路id集合
        List<String> highLightedFlows = getHighLightedFlows(definitionEntity,highLightedActivitList);

        ExecutionEntity execution = (ExecutionEntity) runtimeService.createExecutionQuery().executionId(processInstanceId).singleResult();
        if(null != execution) {
            highLightedActivitis.add(execution.getActivityId());
        }

        InputStream imageStream = diagramGenerator.generateDiagram(bpmnModel, "png", highLightedActivitis,highLightedFlows,"宋体","宋体", "宋体",null,1.0);

        response.setHeader("Content-Type","image/png");
        response.setHeader("Cache-Control", "no-store, no-cache");
        BufferedImage bufferedImage =  ImageIO.read(imageStream);
        ImageIO.write(bufferedImage, "png", response.getOutputStream());

    }
    /**
     * 获取需要高亮的线
     * @param processDefinitionEntity
     * @param historicActivityInstances
     * @return
     */
    private List<String> getHighLightedFlows(ProcessDefinitionEntity processDefinitionEntity,
            List<HistoricActivityInstance> historicActivityInstances) {
        List<String> highFlows = new ArrayList<>();
        for (int i = 0; i < historicActivityInstances.size() - 1; i++) {
            ActivityImpl activityImpl = processDefinitionEntity.findActivity(historicActivityInstances.get(i).getActivityId());
            List<ActivityImpl> sameStartTimeNodes = new ArrayList<>();
            ActivityImpl sameActivityImpl1 = processDefinitionEntity.findActivity(historicActivityInstances.get(i + 1).getActivityId());
            sameStartTimeNodes.add(sameActivityImpl1);
            for (int j = i + 1; j < historicActivityInstances.size() - 1; j++) {
                HistoricActivityInstance activityImpl1 = historicActivityInstances.get(j);
                HistoricActivityInstance activityImpl2 = historicActivityInstances.get(j + 1);
                if (Math.abs(activityImpl1.getStartTime().getTime()-activityImpl2.getStartTime().getTime()) < 200) {
                    ActivityImpl sameActivityImpl2 = processDefinitionEntity.findActivity(activityImpl2.getActivityId());
                    sameStartTimeNodes.add(sameActivityImpl2);
                } else {
                    break;
                }
            }
            for(int j = i+1; j < historicActivityInstances.size() - 1; j++){
                HistoricActivityInstance activityImpl1 = historicActivityInstances.get(j);
                if(null != historicActivityInstances.get(i).getEndTime()){
                    if (Math.abs(activityImpl1.getStartTime().getTime()-historicActivityInstances.get(i).getEndTime().getTime()) < 200) {
                        ActivityImpl sameActivityImpl2 = processDefinitionEntity.findActivity(activityImpl1.getActivityId());
                        sameStartTimeNodes.add(sameActivityImpl2);
                    }
                }
            }
            List<PvmTransition> pvmTransitions = activityImpl.getOutgoingTransitions();
            for (PvmTransition pvmTransition : pvmTransitions) {
                ActivityImpl pvmActivityImpl = (ActivityImpl) pvmTransition.getDestination();
                if (sameStartTimeNodes.contains(pvmActivityImpl)) {
                    highFlows.add(pvmTransition.getId());
                }
            }
        }
        return highFlows;
    }


    public PageData<ProcessInstanceDTO> getHistoryProcessInstancePage(Map<String, Object> params) {
        HistoricProcessInstanceQuery query = historyService.createHistoricProcessInstanceQuery();
        Integer curPage = 1;
        Integer limit = 10;
        if(params.get(Constant.PAGE) != null){
            curPage = Integer.parseInt((String)params.get(Constant.PAGE));
        }
        if(params.get(Constant.LIMIT) != null){
            limit = Integer.parseInt((String)params.get(Constant.LIMIT));
        }

        if(StringUtils.isNotEmpty((String)params.get("processInstanceId"))){
            query.processInstanceId((String)params.get("processInstanceId"));
        }

        if(StringUtils.isNotEmpty((String)params.get("businessKey"))){
            query.processInstanceBusinessKey((String)params.get("businessKey"));
        }

        if(StringUtils.isNotEmpty((String)params.get("processDefinitionId"))){
            query.processDefinitionId((String)params.get("processDefinitionId"));
        }
        if(StringUtils.isNotEmpty((String)params.get("ended"))){
            if("true".equals((String)params.get("ended"))){
                query.finished();
            } else if("false".equals((String)params.get("ended"))){
                query.unfinished();
            }
        }
        if(null != params.get("finishedBeginTime")){
            query.finishedAfter((Date) params.get("finishedBeginTime"));
        }
        if(null != params.get("finishedEndTime")){
            query.finishedBefore((Date) params.get("finishedEndTime"));
        }
        if(null != params.get("startBeginTime")){
            query.startedAfter((Date) params.get("startBeginTime"));
        }
        if(null != params.get("startEndTime")){
            query.startedBefore((Date) params.get("startEndTime"));
        }
        if(StringUtils.isNotEmpty((String)params.get("startBy"))){
            query.startedBy((String)params.get("startBy"));
        }
        query.orderByProcessInstanceStartTime().desc();
        List<HistoricProcessInstance> list =  query.listPage((curPage-1)*limit, limit);
        List<ProcessInstanceDTO> listInstance = new ArrayList<>();
        if(!list.isEmpty()){
            this.converHistoricProcessInstance(list, listInstance);
        }

        return new PageData<ProcessInstanceDTO>(listInstance, (int) query.count());
    }

    private void converHistoricProcessInstance(List<HistoricProcessInstance> list, List<ProcessInstanceDTO> listInstance) {
        for(HistoricProcessInstance historicProcessInstance : list){
            ProcessInstanceDTO dto = new ProcessInstanceDTO();
            dto.setProcessDefinitionId(historicProcessInstance.getProcessDefinitionId());
            dto.setProcessInstanceId(historicProcessInstance.getId());
            dto.setProcessDefinitionVersion(historicProcessInstance.getProcessDefinitionVersion());
            dto.setProcessDefinitionName(historicProcessInstance.getProcessDefinitionName());
            dto.setProcessDefinitionKey(historicProcessInstance.getProcessDefinitionKey());
            if(null != historicProcessInstance.getEndTime()){
                dto.setEnded(true);
            } else {
                dto.setEnded(false);
            }
            dto.setEndTime(historicProcessInstance.getEndTime());
            dto.setStartTime(historicProcessInstance.getStartTime());
            dto.setBusinessKey(historicProcessInstance.getBusinessKey());
            dto.setCreateUserId(historicProcessInstance.getStartUserId());
            listInstance.add(dto);
        }

    }

    /**
     * 我发起的流程
     * 根据登录用户信息获取处理中的示例和任务信息
     * @param params
     * @return
     */
    public PageData<ProcessInstanceDTO> getMyProcessInstancePage(Map<String, Object> params) {
        params.put("startBy", SecurityUser.getUserId().toString());
        PageData<ProcessInstanceDTO> pageData = this.getHistoryProcessInstancePage(params);
        List<ProcessInstanceDTO> list = pageData.getList();
        for(ProcessInstanceDTO dto : list){
            if(dto.isEnded()){
                continue;
            }
            List<Task> listTask = taskService.createTaskQuery().processInstanceId(dto.getProcessInstanceId()).list();
            List<TaskDTO> taskDTOList = new ArrayList<>();
            for(Task task : listTask){
                TaskDTO taskDTO = new TaskDTO();
                this.convertTaskInfo(task, taskDTO);
                if(StringUtils.isNotEmpty(taskDTO.getAssignee())){
                    SysUserDTO userDTO = sysUserService.get(Long.valueOf(taskDTO.getAssignee()));
                    taskDTO.setAssigneeName(userDTO.getRealName());
                }
                taskDTOList.add(taskDTO);
            }
            dto.setCurrentTaskList(taskDTOList);
        }
        return pageData;
    }

    /**
     * 转换Task对象
     * @param task
     * @param dto
     */
    private void convertTaskInfo(Task task, TaskDTO dto) {
        dto.setTaskId(task.getId());
        dto.setTaskName(task.getName());
        dto.setActivityId(task.getExecutionId());
        dto.setAssignee(task.getAssignee());
        dto.setProcessDefinitionId(task.getProcessDefinitionId());
        dto.setProcessInstanceId(task.getProcessInstanceId());
        dto.setOwner(task.getOwner());
        dto.setCreateTime(task.getCreateTime());
    }

    public ProcessInstanceDTO getHistoryProcessInstanceByBusinessKey(String procDefKey, String businessKey) {
        HistoricProcessInstanceQuery query = historyService.createHistoricProcessInstanceQuery();
        if(StringUtils.isNotEmpty(businessKey)){
            query.processInstanceBusinessKey(businessKey);
        }

        if(StringUtils.isNotEmpty(procDefKey)){
            query.processDefinitionKey(procDefKey);
        }
        List<HistoricProcessInstance> list =  query.list();
        List<ProcessInstanceDTO> listInstance = new ArrayList<>();
        if(!list.isEmpty()){
            this.converHistoricProcessInstance(list, listInstance);
        } else {
            return null;
        }
        return listInstance.get(0);
    }
}
