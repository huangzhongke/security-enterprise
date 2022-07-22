/**
 * Copyright (c) 2018 人人开源 All rights reserved.
 *
 * https://www.renren.io
 *
 * 版权所有，侵权必究！
 */

package io.renren.modules.job.controller;

import com.alibaba.fastjson.JSONObject;
import io.renren.common.annotation.LogOperation;
import io.renren.common.constant.Constant;
import io.renren.common.page.PageData;
import io.renren.common.utils.Result;
import io.renren.common.validator.ValidatorUtils;
import io.renren.common.validator.group.AddGroup;
import io.renren.common.validator.group.DefaultGroup;
import io.renren.modules.job.dto.ScheduleJobDTO;
import io.renren.modules.job.service.ScheduleJobService;
import io.renren.modules.spider.one.dto.DataFormDto;
import io.renren.modules.spider.one.service.LineService;
import io.renren.modules.spider.oocl.dto.MonitorDTO;
import io.renren.modules.spider.oocl.dto.OOCLDataFormDTO;
import io.renren.modules.spider.oocl.entity.MonitorData;
import io.renren.modules.spider.oocl.service.ChildAccountService;
import io.renren.modules.spider.oocl.service.MonitorDataService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import java.util.List;
import java.util.Map;

/**
 * 定时任务
 *
 * @author Mark sunlightcs@gmail.com
 */
@RestController
@RequestMapping("/sys/schedule")
@Api(tags="定时任务")
public class ScheduleJobController {
	@Autowired
	private ScheduleJobService scheduleJobService;

	@Autowired
	private LineService lineService;

	@Autowired
	MonitorDataService monitorDataService;
	@GetMapping("page")
	@ApiOperation("分页")
	@ApiImplicitParams({
		@ApiImplicitParam(name = Constant.PAGE, value = "当前页码，从1开始", paramType = "query", required = true, dataType="int") ,
		@ApiImplicitParam(name = Constant.LIMIT, value = "每页显示记录数", paramType = "query",required = true, dataType="int") ,
		@ApiImplicitParam(name = Constant.ORDER_FIELD, value = "排序字段", paramType = "query", dataType="String") ,
		@ApiImplicitParam(name = Constant.ORDER, value = "排序方式，可选值(asc、desc)", paramType = "query", dataType="String") ,
		@ApiImplicitParam(name = "beanName", value = "beanName", paramType = "query", dataType="String")
	})
	@RequiresPermissions("sys:schedule:all")
	public Result<PageData<ScheduleJobDTO>> page(@ApiIgnore @RequestParam Map<String, Object> params){
		PageData<ScheduleJobDTO> page = scheduleJobService.page(params);

		return new Result<PageData<ScheduleJobDTO>>().ok(page);
	}

	@GetMapping("{id}")
	@ApiOperation("信息")
	@RequiresPermissions("sys:schedule:all")
	public Result info(@PathVariable("id") Long id){

		ScheduleJobDTO schedule = scheduleJobService.get(id);
		if (schedule.getType() == 0) {
			DataFormDto dataFormDto = JSONObject.parseObject(schedule.getParams(), DataFormDto.class);
			dataFormDto.setBeanName(schedule.getBeanName());
			dataFormDto.setParams(schedule.getParams());
			dataFormDto.setCronExpression(schedule.getCronExpression());
			dataFormDto.setRemark(schedule.getRemark());
			dataFormDto.setStatus(schedule.getStatus());
			dataFormDto.setId(schedule.getId());
			dataFormDto.setType(schedule.getType());
			return new Result<DataFormDto>().ok(dataFormDto);
		}
		if (schedule.getType() == 1) {
			OOCLDataFormDTO dataFormDTO = JSONObject.parseObject(schedule.getParams(), OOCLDataFormDTO.class);
			dataFormDTO.setBeanName(schedule.getBeanName());
			dataFormDTO.setParams(schedule.getParams());
			dataFormDTO.setCronExpression(schedule.getCronExpression());
			dataFormDTO.setRemark(schedule.getRemark());
			dataFormDTO.setStatus(schedule.getStatus());
			dataFormDTO.setId(schedule.getId());
			dataFormDTO.setType(schedule.getType());
			return new Result<OOCLDataFormDTO>().ok(dataFormDTO);
		}
		if (schedule.getType() == 3) {
			MonitorDTO monitorDTO = JSONObject.parseObject(schedule.getParams(), MonitorDTO.class);
			monitorDTO.setBeanName(schedule.getBeanName());
			monitorDTO.setParams(schedule.getParams());
			monitorDTO.setCronExpression(schedule.getCronExpression());
			monitorDTO.setRemark(schedule.getRemark());
			monitorDTO.setStatus(schedule.getStatus());
			monitorDTO.setId(schedule.getId());
			monitorDTO.setType(schedule.getType());
			return new Result<MonitorDTO>().ok(monitorDTO);
		}

		return new Result<>();
	}

	@PostMapping
	@ApiOperation("保存")
	@LogOperation("保存")
	@RequiresPermissions("sys:schedule:all")
	public Result save(@RequestBody Map<String, Object> params){
		//@RequestBody DataFormDto dataForm
		if (params.get("tag").equals("one")){
			DataFormDto dataForm = JSONObject.parseObject(params.get("data").toString(),DataFormDto.class);
			ValidatorUtils.validateEntity(dataForm, AddGroup.class, DefaultGroup.class);
			scheduleJobService.save(dataForm);
		}
		if (params.get("tag").equals("oocl")){
			OOCLDataFormDTO dataFormDTO = JSONObject.parseObject(params.get("data").toString(), OOCLDataFormDTO.class);
			ValidatorUtils.validateEntity(dataFormDTO, AddGroup.class, DefaultGroup.class);
			scheduleJobService.save(dataFormDTO);
		}
		if (params.get("tag").equals("monitor")){
			MonitorDTO monitorDTO = JSONObject.parseObject(params.get("data").toString(), MonitorDTO.class);
			ValidatorUtils.validateEntity(monitorDTO, AddGroup.class, DefaultGroup.class);
			scheduleJobService.save(monitorDTO);
		}
		return new Result();
	}

	@PutMapping
	@ApiOperation("修改")
	@LogOperation("修改")
	@RequiresPermissions("sys:schedule:all")
	public Result update(@RequestBody Map<String, Object> params){

		if (params.get("tag").equals("one")){
			DataFormDto dataForm = JSONObject.parseObject(params.get("data").toString(),DataFormDto.class);
			ValidatorUtils.validateEntity(dataForm, AddGroup.class, DefaultGroup.class);
			scheduleJobService.update(dataForm);
		}
		if (params.get("tag").equals("oocl")){
			OOCLDataFormDTO dataFormDTO = JSONObject.parseObject(params.get("data").toString(), OOCLDataFormDTO.class);
			ValidatorUtils.validateEntity(dataFormDTO, AddGroup.class, DefaultGroup.class);
			scheduleJobService.update(dataFormDTO);
		}
		if (params.get("tag").equals("monitor")){
			MonitorDTO monitorDTO = JSONObject.parseObject(params.get("data").toString(), MonitorDTO.class);
			ValidatorUtils.validateEntity(monitorDTO, AddGroup.class, DefaultGroup.class);
			scheduleJobService.update(monitorDTO);
		}
		
		return new Result();
	}

	@DeleteMapping
	@ApiOperation("删除")
	@LogOperation("删除")
	@RequiresPermissions("sys:schedule:all")
	public Result delete(@RequestBody Long[] ids){
		Long id = ids[0];
		ScheduleJobDTO scheduleJobDTO = scheduleJobService.get(id);
		if(scheduleJobDTO.getType() == 0){
			lineService.deleteByScheduleJobIds(ids);

		}
		if (scheduleJobDTO.getType() == 1) {
			lineService.deleteByJobIds(ids);
		}
		if (scheduleJobDTO.getType() == 3) {
			monitorDataService.deleteByJobIds(ids);
		}
		scheduleJobService.deleteBatch(ids);
		return new Result();
	}

	@PutMapping("/run")
	@ApiOperation("立即执行")
	@LogOperation("立即执行")
	@RequiresPermissions("sys:schedule:all")
	public Result run(@RequestBody Long[] ids){
		scheduleJobService.run(ids);
		
		return new Result();
	}

	@PutMapping("/pause")
	@ApiOperation("暂停")
	@LogOperation("暂停")
	@RequiresPermissions("sys:schedule:all")
	public Result pause(@RequestBody Long[] ids){
		scheduleJobService.pause(ids);
		
		return new Result();
	}

	@PutMapping("/resume")
	@ApiOperation("恢复")
	@LogOperation("恢复")
	@RequiresPermissions("sys:schedule:all")
	public Result resume(@RequestBody Long[] ids){
		scheduleJobService.resume(ids);
		
		return new Result();
	}

}