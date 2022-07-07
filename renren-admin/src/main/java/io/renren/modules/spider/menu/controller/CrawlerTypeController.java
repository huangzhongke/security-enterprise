package io.renren.modules.spider.menu.controller;

import io.renren.common.annotation.LogOperation;
import io.renren.common.constant.Constant;
import io.renren.common.page.PageData;
import io.renren.common.utils.Result;
import io.renren.common.validator.AssertUtils;
import io.renren.common.validator.ValidatorUtils;
import io.renren.common.validator.group.DefaultGroup;
import io.renren.common.validator.group.UpdateGroup;
import io.renren.modules.spider.menu.dto.CrawlerTypeDTO;
import io.renren.modules.spider.menu.service.CrawlerTypeService;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import java.util.Map;

/**
 * @author kee
 * @version 1.0
 * @date 2022/6/8 16:54
 */
@RestController
@RequestMapping("/spider/crawler")
public class CrawlerTypeController {

    @Autowired
    CrawlerTypeService crawlerTypeService;

    @GetMapping("page")
    @ApiImplicitParams({
            @ApiImplicitParam(name = Constant.PAGE, value = "当前页码，从1开始", paramType = "query", required = true, dataType="int") ,
            @ApiImplicitParam(name = Constant.LIMIT, value = "每页显示记录数", paramType = "query",required = true, dataType="int") ,
            @ApiImplicitParam(name = Constant.ORDER_FIELD, value = "排序字段", paramType = "query", dataType="String") ,
            @ApiImplicitParam(name = Constant.ORDER, value = "排序方式，可选值(asc、desc)", paramType = "query", dataType="String") ,
            @ApiImplicitParam(name = "crawlerType", value = "字典标签", paramType = "query", dataType="String"),
            @ApiImplicitParam(name = "crawlerName", value = "字典值", paramType = "query", dataType="String")
    })
    @RequiresPermissions("sys:schedule:crawler:list")
    public Result<PageData<CrawlerTypeDTO>> page(@ApiIgnore @RequestParam Map<String, Object> params){
        PageData<CrawlerTypeDTO> page = crawlerTypeService.page(params);
        return  new Result<PageData<CrawlerTypeDTO>>().ok(page);
    }

    @GetMapping("{id}")
    @ApiOperation("信息")
    @RequiresPermissions("sys:schedule:crawler:info")
    public Result<CrawlerTypeDTO> get(@PathVariable("id") Long id){
        CrawlerTypeDTO data = crawlerTypeService.get(id);

        return new Result<CrawlerTypeDTO>().ok(data);
    }

    @PostMapping
    @ApiOperation("保存")
    @LogOperation("保存")
    @RequiresPermissions("sys:schedule:crawler:save")
    public Result save(@RequestBody CrawlerTypeDTO dto){
        //效验数据
        ValidatorUtils.validateEntity(dto, DefaultGroup.class);

        if(crawlerTypeService.save(dto)){
            return new Result().ok(dto);
        }

        return new Result().error("保存失败,该Bean名称已经存在");
    }
    //@RequiresPermissions("sys:dict:update")
    @PutMapping
    @ApiOperation("修改")
    @LogOperation("修改")
    @RequiresPermissions("sys:schedule:crawler:update")
    public Result update(@RequestBody CrawlerTypeDTO dto){
        //效验数据
        ValidatorUtils.validateEntity(dto, UpdateGroup.class, DefaultGroup.class);

        if(crawlerTypeService.update(dto)){
            return new Result().ok(dto);
        }
        return new Result().error("更新失败,该Bean名称已经存在");
    }
    //@RequiresPermissions("sys:dict:delete")
    @DeleteMapping
    @ApiOperation("删除")
    @LogOperation("删除")
    @RequiresPermissions("sys:schedule:crawler:delete")
    public Result delete(@RequestBody Long[] ids){
        //效验数据
        AssertUtils.isArrayEmpty(ids, "id");

        crawlerTypeService.delete(ids);

        return new Result();
    }
}
