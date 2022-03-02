package io.renren.modules.spider.service.impl;

import io.renren.common.service.impl.BaseServiceImpl;
import io.renren.modules.spider.dao.AccountLineDao;
import io.renren.modules.spider.entity.AccountLine;
import io.renren.modules.spider.service.AccountLineService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;

/**
 * <p>
 * 中间表 服务实现类
 * </p>
 *
 * @author kee
 * @since 2022-02-22
 */
@Service
public class AccountLineImpl extends BaseServiceImpl<AccountLineDao, AccountLine> implements AccountLineService {

    @Autowired
    AccountLineDao accountLineDao;
    @Override
    public void deleteBatch(Long[] ids) {
        accountLineDao.deleteBatchIds(Arrays.asList(ids));
    }
}
