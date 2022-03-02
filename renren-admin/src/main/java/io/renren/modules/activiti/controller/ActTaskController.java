package io.renren.modules.activiti.controller;

import io.renren.common.annotation.LogOperation;
import io.renren.common.constant.Constant;
import io.renren.common.exception.ErrorCode;
import io.renren.common.page.PageData;
import io.renren.common.utils.Result;
import io.renren.modules.activiti.dto.TaskDTO;
import io.renren.modules.activiti.service.ActTaskService;
import io.renren.modules.security.user.SecurityUser;
import io.renren.modules.sys.dto.SysUserDTO;
import io.renren.modules.sys.service.SysUserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import java.util.Map;

/**
 * 任务管理
 * @author Jone
 */
@RestController
@RequestMapping("/act/task")
@Api(tags="任务管理")
public class ActTaskController {
    @Autowired
    private ActTaskService actTaskService;
    @Autowired
    private SysUserService sysUserService;

    /**
     * 获取用户任务列表
     * 根据用户ID或角色组获取任务信息
     * @return
     */
    @GetMapping("page")
    @ApiOperation("待办任务，默认查询所有待办任务。根据用户ID或角色ID查询个人或组的任务")
    @ApiImplicitParams({
        @ApiImplicitParam(name = Constant.PAGE, value = "当前页码，从1开始", paramType = "query", required = true, dataType="int") ,
        @ApiImplicitParam(name = Constant.LIMIT, value = "每页显示记录数", paramType = "query",required = true, dataType="int") ,
        @ApiImplicitParam(name = "roleIds", value = "roleIds", paramType = "query", dataType="String"),
        @ApiImplicitParam(name = "userId", value = "userId", paramType = "query", dataType="String"),
        @ApiImplicitParam(name = "isRoleGroup", value = "是否查询分组", paramType = "query", dataType="String")
    })
    @RequiresPermissions("sys:task:all")
    public Result<PageData<TaskDTO>> queryUserTaskPage(@ApiIgnore @RequestParam Map<String, Object> params){
        PageData<TaskDTO> page = actTaskService.page(params);
        return new Result<PageData<TaskDTO>>().ok(page);
    }

    /**
     * 我的待办列表
     * @return
     */
    @GetMapping("myToDoTaskPage")
    @ApiOperation("我的待办列表")
    @ApiImplicitParams({
        @ApiImplicitParam(name = Constant.PAGE, value = "当前页码，从1开始", paramType = "query", required = true, dataType="int") ,
        @ApiImplicitParam(name = Constant.LIMIT, value = "每页显示记录数", paramType = "query",required = true, dataType="int"),
        @ApiImplicitParam(name = "taskName", value = "任务名称", paramType = "query", dataType="String")
    })
    @RequiresPermissions("sys:task:all")
    public Result<PageData<TaskDTO>> myToDoTaskPage(@ApiIgnore @RequestParam Map<String, Object> params){
        params.put("userId", SecurityUser.getUserId().toString());
        PageData<TaskDTO> page = actTaskService.page(params);
        for(TaskDTO taskDTO : page.getList()){
            if(!StringUtils.isEmpty(taskDTO.getAssignee())){
                SysUserDTO userDTO = sysUserService.get(Long.valueOf(taskDTO.getAssignee()));
                taskDTO.setAssigneeName(userDTO.getRealName());
            }
        }
        return new Result<PageData<TaskDTO>>().ok(page);
    }

    /**
     * 获取任务详情
     */
    @GetMapping("task/{id}")
    @ApiOperation("获取任务详情")
    @LogOperation("获取任务详情")
    @RequiresPermissions("sys:task:all")
    public Result getTaskById(@PathVariable("id") String id){
        TaskDTO task = actTaskService.taskDetail(id);
        return new Result().ok(task);
    }

    /**
     * 认领任务
     */
    @PostMapping("claim")
    @ApiOperation("认领任务")
    @ApiImplicitParam(name = "taskId", value = "taskId", paramType = "query", dataType="String")
    @RequiresPermissions("sys:task:all")
    public Result claimTask(String taskId){
        if(StringUtils.isEmpty(taskId)){
            return new Result().error(ErrorCode.PARAMS_GET_ERROR);
        }
        actTaskService.claimTask(taskId);
        return new Result();
    }

    /**
     * 释放任务
     */
    @PostMapping("unclaim")
    @ApiOperation("释放任务")
    @ApiImplicitParam(name = "taskId", value = "任务ID", paramType = "query", dataType="String")
    @RequiresPermissions("sys:task:all")
    public Result unclaimTask(String taskId){
        if(StringUtils.isEmpty(taskId)){
            return new Result().error(ErrorCode.PARAMS_GET_ERROR);
        }
        actTaskService.unclaimTask(taskId);
        return new Result();
    }

    /**
     * 任务处理
     */
    @PostMapping("complete")
    @ApiOperation("任务处理（完成任务）")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "taskId", value = "任务ID", paramType = "query", dataType="String"),
        @ApiImplicitParam(name = "comment", value = "审批意见", paramType = "query", dataType="String")
    })
    @RequiresPermissions("sys:task:all")
    public Result completeTask(String taskId, String comment){
        if(StringUtils.isEmpty(taskId)){
            return new Result().error(ErrorCode.PARAMS_GET_ERROR);
        }
        actTaskService.completeTask(taskId, comment);
        return new Result();
    }
    /**
     * 带参数的任务处理
     */
    @PostMapping("completeByVariables")
    @ApiOperation("带参数的任务处理（完成任务）")
    @RequiresPermissions("sys:task:all")
    public Result completeTaskByVariables(@RequestBody TaskDTO taskDTO){
        if(StringUtils.isEmpty(taskDTO.getTaskId())){
            return new Result().error(ErrorCode.PARAMS_GET_ERROR);
        }
        actTaskService.completeTaskByVariables(taskDTO);
        return new Result();
    }

    /**
     * 任务委托
     */
    @PostMapping("entrust")
    @ApiOperation("任务委托")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "taskId", value = "任务ID", paramType = "query", dataType="String"),
        @ApiImplicitParam(name = "assignee", value = "受理人", paramType = "query", dataType="String")
    })
    @RequiresPermissions("sys:task:all")
    public Result taskEntrust(String taskId, String assignee){
        if(StringUtils.isEmpty(taskId) || StringUtils.isEmpty(assignee)){
            return new Result().error(ErrorCode.PARAMS_GET_ERROR);
        }
        String depositorId = SecurityUser.getUserId().toString();
        actTaskService.changeTaskAssignee(taskId, depositorId, assignee);
        return new Result();
    }

    /**
     * 获取流程变量
     */
    @GetMapping("getTaskVariables")
    @ApiOperation("获取流程变量")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "taskId", value = "当前任务ID", paramType = "query", dataType="String"),
        @ApiImplicitParam(name = "variableName", value = "参数的键", paramType = "query", dataType="String")
    })
    @RequiresPermissions("sys:task:all")
    public Result getTaskVariables(String taskId, String variableName){
        if(StringUtils.isEmpty(taskId) || StringUtils.isEmpty(variableName)){
            return new Result().error(ErrorCode.PARAMS_GET_ERROR);
        }
        return new Result().ok(actTaskService.getTaskVariables(taskId, variableName));
    }

    /**
     * 更新任务变量
     */
    @PostMapping("updateTaskVariable")
    @ApiOperation("更新任务变量")
    @RequiresPermissions("sys:task:all")
    public Result updateTaskVariable(@RequestBody TaskDTO taskDTO){
        if(StringUtils.isEmpty(taskDTO.getTaskId())){
            return new Result().error(ErrorCode.PARAMS_GET_ERROR);
        }
        actTaskService.updateTaskVariable(taskDTO);
        return new Result();
    }

    /**
     * 删除任务的所有变量
     */
    @DeleteMapping("deleteTaskVariables")
    @ApiOperation("删除任务的所有变量")
    @ApiImplicitParam(name = "taskId", value = "当前任务ID", paramType = "query", dataType="String")
    @RequiresPermissions("sys:task:all")
    public Result deleteTaskVariables(String taskId){
        if(StringUtils.isEmpty(taskId)){
            return new Result().error(ErrorCode.PARAMS_GET_ERROR);
        }
        actTaskService.deleteTaskVariables(taskId);
        return new Result();
    }

    /**
     * 删除指定变量
     */
    @DeleteMapping("deleteVariable")
    @ApiOperation("删除指定变量，默认删除本地变量")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "taskId", value = "当前任务ID", paramType = "query", dataType="String"),
        @ApiImplicitParam(name = "variableName", value = "变量名", paramType = "query", dataType="String"),
        @ApiImplicitParam(name = "scope", value = "变量的范围（local：本地；global,全局）", paramType = "query", dataType="String")
    })
    @RequiresPermissions("sys:task:all")
    public Result deleteVariable(String taskId, String variableName, String scope){
        if(StringUtils.isEmpty(taskId) || StringUtils.isEmpty(variableName)){
            return new Result().error(ErrorCode.PARAMS_GET_ERROR);
        }
        actTaskService.deleteTaskVariable(taskId, variableName, scope);
        return new Result();
    }

    /**
     * 回退任务到上一节点
     */
    @PostMapping("backPreviousTask")
    @ApiOperation("回退任务到上一节点")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "taskId", value = "任务ID", paramType = "query", dataType="String"),
        @ApiImplicitParam(name = "comment", value = "回退审核意见", paramType = "query", dataType="String")
    })
    @RequiresPermissions("sys:task:all")
    public Result backPreviousTask(String taskId, String comment){
        if(StringUtils.isEmpty(taskId)){
            return new Result().error(ErrorCode.PARAMS_GET_ERROR);
        }
        actTaskService.doBackPreviousTask(taskId, comment);
        return new Result();
    }

    /**
     * 终止流程
     */
    @PostMapping("endProcess")
    @ApiOperation("终止流程")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "taskId", value = "任务ID", paramType = "query", dataType="String"),
        @ApiImplicitParam(name = "comment", value = "终止审核意见", paramType = "query", dataType="String")
    })
    @RequiresPermissions("sys:task:all")
    public Result endProcess(String taskId, String comment){
        actTaskService.endProcess(taskId, comment);
        return new Result();
    }

    /**
     * 驳回
     */
    @PostMapping("backToFirst")
    @ApiOperation("驳回，回退至第一个用户任务")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "taskId", value = "任务ID", paramType = "query", dataType="String"),
        @ApiImplicitParam(name = "comment", value = "驳回审核意见", paramType = "query", dataType="String")
    })
    @RequiresPermissions("sys:task:all")
    public Result backToFirst(String taskId, String comment){
        if(StringUtils.isEmpty(taskId)){
            return new Result().error(ErrorCode.PARAMS_GET_ERROR);
        }
        actTaskService.backToFirst(taskId, comment);
        return new Result();
    }

}
