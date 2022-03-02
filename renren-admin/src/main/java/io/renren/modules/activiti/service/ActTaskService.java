/**
 * Copyright (c) 2019 人人开源 All rights reserved.
 *
 * https://www.renren.io
 *
 * 版权所有，侵权必究！
 */

package io.renren.modules.activiti.service;

import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import io.renren.common.constant.Constant;
import io.renren.common.exception.ErrorCode;
import io.renren.common.exception.RenException;
import io.renren.common.page.PageData;
import io.renren.common.service.impl.BaseServiceImpl;
import io.renren.common.utils.MessageUtils;
import io.renren.modules.activiti.dto.TaskDTO;
import io.renren.modules.security.user.SecurityUser;
import io.renren.modules.sys.service.SysRoleUserService;
import org.activiti.engine.*;
import org.activiti.engine.delegate.Expression;
import org.activiti.engine.history.HistoricActivityInstance;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.impl.RepositoryServiceImpl;
import org.activiti.engine.impl.bpmn.behavior.ParallelMultiInstanceBehavior;
import org.activiti.engine.impl.bpmn.behavior.UserTaskActivityBehavior;
import org.activiti.engine.impl.persistence.entity.ExecutionEntity;
import org.activiti.engine.impl.persistence.entity.ProcessDefinitionEntity;
import org.activiti.engine.impl.pvm.PvmTransition;
import org.activiti.engine.impl.pvm.process.ActivityImpl;
import org.activiti.engine.impl.pvm.process.TransitionImpl;
import org.activiti.engine.impl.task.TaskDefinition;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.activiti.engine.task.TaskInfo;
import org.activiti.engine.task.TaskQuery;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * 任务管理
 *
 * @author Jone
 */
@Service
public class ActTaskService extends BaseServiceImpl {
    @Autowired
    protected TaskService taskService;
    @Autowired
    protected HistoryService historyService;
    @Autowired
    protected RepositoryService repositoryService;
    @Autowired
    protected RuntimeService runtimeService;
    @Autowired
    private SysRoleUserService sysRoleUserService;

    /**
     * 根据参数获取当前运行的任务信息
     *
     * @param params
     * @return
     */
    public PageData<TaskDTO> page(Map<String, Object> params) {
        String userId = (String) params.get("userId");
        Integer curPage = 1;
        Integer limit = 10;
        if (params.get(Constant.PAGE) != null) {
            curPage = Integer.parseInt((String) params.get(Constant.PAGE));
        }
        if (params.get(Constant.LIMIT) != null) {
            limit = Integer.parseInt((String) params.get(Constant.LIMIT));
        }
        TaskQuery taskQuery = taskService.createTaskQuery();
        if (StringUtils.isNotEmpty(userId)) {
            taskQuery.taskAssignee(userId);
        }
        if (StringUtils.isNotEmpty((String)params.get("taskName"))){
            taskQuery.taskNameLike("%"+(String)params.get("taskName")+"%");
        }
        if(StringUtils.isNotEmpty((String)params.get("isRoleGroup"))&&"1".equals(params.get("isRoleGroup"))){
            List<Long> listRoles = sysRoleUserService.getRoleIdList(SecurityUser.getUserId());
            List<String> listStr = new ArrayList<>();
            for(Long role : listRoles){
                listStr.add(role.toString());
            }
            listStr.add(SecurityUser.getUserId().toString());
            if(!listStr.isEmpty()){
                taskQuery.taskCandidateGroupIn(listStr);
            } else {
                return new PageData<>(new ArrayList<>(), 0);
            }
        }
        taskQuery.orderByTaskCreateTime().desc();
        List<Task> list = taskQuery.listPage((curPage - 1) * limit, limit);
        List<TaskDTO> listDto = new ArrayList<>();
        for (Task task : list) {
            TaskDTO dto = new TaskDTO();
            this.convertTaskInfo(task, dto);
            listDto.add(dto);
        }
        return new PageData<>(listDto, (int) taskQuery.count());
    }

    /**
     * 获取任务详情信息
     *
     * @param id
     * @return
     */
    public TaskDTO taskDetail(String id) {
        Task task = taskService.createTaskQuery().taskId(id).singleResult();
        TaskDTO dto = new TaskDTO();
        this.convertTaskInfo(task, dto);
        return dto;
    }

    /**
     * 签收任务
     *
     * @param taskId
     */
    public void claimTask(String taskId) {
        String userId = SecurityUser.getUserId().toString();
        Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
        if(StringUtils.isNotEmpty(task.getAssignee())){
            throw new RenException(ErrorCode.TASK_CLIME_FAIL);
        }
        taskService.claim(taskId, userId);
    }

    /**
     * 释放任务
     *
     * @param taskId
     */
    public void unclaimTask(String taskId) {
        Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
        String procInstId = task.getProcessInstanceId();
        String processDefinitionId = historyService.createHistoricProcessInstanceQuery().processInstanceId(procInstId)
                .singleResult().getProcessDefinitionId();
        ProcessDefinitionEntity def = (ProcessDefinitionEntity) ((RepositoryServiceImpl) repositoryService).getDeployedProcessDefinition(processDefinitionId);
        ExecutionEntity execution = (ExecutionEntity) runtimeService.createProcessInstanceQuery().processInstanceId(procInstId).singleResult();
        String activitiId = execution.getActivityId();
        List<ActivityImpl> activitiList = def.getActivities();
        List<String> lstGroupId = new ArrayList<>();
        for (ActivityImpl activityImpl : activitiList) {
            if (activitiId.equals(activityImpl.getId())) {
                TaskDefinition taskDef = ((UserTaskActivityBehavior) activityImpl.getActivityBehavior()).getTaskDefinition();
                Set<Expression> groupIds = taskDef.getCandidateGroupIdExpressions();
                for (Expression exp : groupIds) {
                    lstGroupId.add(exp.getExpressionText());
                }
            }
        }
        if(lstGroupId.isEmpty()){
            throw new RenException(ErrorCode.UNCLAIM_ERROR_MESSAGE);
        }
        taskService.unclaim(taskId);
    }

    /**
     * 处理任务
     *
     * @param taskId
     * @param comment
     */
    public void completeTask(String taskId, String comment) {
        String userId = SecurityUser.getUserId().toString();
        Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
        if(StringUtils.isNotEmpty(task.getAssignee())){
            taskService.setAssignee(taskId, userId);
        }
        if(StringUtils.isNotEmpty(comment)){
            taskService.addComment(taskId, task.getProcessInstanceId(), comment);
        }
        taskService.complete(taskId);
    }

    /**
     * 任务转办
     *
     * @param taskId
     * @param depositorId
     * @param assignee
     */
    public void changeTaskAssignee(String taskId, String depositorId, String assignee) {
        taskService.setOwner(taskId, depositorId);
        taskService.setAssignee(taskId, assignee);
    }

    /**
     * 获取流程参数
     *
     * @param taskId
     * @param variableName
     * @return
     */
    public Map<String, Object> getTaskVariables(String taskId, String variableName) {
        Map<String, Object> map = null;
        if (StringUtils.isNotBlank(variableName)) {
            Object value = taskService.getVariable(taskId, variableName);
            if(null != value){
                map = new HashMap<>();
                map.put(variableName, value);
            }
        } else {
            map = taskService.getVariables(taskId);
        }
        return map;
    }

    /**
     * 更新任务变量
     * @param taskDTO
     */
    public void updateTaskVariable(TaskDTO taskDTO) {
        taskService.setVariables(taskDTO.getTaskId(), taskDTO.getParams());
    }


    /**
     * 根据任务ID判断是否为多实例任务
     * @param taskId
     * @return
     */
    private boolean isMultiInstance(String taskId) {
        boolean flag = false;
        Task task=taskService.createTaskQuery().taskId(taskId).singleResult();
        if(task != null){
            // 获取流程定义id
            String processDefinitionId=task.getProcessDefinitionId();
            ProcessDefinitionEntity processDefinitionEntity=(ProcessDefinitionEntity) repositoryService.getProcessDefinition(processDefinitionId);
            // 根据活动id获取活动实例
            ActivityImpl activityImpl=processDefinitionEntity.findActivity(task.getTaskDefinitionKey());
            if((activityImpl).getActivityBehavior() instanceof ParallelMultiInstanceBehavior){
                ParallelMultiInstanceBehavior behavior = (ParallelMultiInstanceBehavior)activityImpl.getActivityBehavior();
                if(behavior != null && behavior.getCollectionExpression() != null){
                    flag = true;
                }
            }
        }

        return flag;
    }

    /**
     * 删除任务下的所有变量
     *
     * @param taskId
     * @return
     */
    public void deleteTaskVariables(String taskId) {
        Collection<String> currentVariables = taskService.getVariablesLocal(taskId).keySet();
        taskService.removeVariables(taskId, currentVariables);
    }

    public void deleteTaskVariable(String taskId, String variableName, String scope) {
        if (StringUtils.isNotEmpty(scope)) {
            if ("global".equals(scope.toLowerCase())) {
                Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
                runtimeService.removeVariable(task.getExecutionId(), variableName);
            } else if ("local".equals(scope.toLowerCase())) {
                taskService.removeVariable(taskId, variableName);
            }
        } else {
            taskService.removeVariable(taskId, variableName);
        }
    }

    /**
     * 任务回退至上一用户任务节点
     * @param taskId
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public void doBackPreviousTask(String taskId, String comment) {
        Map<String, Object> variables = null;
        HistoricTaskInstance currTask = historyService.createHistoricTaskInstanceQuery().taskId(taskId).singleResult();
        if(this.isMultiInstance(taskId)){
            throw new RenException(ErrorCode.BACK_PROCESS_PARALLEL_ERROR);
        }
        ProcessInstance processInstance = runtimeService.createProcessInstanceQuery().processInstanceId(currTask.getProcessInstanceId()).singleResult();
        ProcessDefinitionEntity processDefinitionEntity = (ProcessDefinitionEntity) ((RepositoryServiceImpl) repositoryService)
                .getDeployedProcessDefinition(currTask.getProcessDefinitionId());
        ActivityImpl currActivity = processDefinitionEntity.findActivity(currTask.getTaskDefinitionKey());
        List<ActivityImpl>  canBackActivitys = new ArrayList<>();
        this.getCanBackUpActivitys(currActivity, canBackActivitys);
        if(canBackActivitys.isEmpty()) {
            throw new RenException(ErrorCode.SUPERIOR_NOT_EXIST);
        }
        List<PvmTransition> originPvmTransitionList = new ArrayList<PvmTransition>();
        List<PvmTransition> pvmTransitionList = currActivity.getOutgoingTransitions();
        for (PvmTransition pvmTransition : pvmTransitionList) {
            originPvmTransitionList.add(pvmTransition);
        }
        pvmTransitionList.clear();
        List<HistoricActivityInstance> historicActivityInstances = historyService
                .createHistoricActivityInstanceQuery().activityType("userTask")
                .processInstanceId(processInstance.getId())
                .finished().orderByHistoricActivityInstanceEndTime().desc().list();
        List<ActivityImpl> backActivitys = new ArrayList<>();
        for(HistoricActivityInstance historicActivityInstance: historicActivityInstances){
            for(ActivityImpl activity : canBackActivitys){
                if(historicActivityInstance.getActivityId().equals(activity.getId())){
                    boolean flag = false;
                    for(ActivityImpl activity1 : backActivitys){
                        if(activity.getId().equals(activity1.getId())){
                            flag = true;
                            break;
                        }
                    }
                    if(!flag){
                        backActivitys.add(activity);
                    }
                }
            }
        }

        if(backActivitys.isEmpty()){
            throw new RenException(ErrorCode.SUPERIOR_NOT_EXIST);
        }
        List<TransitionImpl> transitionList = new ArrayList<>();
        for(ActivityImpl activity : backActivitys) {
            TransitionImpl transition = currActivity.createOutgoingTransition(IdWorker.get32UUID());
            transition.setDestination(activity);
            transitionList.add(transition);
        }

        variables = processInstance.getProcessVariables();
        List<Task> tasks = taskService.createTaskQuery().processInstanceId(processInstance.getId())
                .taskDefinitionKey(currTask.getTaskDefinitionKey()).list();
        for (Task task : tasks) {
            String commentMode = MessageUtils.getMessage(ErrorCode.ROLLBACK_MESSAGE);
            if(StringUtils.isNotEmpty(comment)){
                commentMode += "[" +comment+"]";
            }
            taskService.addComment(task.getId(), task.getProcessInstanceId(), commentMode);
            taskService.complete(task.getId(), variables);
        }
        currActivity.getOutgoingTransitions().clear();
        for (PvmTransition pvmTransition : originPvmTransitionList) {
            currActivity.getOutgoingTransitions().add(pvmTransition);
        }
        for(ActivityImpl activity : backActivitys) {
            List<PvmTransition> incomingTransitions = activity.getIncomingTransitions();
            Iterator<PvmTransition> iterator = incomingTransitions.iterator();
            while (iterator.hasNext()) {
                PvmTransition pvmTransition = iterator.next();
                for(TransitionImpl transition : transitionList) {
                    if(transition.getId().equals(pvmTransition.getId())){
                        iterator.remove();
                    }
                }
            }
        }
    }

    private void getCanBackUpActivitys(ActivityImpl currActivity, List<ActivityImpl> rtnList) {
        List<PvmTransition> incomingTransitions = currActivity.getIncomingTransitions();
        for(PvmTransition pvmTransition : incomingTransitions){
            TransitionImpl transitionImpl = (TransitionImpl) pvmTransition;
            ActivityImpl activityImpl = transitionImpl.getSource();
            String type = (String) activityImpl.getProperty("type");
            if ("parallelGateway".equals(type)){
                // 并行路线
                if(activityImpl.getOutgoingTransitions().size() > 1){
                    throw new RenException(ErrorCode.BACK_PROCESS_HANDLEING_ERROR);
                }
                this.getCanBackUpActivitys(activityImpl, rtnList);
            } else if ("startEvent".equals(type)) {
                return;
            } else if ("userTask".equals(type)) {
                rtnList.add(activityImpl);
            } else if ("exclusiveGateway".equals(type)) {
                this.getCanBackUpActivitys(activityImpl, rtnList);
            } else if("inclusiveGateway".equals(type)) {
                if(activityImpl.getOutgoingTransitions().size() > 1){
                    return;
                }
                this.getCanBackUpActivitys(activityImpl, rtnList);
            }
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public void endProcess(String taskId, String comment) {
        if(isMultiInstance(taskId)){
            throw new RenException(ErrorCode.END_PROCESS_PARALLEL_ERROR);
        }
        Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
        List<Task> tasks = taskService.createTaskQuery().processInstanceId(task.getProcessInstanceId())
                .taskDefinitionKey(task.getTaskDefinitionKey()).list();
        if(tasks.size() > 1){
            throw new RenException(ErrorCode.END_PROCESS_HANDLEING_ERROR);
        }
        ActivityImpl endActivity = findActivitiImpl(taskId, "end");
        if (endActivity == null) {
            return;
        }
        if (StringUtils.isEmpty(endActivity.getId())) {
            if (!StringUtils.isEmpty(task.getOwner())) {
                taskService.resolveTask(task.getId());
            }
            taskService.addComment(task.getId(), task.getProcessInstanceId(), comment);
            taskService.complete(taskId);
        } else {
            ActivityImpl currActivity = findActivitiImpl(taskId, null);
            List<PvmTransition> oriPvmTransitionList = new ArrayList<PvmTransition>();
            List<PvmTransition> pvmTransitionList = currActivity
                    .getOutgoingTransitions();
            for (PvmTransition pvmTransition : pvmTransitionList) {
                oriPvmTransitionList.add(pvmTransition);
            }
            pvmTransitionList.clear();
            TransitionImpl newTransition = currActivity.createOutgoingTransition();
            ActivityImpl pointActivity = findActivitiImpl(taskId, endActivity.getId());
            newTransition.setDestination(pointActivity);
            if (StringUtils.isNotEmpty(task.getOwner())) {
                taskService.resolveTask(task.getId());
            }
            String message =MessageUtils.getMessage(ErrorCode.END_PROCESS_MESSAGE);
            comment = message + "["+ comment+ "]";
            taskService.addComment(task.getId(), task.getProcessInstanceId(), comment);
            taskService.complete(taskId);
            pointActivity.getIncomingTransitions().remove(newTransition);
            List<PvmTransition> pvmTransitionListC = currActivity.getOutgoingTransitions();
            pvmTransitionListC.clear();
            for (PvmTransition pvmTransition : oriPvmTransitionList) {
                pvmTransitionListC.add(pvmTransition);
            }
        }

    }

    private ActivityImpl findActivitiImpl(String taskId, String activityId) {
        Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
        if (task == null) {
            return null;
        }
        ProcessDefinitionEntity processDefinition = (ProcessDefinitionEntity) ((RepositoryServiceImpl) repositoryService).getDeployedProcessDefinition(task.getProcessDefinitionId());
        if (processDefinition == null) {
            throw new RenException(ErrorCode.NONE_EXIST_PROCESS);
        }
        if (StringUtils.isEmpty(activityId)) {
            activityId = task.getTaskDefinitionKey();
        }
        if ("END".equals(activityId.toUpperCase())) {
            for (ActivityImpl activityImpl : processDefinition.getActivities()) {
                String type = (String)activityImpl.getProperty("type");
                if("endEvent".equals(type)){
                    return activityImpl;
                }
            }
        }
        ActivityImpl activityImpl = processDefinition.findActivity(activityId);
        return activityImpl;
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
        ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery().processDefinitionId(task.getProcessDefinitionId()).singleResult();
        dto.setProcessDefinitionName(processDefinition.getName());
        dto.setProcessDefinitionKey(processDefinition.getKey());
        HistoricProcessInstance processInstance =  historyService.createHistoricProcessInstanceQuery().processInstanceId(task.getProcessInstanceId()).singleResult();
        dto.setStartTime(processInstance.getStartTime());
        dto.setBusinessKey(processInstance.getBusinessKey());
        dto.setProcessInstanceId(task.getProcessInstanceId());
        dto.setOwner(task.getOwner());
        dto.setCreateTime(task.getCreateTime());
        dto.setDueDate(task.getDueDate());
    }

    /**
     * 驳回至第一个用户任务
     * @param taskId
     */
    @Transactional(rollbackFor = Exception.class)
    public void backToFirst(String taskId, String comment) {
        if(this.isMultiInstance(taskId)){
            throw new RenException(ErrorCode.REJECT_PROCESS_PARALLEL_ERROR);
        }
        Map variables = null;
        HistoricTaskInstance currTask = historyService.createHistoricTaskInstanceQuery().taskId(taskId).singleResult();
        ProcessInstance processInstance = runtimeService.createProcessInstanceQuery().processInstanceId(currTask.getProcessInstanceId()).singleResult();
        ProcessDefinitionEntity processDefinitionEntity = (ProcessDefinitionEntity) ((RepositoryServiceImpl) repositoryService)
                .getDeployedProcessDefinition(currTask.getProcessDefinitionId());
        if (processDefinitionEntity == null) {
            throw new RenException(ErrorCode.NONE_EXIST_PROCESS);
        }
        List<Task> tasks = taskService.createTaskQuery().processInstanceId(currTask.getProcessInstanceId())
                .taskDefinitionKey(currTask.getTaskDefinitionKey()).list();
        if(tasks.size() > 1){
            throw new RenException(ErrorCode.REJECT_PROCESS_HANDLEING_ERROR);
        }
        ActivityImpl currActivity = processDefinitionEntity.findActivity(currTask.getTaskDefinitionKey());
        List<PvmTransition> originPvmTransitionList = new ArrayList<>();
        List<PvmTransition> pvmTransitionList = currActivity.getOutgoingTransitions();
        for (PvmTransition pvmTransition : pvmTransitionList) {
            originPvmTransitionList.add(pvmTransition);
        }
        pvmTransitionList.clear();
        List<HistoricActivityInstance> historicActivityInstances = historyService
                .createHistoricActivityInstanceQuery().activityType("userTask")
                .processInstanceId(processInstance.getId())
                .finished().orderByHistoricActivityInstanceEndTime().asc().list();
        TransitionImpl transitionImpl = null;
        if (historicActivityInstances.size() > 0) {
            ActivityImpl lastActivity = processDefinitionEntity.findActivity(historicActivityInstances.get(0).getActivityId());
            transitionImpl = currActivity.createOutgoingTransition();
            transitionImpl.setDestination(lastActivity);
        } else {
            throw new RenException(ErrorCode.SUPERIOR_NOT_EXIST);
        }
        variables = processInstance.getProcessVariables();
        for (Task task : tasks) {
            String commentMode = MessageUtils.getMessage(ErrorCode.REJECT_MESSAGE);
            if(StringUtils.isNotEmpty(comment)){
                commentMode += "[" + comment+"]";
            }
            taskService.addComment(task.getId(), task.getProcessInstanceId(), commentMode);
            taskService.complete(task.getId(), variables);
        }
        currActivity.getOutgoingTransitions().remove(transitionImpl);

        for (PvmTransition pvmTransition : originPvmTransitionList) {
            pvmTransitionList.add(pvmTransition);
        }
    }

    public void completeTaskByVariables(TaskDTO taskDTO) {
        if(null != taskDTO.getParams()){
            Set keySet = taskDTO.getParams().keySet();
            Iterator iterator = keySet.iterator();
            while(iterator.hasNext()){
                String key = (String)iterator.next();
                this.setTaskVariable(taskDTO.getTaskId(),key , taskDTO.getParams().get(key));
            }
        }
        this.completeTask(taskDTO.getTaskId(), taskDTO.getComment());
    }

    private void setTaskVariable(String taskId, String key, Object value){
        TaskInfo taskInfo = taskService.createTaskQuery().taskId(taskId).singleResult();
        runtimeService.setVariable(taskInfo.getExecutionId(), key, value);
    }
}
