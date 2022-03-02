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
import io.renren.common.page.PageData;
import io.renren.common.utils.Result;
import io.renren.modules.activiti.dto.ProcessInstanceDTO;
import io.renren.modules.activiti.dto.ProcessStartDTO;
import io.renren.modules.activiti.service.ActRunningService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import java.util.Map;

/**
 * 运行中的流程
 *
 * @author Mark sunlightcs@gmail.com
 */
@RestController
@RequestMapping("/act/running")
@Api(tags="运行中的流程")
public class ActRunningController {
    @Autowired
    private ActRunningService actRunningService;

    @GetMapping("page")
    @ApiOperation("分页")
    @ApiImplicitParams({
        @ApiImplicitParam(name = Constant.PAGE, value = "当前页码，从1开始", paramType = "query", required = true, dataType="int") ,
        @ApiImplicitParam(name = Constant.LIMIT, value = "每页显示记录数", paramType = "query",required = true, dataType="int") ,
        @ApiImplicitParam(name = "id", value = "实例ID", paramType = "query", dataType="String"),
        @ApiImplicitParam(name = "definitionKey", value = "definitionKey", paramType = "query", dataType="String")
    })
    @RequiresPermissions("sys:running:all")
    public Result<PageData<Map<String, Object>>> page(@ApiIgnore @RequestParam Map<String, Object> params){
        PageData<Map<String, Object>> page = actRunningService.page(params);

        return new Result<PageData<Map<String, Object>>>().ok(page);
    }

    @DeleteMapping("{id}")
    @ApiOperation("删除")
    @LogOperation("删除")
    @RequiresPermissions("sys:running:all")
    @ApiImplicitParam(name = "id", value = "ID", paramType = "query", dataType="String")
    public Result deleteInstance(@PathVariable("id") String id) {
        actRunningService.delete(id);
        return new Result();
    }

    @PostMapping("start")
    @ApiOperation("启动流程实例，依据流程定义KEY，启动流程实例")
    @LogOperation("启动流程实例，依据流程定义KEY，启动流程实例")
    @ApiImplicitParam(name = "key", value = "流程定义标识key", paramType = "query", dataType="String")
    @RequiresPermissions("sys:running:all")
    public Result<ProcessInstanceDTO> start(String key){
        ProcessInstanceDTO dto = actRunningService.startProcess(key);
        return new Result().ok(dto);
    }

    @PostMapping("startOfBusinessKey")
    @ApiOperation("启动流程实例，依据流程定义ID和业务唯一标示启动实例")
    @LogOperation("启动流程实例，依据流程定义ID和业务唯一标示启动实例")
    @RequiresPermissions("sys:running:all")
    public Result<ProcessInstanceDTO> startOfBusinessKey(@RequestBody ProcessStartDTO processStartDTO){
        ProcessInstanceDTO dto = actRunningService.startOfBusinessKey(processStartDTO);
        return new Result().ok(dto);
    }

}