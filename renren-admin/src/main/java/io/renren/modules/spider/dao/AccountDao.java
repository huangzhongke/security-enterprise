package io.renren.modules.spider.dao;

import io.renren.common.dao.BaseDao;
import io.renren.modules.spider.entity.Account;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 * 账号 Mapper 接口
 * </p>
 *
 * @author kee
 * @since 2022-02-22
 */
    @Mapper
    public interface AccountDao extends BaseDao<Account> {

}
