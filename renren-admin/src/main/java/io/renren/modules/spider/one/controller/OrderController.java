package io.renren.modules.spider.one.controller;

import io.renren.common.annotation.LogOperation;
import io.renren.common.constant.Constant;
import io.renren.common.page.PageData;
import io.renren.common.utils.ExcelUtils;
import io.renren.common.utils.Result;
import io.renren.modules.spider.one.entity.OrderEntity;
import io.renren.modules.spider.one.excel.OrderExcel;
import io.renren.modules.spider.one.service.OrderService;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

/**
 * @author kee
 * @version 1.0
 * @date 2022/6/13 16:04
 */
@RequestMapping("/sys/schedule/order")
@RestController
public class OrderController {
    @Autowired
    OrderService orderService;
    @GetMapping("page")
    @ApiOperation("分页")
    @ApiImplicitParams({
            @ApiImplicitParam(name = Constant.PAGE, value = "当前页码，从1开始", paramType = "query", required = true, dataType="int") ,
            @ApiImplicitParam(name = Constant.LIMIT, value = "每页显示记录数", paramType = "query",required = true, dataType="int") ,
            @ApiImplicitParam(name = Constant.ORDER_FIELD, value = "排序字段", paramType = "query", dataType="String") ,
            @ApiImplicitParam(name = Constant.ORDER, value = "排序方式，可选值(asc、desc)", paramType = "query", dataType="String") ,
            @ApiImplicitParam(name = "id", value = "id", paramType = "query", dataType="String")
    })
    @RequiresPermissions("sys:schedule:all")
    public Result<PageData<OrderEntity>> page(@ApiIgnore @RequestParam Map<String, Object> params){
        PageData<OrderEntity> page = orderService.page(params);

        return new Result<PageData<OrderEntity>>().ok(page);
    }

    @GetMapping("{id}")
    @ApiOperation("信息")
    @RequiresPermissions("sys:schedule:all")
    public Result<OrderEntity> info(@PathVariable("id") Long id){
        OrderEntity orderEntity = orderService.get(id);

        return new Result<OrderEntity>().ok(orderEntity);
    }

    @GetMapping("export")
    @ApiOperation("导出")
    @LogOperation("导出")
    @RequiresPermissions("sys:schedule:all")
    public void export(HttpServletResponse response) throws Exception {
        List<OrderEntity> list = orderService.list();

        ExcelUtils.exportExcelToTarget(response, null, list, OrderExcel.class);
    }
}
