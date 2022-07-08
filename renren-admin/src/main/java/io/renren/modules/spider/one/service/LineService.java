package io.renren.modules.spider.one.service;

import io.renren.common.service.BaseService;
import io.renren.modules.spider.menu.entity.Line;

/**
 * <p>
 * 航线参数 服务类
 * </p>
 *
 * @author kee
 * @since 2022-02-22
 */
public interface LineService extends BaseService<Line> {
    Long save(Line line);
    void  deleteByScheduleJobIds(Long[] ids);

    void  deleteByJobIds(Long[] ids);

    Line getByJobId(Long id);
}
