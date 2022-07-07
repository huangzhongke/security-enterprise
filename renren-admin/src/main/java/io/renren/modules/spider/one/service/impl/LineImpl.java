package io.renren.modules.spider.one.service.impl;

import com.alibaba.fastjson.JSONObject;
import io.renren.common.service.impl.BaseServiceImpl;
import io.renren.modules.job.entity.ScheduleJobEntity;
import io.renren.modules.job.service.ScheduleJobService;
import io.renren.modules.spider.one.dao.LineDao;
import io.renren.modules.spider.menu.entity.Line;
import io.renren.modules.spider.one.service.LineService;
import io.renren.modules.spider.one.service.SpiderReferenceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
    private ScheduleJobService scheduleJobService;

    @Autowired
    private SpiderReferenceService spiderReferenceService;
    @Autowired
    LineDao lineDao;
    @Override
    public Long save(Line line) {
        lineDao.insert(line);
        return line.getId();
    }

    @Override
    public void deleteByScheduleJobIds(Long[] ids) {
        for (Long id : ids) {
            ScheduleJobEntity job = scheduleJobService.selectById(id);
            Map<String, Object> map = JSONObject.parseObject(job.getParams());
            Long lineId = Long.parseLong(map.get("id").toString());
            spiderReferenceService.deleteByLineId(lineId);
            lineDao.deleteById(lineId);
        }
    }
}