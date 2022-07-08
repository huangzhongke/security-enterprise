package io.renren.modules.spider.one.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.renren.common.service.impl.BaseServiceImpl;
import io.renren.modules.job.entity.ScheduleJobEntity;
import io.renren.modules.job.service.ScheduleJobService;
import io.renren.modules.spider.one.dao.LineDao;
import io.renren.modules.spider.menu.entity.Line;
import io.renren.modules.spider.one.service.LineService;
import io.renren.modules.spider.one.service.SpiderReferenceService;
import io.renren.modules.spider.oocl.dao.ChildAccountDao;
import io.renren.modules.spider.oocl.entity.ChildAccount;
import io.renren.modules.spider.oocl.service.ChildAccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 航线参数 服务实现类
 * </p>
 *
 * @author kee
 * @since 2022-02-22
 */
@Service
public class LineImpl extends BaseServiceImpl<LineDao, Line> implements LineService {

    @Autowired
    private SpiderReferenceService spiderReferenceService;

    @Autowired
    private ChildAccountDao childAccountDao;


    @Autowired
    LineDao lineDao;
    @Override
    public Long save(Line line) {
        lineDao.insert(line);
        return line.getId();
    }

    @Override
    public void deleteByScheduleJobIds(Long[] ids) {
        //删one 子表以及对应主表
        for (Long id : ids) {
            Line line = getByJobId(id);
            spiderReferenceService.deleteByLineId(line.getId());
            lineDao.deleteById(line.getId());
        }
    }

    @Override
    public void deleteByJobIds(Long[] ids) {
        //删除oocl 子表以及对应主表
        for (Long id : ids) {
            Line line = getByJobId(id);
            List<Long> childIds = childAccountDao.getIdsByLineID(line.getId());
            for (Long childId : childIds) {
                childAccountDao.deleteById(childId);
            }
            lineDao.deleteById(line.getId());
        }
    }

    @Override
    public Line getByJobId(Long id) {
        //通过 定时任务id 查询到 spider_line的数据
        LambdaQueryWrapper<Line> lambda = new QueryWrapper<Line>().lambda();
        lambda.eq(Line::getJobId,id);
        return baseDao.selectOne(lambda);
    }
}
