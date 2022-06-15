package io.renren.modules.spider.dao;

import io.renren.common.dao.BaseDao;
import io.renren.modules.spider.entity.CrawlerType;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author kee
 * @version 1.0
 * @date 2022/6/8 16:51
 */
@Mapper
public interface CrawlerTypeDao extends BaseDao<CrawlerType> {
    CrawlerType getCrawlerTypeByCrawlerName(String crawlerName);
}
