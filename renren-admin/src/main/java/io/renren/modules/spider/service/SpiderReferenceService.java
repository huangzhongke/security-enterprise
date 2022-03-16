package io.renren.modules.spider.service;

import io.renren.common.service.BaseService;
import io.renren.modules.spider.entity.Line;
import io.renren.modules.spider.entity.SpiderReference;

import java.util.List;

/**
 * @author kee
 * @version 1.0
 * @date 2022/3/15 16:41
 */
public interface SpiderReferenceService extends BaseService<SpiderReference> {

    List<SpiderReference> getListByLineId(Long lineId);

    void saveReferences(Line line,Long id);

    void updateReferencesByLindId(Line line);

    void deleteByLineId(Long id);
}