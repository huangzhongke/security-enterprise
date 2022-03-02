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
import io.renren.common.validator.ValidatorUtils;
import io.renren.modules.activiti.dto.ModelDTO;
import io.renren.modules.activiti.service.ActModelService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.activiti.engine.repository.Model;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * 模型管理
 *
 * @author Mark sunlightcs@gmail.com
 */
@RestController
@RequestMapping("/act/model")
@Api(tags="模型管理")
public class ActModelController {
    @Autowired
    private ActModelService actModelService;

    @GetMapping("page")
    @ApiOperation("分页")
    @ApiImplicitParams({
        @ApiImplicitParam(name = Constant.PAGE, value = "当前页码，从1开始", paramType = "query", required = true, dataType="int") ,
        @ApiImplicitParam(name = Constant.LIMIT, value = "每页显示记录数", paramType = "query",required = true, dataType="int") ,
        @ApiImplicitParam(name = "key", value = "key", paramType = "query", dataType="String"),
        @ApiImplicitParam(name = "name", value = "name", paramType = "query", dataType="String")
    })
    @RequiresPermissions("sys:model:all")
    public Result<PageData<Model>> page(@ApiIgnore @RequestParam Map<String, Object> params){
        PageData<Model> page = actModelService.page(params);

        return new Result<PageData<Model>>().ok(page);
    }

    @PostMapping
    @ApiOperation("新增模型")
    @LogOperation("新增模型")
    @RequiresPermissions("sys:model:all")
    public Result save(@RequestBody ModelDTO dto) throws Exception{
        //效验数据
        ValidatorUtils.validateEntity(dto);

        actModelService.save(dto.getName(), dto.getKey(), dto.getDescription());

        return new Result();
    }

    @PostMapping("deploy/{id}")
    @ApiOperation("部署")
    @LogOperation("部署")
    @RequiresPermissions("sys:model:all")
    public Result deploy(@PathVariable("id") String id) {
        actModelService.deploy(id);
        return new Result();
    }

    @GetMapping("export/{id}")
    @ApiOperation("导出")
    @LogOperation("导出")
    @RequiresPermissions("sys:model:all")
    public void export(@PathVariable("id") String id, @ApiIgnore HttpServletResponse response) {
        actModelService.export(id, response);
    }

    @DeleteMapping
    @ApiOperation("删除")
    @LogOperation("删除")
    @RequiresPermissions("sys:model:all")
    public Result delete(@RequestBody String[] ids) {
        for(String id : ids) {
            actModelService.delete(id);
        }
        return new Result();
    }

    @GetMapping("image/{deploymentId}")
    @ApiOperation(value = "查看流程图", produces="application/octet-stream")
    @LogOperation("查看流程图")
    @RequiresPermissions("sys:model:all")
    public void viewDeployImage(@PathVariable("deploymentId") String deploymentId, @ApiIgnore HttpServletResponse response){
        actModelService.deployImage(deploymentId, response);
    }
}