package io.renren.modules.spider.menu.service;

import io.renren.common.service.BaseService;
import io.renren.modules.spider.menu.entity.Account;

import java.util.List;

/**
 * <p>
 * 账号 服务类
 * </p>
 *
 * @author kee
 * @since 2022-02-22
 */
public interface AccountService extends BaseService<Account> {
  List<Account> getList();
  Account getAccountByUserName(String userName);

  List<Account> getAccountListByIsOrderAccount(Boolean isOrderAccount);
}
