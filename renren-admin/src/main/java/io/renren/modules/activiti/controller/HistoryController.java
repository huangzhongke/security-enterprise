package io.renren.modules.activiti.controller;

import io.renren.common.constant.Constant;
import io.renren.common.page.PageData;
import io.renren.common.utils.Result;
import io.renren.modules.activiti.dto.HistoryDetailDTO;
import io.renren.modules.activiti.dto.ProcessActivityDTO;
import io.renren.modules.activiti.dto.ProcessInstanceDTO;
import io.renren.modules.activiti.service.ActHistoryService;
import io.renren.modules.activiti.service.ActivitiService;
import io.renren.modules.sys.dto.SysUserDTO;
import io.renren.modules.sys.service.SysUserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

/**
 * 流程的历史信息
 * @author Jone
 */
@RestController
@RequestMapping("/act/his")
@Api(tags="流程历史")
public class HistoryController {
    @Autowired
    private ActHistoryService historyService;
    @Autowired
    private ActivitiService activitiService;
    @Autowired
    private SysUserService sysUserService;

    @GetMapping("getInstImage")
    @ApiOperation(value ="获取流程活动图", produces="application/octet-stream")
    @ApiImplicitParam(name = "processInstanceId", value = "流程实例ID", paramType = "query", dataType="String")
    @RequiresPermissions("sys:his:all")
    public void getProcessInstanceDiagram(String processInstanceId, @ApiIgnore HttpServletResponse response) throws Exception {
        historyService.getProcessInstanceDiagram(processInstanceId, response);
    }

    @GetMapping("getHistoryProcessInstancePage")
    @ApiOperation("历史流程实例列表")
    @ApiImplicitParams({
        @ApiImplicitParam(name = Constant.PAGE, value = "当前页码，从1开始", paramType = "query", required = true, dataType="int") ,
        @ApiImplicitParam(name = Constant.LIMIT, value = "每页显示记录数", paramType = "query",required = true, dataType="int") ,
        @ApiImplicitParam(name = "processInstanceId", value = "实例ID", paramType = "query", dataType="String"),
        @ApiImplicitParam(name = "businessKey", value = "业务KEY", paramType = "query", dataType="String"),
        @ApiImplicitParam(name = "processDefinitionId", value = "流程定义ID", paramType = "query", dataType="String"),
        @ApiImplicitParam(name = "finishedBeginTime", value = "流程完成开始时间", paramType = "query", dataType="Date"),
        @ApiImplicitParam(name = "finishedEndTime", value = "流程完成结束时间", paramType = "query", dataType="Date"),
        @ApiImplicitParam(name = "startBeginTime", value = "流程启动开始时间", paramType = "query", dataType="Date"),
        @ApiImplicitParam(name = "startEndTime", value = "流程启动结束时间", paramType = "query", dataType="Date"),
        @ApiImplicitParam(name = "ended", value = "是否完成(true, false)", paramType = "query", dataType="String")
    })
    @RequiresPermissions("sys:his:all")
    public Result<ProcessInstanceDTO> getHistoryProcessInstancePage(@ApiIgnore @RequestParam Map<String, Object> params){
        PageData<ProcessInstanceDTO> page = historyService.getHistoryProcessInstancePage(params);
        return new Result().ok(page);
    }

    @GetMapping("getMyProcessInstancePage")
    @ApiOperation("我发起的流程")
    @ApiImplicitParams({
        @ApiImplicitParam(name = Constant.PAGE, value = "当前页码，从1开始", paramType = "query", required = true, dataType="int") ,
        @ApiImplicitParam(name = Constant.LIMIT, value = "每页显示记录数", paramType = "query",required = true, dataType="int") ,
        @ApiImplicitParam(name = "processInstanceId", value = "实例ID", paramType = "query", dataType="String"),
        @ApiImplicitParam(name = "businessKey", value = "业务KEY", paramType = "query", dataType="String"),
        @ApiImplicitParam(name = "processDefinitionId", value = "流程定义ID", paramType = "query", dataType="String"),
        @ApiImplicitParam(name = "startBeginTime", value = "流程启动开始时间", paramType = "query", dataType="Date"),
        @ApiImplicitParam(name = "startEndTime", value = "流程启动结束时间", paramType = "query", dataType="Date"),
        @ApiImplicitParam(name = "ended", value = "是否接受（true:是，false:否）", paramType = "query", dataType="String")
    })
    @RequiresPermissions("sys:his:all")
    public Result<ProcessInstanceDTO> getMyProcessInstancePage(@ApiIgnore @RequestParam Map<String, Object> params){
        PageData<ProcessInstanceDTO> page = historyService.getMyProcessInstancePage(params);
        return new Result().ok(page);
    }

    @GetMapping("getMyHandledInstancePage")
    @ApiOperation("已办任务：根据登录账号查询用户已办任务")
    @ApiImplicitParams({
        @ApiImplicitParam(name = Constant.PAGE, value = "当前页码，从1开始", paramType = "query", required = true, dataType="int") ,
        @ApiImplicitParam(name = Constant.LIMIT, value = "每页显示记录数", paramType = "query",required = true, dataType="int") ,
        @ApiImplicitParam(name = "processInstanceId", value = "实例ID", paramType = "query", dataType="String"),
        @ApiImplicitParam(name = "businessKey", value = "业务KEY", paramType = "query", dataType="String"),
        @ApiImplicitParam(name = "processDefinitionId", value = "流程定义ID", paramType = "query", dataType="String"),
        @ApiImplicitParam(name = "startBeginTime", value = "流程启动开始时间", paramType = "query", dataType="Date"),
        @ApiImplicitParam(name = "startEndTime", value = "流程启动结束时间", paramType = "query", dataType="Date"),
        @ApiImplicitParam(name = "finishedBeginTime", value = "流程完成开始时间", paramType = "query", dataType="Date"),
        @ApiImplicitParam(name = "finishedEndTime", value = "流程完成结束时间", paramType = "query", dataType="Date")
    })
    @RequiresPermissions("sys:his:all")
    public Result<ProcessActivityDTO> getMyHandledInstancePage(@ApiIgnore @RequestParam Map<String, Object> params){
        PageData<ProcessActivityDTO> page = activitiService.getMyProcessInstancePage(params);
        for(ProcessActivityDTO activityDTO : page.getList()){
            if(StringUtils.isNotEmpty(activityDTO.getStartUserId())){
                SysUserDTO userDTO = sysUserService.get(Long.valueOf(activityDTO.getStartUserId()));
                activityDTO.setStartUserName(userDTO.getRealName());
            }
            if(StringUtils.isNotEmpty(activityDTO.getAssignee())){
                SysUserDTO userDTO = sysUserService.get(Long.valueOf(activityDTO.getAssignee()));
                activityDTO.setAssigneeName(userDTO.getRealName());
            }
        }
        return new Result().ok(page);
    }

    @GetMapping("getTaskHandleDetailInfo")
    @ApiOperation("获取流程处理详情")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "processInstanceId", value = "实例ID", paramType = "query", dataType="String")
    })
    @RequiresPermissions("sys:his:all")
    public Result<HistoryDetailDTO> getTaskHandleDetailInfo(String processInstanceId){
        List<HistoryDetailDTO> list = activitiService.getTaskHandleDetailInfo(processInstanceId);
        return new Result().ok(list);
    }

}