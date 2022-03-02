package io.renren.modules.spider.service.impl;

import io.renren.common.service.impl.BaseServiceImpl;
import io.renren.modules.spider.dao.LineDao;
import io.renren.modules.spider.entity.Line;
import io.renren.modules.spider.service.LineService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
    LineDao lineDao;
    @Override
    public Long save(Line line) {
        lineDao.insert(line);
        return line.getId();
    }
}
