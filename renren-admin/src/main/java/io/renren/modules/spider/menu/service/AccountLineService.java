package io.renren.modules.spider.menu.service;

import io.renren.common.service.BaseService;
import io.renren.modules.spider.menu.entity.AccountLine;

/**
 * <p>
 * 中间表 服务类
 * </p>
 *
 * @author kee
 * @since 2022-02-22
 */
public interface AccountLineService extends BaseService<AccountLine> {
    void deleteBatch(Long[] ids);
}
