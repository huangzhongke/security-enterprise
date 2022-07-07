package io.renren.modules.spider.menu.dao;

import io.renren.common.dao.BaseDao;
import io.renren.modules.spider.menu.entity.AccountLine;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 * 中间表 Mapper 接口
 * </p>
 *
 * @author kee
 * @since 2022-02-22
 */
@Mapper
public interface AccountLineDao extends BaseDao<AccountLine> {

}
