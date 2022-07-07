package io.renren.modules.spider.menu.service.impl;

import io.renren.common.service.impl.BaseServiceImpl;
import io.renren.modules.spider.menu.dao.AccountDao;
import io.renren.modules.spider.menu.entity.Account;
import io.renren.modules.spider.menu.service.AccountService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 账号 服务实现类
 * </p>
 *
 * @author kee
 * @since 2022-02-22
 */
@Service
public class AccountImpl extends BaseServiceImpl<AccountDao, Account> implements AccountService {

    @Override
    public List<Account> getList() {
        return baseDao.selectList(null);
    }
}
