package io.renren.modules.spider.controller;


import io.renren.common.annotation.LogOperation;
import io.renren.common.utils.Result;
import io.renren.modules.spider.entity.Line;
import io.renren.modules.spider.service.LineService;
import io.renren.modules.spider.service.SpiderReferenceService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 * 航线参数 前端控制器
 * </p>
 *
 * @author kee
 * @since 2022-02-22
 */
@RestController
@RequestMapping("/spider/line")
public class LineController {

    @Autowired
    LineService lineService;

    @Autowired
    SpiderReferenceService spiderReferenceService;

    @PostMapping("save")
    @ApiOperation("保存")
    @LogOperation("保存")
    public Result save(@RequestBody Line line) {
        Long id = lineService.save(line);
        //保存小提单号
        spiderReferenceService.saveReferences(line,id);
        return new Result().ok(id);
    }

    @PutMapping("update")
    @ApiOperation("修改")
    @LogOperation("修改")
    public Result update(@RequestBody Line line) {
        lineService.updateById(line);
        spiderReferenceService.updateReferencesByLindId(line);
        return new Result().ok(line);
    }

}

