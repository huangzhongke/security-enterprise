/**
 * Copyright (c) 2018 人人开源 All rights reserved.
 *
 * https://www.renren.io
 *
 * 版权所有，侵权必究！
 */

package io.renren.modules.activiti.controller;

import io.renren.common.annotation.LogOperation;
import io.renren.common.constant.Constant;
import io.renren.common.exception.ErrorCode;
import io.renren.common.page.PageData;
import io.renren.common.utils.ConvertUtils;
import io.renren.common.utils.Result;
import io.renren.modules.activiti.dto.ProcessBizRouteAndProcessInstanceDTO;
import io.renren.modules.activiti.dto.ProcessBizRouteDTO;
import io.renren.modules.activiti.dto.ProcessInstanceDTO;
import io.renren.modules.activiti.service.ActHistoryService;
import io.renren.modules.activiti.service.ActProcessService;
import io.renren.modules.activiti.service.ProcessBizRouteService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

/**
 * 流程管理
 *
 * @author Mark sunlightcs@gmail.com
 */
@RestController
@RequestMapping("/act/process")
@Api(tags="流程管理")
public class ActProcessController {
    @Autowired
    private ActProcessService actProcessService;

    @Autowired
    private ProcessBizRouteService processBizRouteService;

    @Autowired
    private ActHistoryService historyService;

    @GetMapping("page")
    @ApiOperation("流程管理-分页")
    @ApiImplicitParams({
        @ApiImplicitParam(name = Constant.PAGE, value = "当前页码，从1开始", paramType = "query", required = true, dataType="int") ,
        @ApiImplicitParam(name = Constant.LIMIT, value = "每页显示记录数", paramType = "query",required = true, dataType="int") ,
        @ApiImplicitParam(name = "key", value = "key", paramType = "query", dataType="String"),
        @ApiImplicitParam(name = "processName", value = "processName", paramType = "query", dataType="String")
    })
    @RequiresPermissions("sys:process:all")
    public Result<PageData<Map<String, Object>>> page(@ApiIgnore @RequestParam Map<String, Object> params){
        PageData<Map<String, Object>> page = actProcessService.page(params);
        return new Result<PageData<Map<String, Object>>>().ok(page);
    }

    @GetMapping("lastestPage")
    @ApiOperation("发起流程-分页")
    @ApiImplicitParams({
        @ApiImplicitParam(name = Constant.PAGE, value = "当前页码，从1开始", paramType = "query", required = true, dataType="int") ,
        @ApiImplicitParam(name = Constant.LIMIT, value = "每页显示记录数", paramType = "query",required = true, dataType="int") ,
        @ApiImplicitParam(name = "key", value = "key", paramType = "query", dataType="String"),
        @ApiImplicitParam(name = "processName", value = "processName", paramType = "query", dataType="String")
    })
    @RequiresPermissions("sys:process:all")
    public Result<PageData<Map<String, Object>>> lastestPage(@ApiIgnore @RequestParam Map<String, Object> params){
        params.put("isLatestVersion", true);
        PageData<Map<String, Object>> page = actProcessService.page(params);
        return new Result<PageData<Map<String, Object>>>().ok(page);
    }

    @PostMapping("deploy")
    @ApiOperation("部署流程文件")
    @LogOperation("部署流程文件")
    @ApiImplicitParam(name = "processFile", value = "流程文件", paramType = "query", dataType="file")
    @RequiresPermissions("sys:process:all")
    public Result deploy(@RequestParam("processFile") MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            return new Result().error(ErrorCode.UPLOAD_FILE_EMPTY);
        }

        actProcessService.deploy(file);

        return new Result();
    }

    @PutMapping("active/{id}")
    @LogOperation("激活流程")
    @RequiresPermissions("sys:process:all")
    public Result active(@PathVariable("id") String id) {
        actProcessService.active(id);

        return new Result();
    }

    @PutMapping("suspend/{id}")
    @ApiOperation("挂起流程")
    @LogOperation("挂起流程")
    @RequiresPermissions("sys:process:all")
    public Result suspend(@PathVariable("id") String id) {
        actProcessService.suspend(id);

        return new Result();
    }

    @PostMapping("convertToModel/{id}")
    @ApiOperation("将部署的流程转换为模型")
    @LogOperation("将部署的流程转换为模型")
    @RequiresPermissions("sys:process:all")
    public Result convertToModel(@PathVariable("id") String id) throws Exception {
        actProcessService.convertToModel(id);

        return new Result();
    }

    @DeleteMapping
    @ApiOperation("删除流程")
    @LogOperation("删除流程")
    @RequiresPermissions("sys:process:all")
    public Result delete(@RequestBody String[] deploymentIds) {
        for(String deploymentId : deploymentIds) {
            actProcessService.deleteDeployment(deploymentId);
        }
        return new Result();
    }

    @GetMapping(value = "resource")
    @ApiOperation(value="获取资源文件", produces="application/octet-stream")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "deploymentId", value = "部署ID", paramType = "query", dataType="String"),
        @ApiImplicitParam(name = "resourceName", value = "资源名称", paramType = "query", dataType="String")
    })
    @RequiresPermissions("sys:process:all")
    public void resource(String deploymentId, String resourceName, @ApiIgnore HttpServletResponse response) throws Exception {
        InputStream resourceAsStream = actProcessService.getResourceAsStream(deploymentId, resourceName);
        String[] fileNames = resourceName.split("\\.");
        if(fileNames.length>1){
            if(fileNames[fileNames.length-1].toLowerCase().equals("png")){
                response.setHeader("Content-Type","image/png");
            } else if(fileNames[fileNames.length-1].toLowerCase().equals("xml")){
                response.setHeader("Content-Type", "text/xml");
                response.setHeader("Content-Disposition", "attachment; filename=" + java.net.URLEncoder.encode(resourceName, "UTF-8"));
            }
        }

        response.setHeader("Cache-Control", "no-store, no-cache");
        IOUtils.copy(resourceAsStream, response.getOutputStream());
    }

    @GetMapping(value = "getProcDefBizRoute/{id}")
    @ApiOperation("根据流程ID获取业务路由配置")
    public Result getProcDefBizRoute(@PathVariable("id") String id){
        ProcessBizRouteDTO processBizRouteDTO = processBizRouteService.getProcDefBizRoute(id);
        return new Result().ok(processBizRouteDTO);
    }

    @GetMapping(value = "getLatestProcDefBizRoute")
    @ApiOperation("根据流程定义KEY获取最新的流程配置信息")
    @ApiImplicitParam(name = "procDefKey", value = "流程定义KEY", paramType = "query", dataType="String")
    public Result getLatestProcDefBizRoute(String procDefKey){
        ProcessBizRouteDTO processBizRouteDTO = processBizRouteService.getLatestProcDefBizRoute(procDefKey);
        return new Result().ok(processBizRouteDTO);
    }

    @GetMapping(value = "getProcDefBizRouteAndProcessInstance")
    @ApiOperation("根据业务ID获取流程业务路由配置和实例信息")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "procDefKey", value = "流程定义KEY", required = true, paramType = "query", dataType="String"),
        @ApiImplicitParam(name = "businessKey", value = "业务KEY", required = true, paramType = "query", dataType="String")
    })
    public Result getProcDefBizRouteAndProcessInstance(String procDefKey, String businessKey) {
        if(StringUtils.isEmpty(businessKey)){
            return new Result().error(ErrorCode.PARAMS_GET_ERROR);
        }
        if(StringUtils.isEmpty(procDefKey)){
            return new Result().error(ErrorCode.PARAMS_GET_ERROR);
        }
        ProcessInstanceDTO processInstanceDTO = historyService.getHistoryProcessInstanceByBusinessKey(procDefKey, businessKey);
        if(null == processInstanceDTO){
            return new Result();
        }
        ProcessBizRouteDTO processBizRouteDTO = processBizRouteService.getProcDefBizRoute(processInstanceDTO.getProcessDefinitionId());
        ProcessBizRouteAndProcessInstanceDTO dto = ConvertUtils.sourceToTarget(processBizRouteDTO, ProcessBizRouteAndProcessInstanceDTO.class);
        if(null != dto) {
            dto.setProcessDefinitionId(processInstanceDTO.getProcessDefinitionId());
            dto.setProcessDefinitionKey(procDefKey);
            dto.setProcessDefinitionName(processInstanceDTO.getProcessDefinitionName());
            dto.setProcessInstanceId(processInstanceDTO.getProcessInstanceId());
        }
        return new Result().ok(dto);
    }

    @PostMapping("saveProcBizRoute")
    @ApiOperation("保存业务路由配置")
    public Result saveProcBizRoute(@RequestBody ProcessBizRouteDTO processBizRouteDTO){
        processBizRouteService.save(processBizRouteDTO);
        return new Result();
    }

    @PutMapping("saveProcBizRoute")
    @ApiOperation("更新业务路由配置")
    public Result updateProcBizRoute(@RequestBody ProcessBizRouteDTO processBizRouteDTO){
        processBizRouteService.updateProcBizRoute(processBizRouteDTO);
        return new Result();
    }

}