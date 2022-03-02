package io.renren.modules.spider.service.impl;

import io.renren.common.service.impl.BaseServiceImpl;
import io.renren.modules.spider.dao.AccountDao;
import io.renren.modules.spider.entity.Account;
import io.renren.modules.spider.service.AccountService;
import org.springframework.stereotype.Service;

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

}
