package io.renren.modules.spider.one.dao;

import io.renren.common.dao.BaseDao;
import io.renren.modules.spider.menu.entity.Line;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * <p>
 * 航线参数 Mapper 接口
 * </p>
 *
 * @author kee
 * @since 2022-02-22
 */
@Mapper
public interface LineDao extends BaseDao<Line> {

}
