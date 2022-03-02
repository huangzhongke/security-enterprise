package io.renren.modules.activiti.demo.controller;

import io.renren.common.annotation.LogOperation;
import io.renren.common.constant.Constant;
import io.renren.common.page.PageData;
import io.renren.common.utils.Result;
import io.renren.common.validator.AssertUtils;
import io.renren.common.validator.ValidatorUtils;
import io.renren.common.validator.group.AddGroup;
import io.renren.common.validator.group.DefaultGroup;
import io.renren.common.validator.group.UpdateGroup;
import io.renren.modules.activiti.demo.dto.CorrectionDTO;
import io.renren.modules.activiti.demo.service.CorrectionService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import java.util.HashMap;
import java.util.Map;


/**
 * 转正申请
 *
 * @author Mark sunlightcs@gmail.com
 */
@RestController
@RequestMapping("act/demo/correction")
@Api(tags="转正申请")
public class CorrectionController {
    @Autowired
    private CorrectionService correctionService;

    @GetMapping("page")
    @ApiOperation("分页")
    @ApiImplicitParams({
        @ApiImplicitParam(name = Constant.PAGE, value = "当前页码，从1开始", paramType = "query", required = true, dataType="int") ,
        @ApiImplicitParam(name = Constant.LIMIT, value = "每页显示记录数", paramType = "query",required = true, dataType="int") ,
        @ApiImplicitParam(name = Constant.ORDER_FIELD, value = "排序字段", paramType = "query", dataType="String") ,
        @ApiImplicitParam(name = Constant.ORDER, value = "排序方式，可选值(asc、desc)", paramType = "query", dataType="String")
    })
    @RequiresPermissions("activiti:correction:all")
    public Result<PageData<CorrectionDTO>> page(@ApiIgnore @RequestParam Map<String, Object> params){
        PageData<CorrectionDTO> page = correctionService.page(params);

        return new Result<PageData<CorrectionDTO>>().ok(page);
    }

    @GetMapping("{id}")
    @ApiOperation("信息")
    @RequiresPermissions("activiti:correction:all")
    public Result<CorrectionDTO> get(@PathVariable("id") Long id){
        CorrectionDTO data = correctionService.get(id);

        return new Result<CorrectionDTO>().ok(data);
    }

    @PostMapping
    @ApiOperation("保存")
    @LogOperation("保存")
    @RequiresPermissions("activiti:correction:all")
    public Result save(@RequestBody CorrectionDTO dto){
        //效验数据
        ValidatorUtils.validateEntity(dto, AddGroup.class, DefaultGroup.class);

        correctionService.save(dto);
        Map<String, Object> map = new HashMap<>();
        map.put("businessKey", dto.getId().toString());
        return new Result().ok(map);
    }

    @PutMapping
    @ApiOperation("修改")
    @LogOperation("修改")
    @RequiresPermissions("activiti:correction:all")
    public Result update(@RequestBody CorrectionDTO dto){
        //效验数据
        ValidatorUtils.validateEntity(dto, UpdateGroup.class, DefaultGroup.class);

        correctionService.update(dto);

        return new Result();
    }

    @DeleteMapping
    @ApiOperation("删除")
    @LogOperation("删除")
    @RequiresPermissions("activiti:correction:all")
    public Result delete(@RequestBody Long[] ids){
        //效验数据
        AssertUtils.isArrayEmpty(ids, "id");

        correctionService.delete(ids);

        return new Result();
    }

    @PostMapping("updateInstanceId")
    @ApiOperation("更新实例ID")
    @LogOperation("更新实例ID")
    @RequiresPermissions("activiti:correction:all")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "businessKey", value = "业务KEY", paramType = "query", required = true, dataType="String"),
        @ApiImplicitParam(name = "processInstanceId", value = "实例ID", paramType = "query",required = true, dataType="String")
    })
    public Result updateInstanceId(String businessKey, String processInstanceId){
        Long id = Long.valueOf(businessKey);
        correctionService.updateInstanceId(processInstanceId, id);
        return new Result();
    }

}