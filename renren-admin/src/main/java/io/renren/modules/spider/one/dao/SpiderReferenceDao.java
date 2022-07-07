package io.renren.modules.spider.one.dao;

import io.renren.common.dao.BaseDao;
import io.renren.modules.spider.one.entity.SpiderReference;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @author kee
 * @version 1.0
 * @date 2022/3/15 16:40
 */
@Mapper
public interface SpiderReferenceDao extends BaseDao<SpiderReference> {

    List<String> getSuccessListByLineId(Long lineId);

    List<String> getFailListByLineId(Long lineId);
}
