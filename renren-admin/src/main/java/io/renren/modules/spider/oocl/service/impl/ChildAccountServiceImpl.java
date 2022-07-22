package io.renren.modules.spider.oocl.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.renren.common.service.impl.BaseServiceImpl;
import io.renren.modules.spider.menu.service.AccountService;
import io.renren.modules.spider.oocl.dao.ChildAccountDao;
import io.renren.modules.spider.oocl.entity.ChildAccount;
import io.renren.modules.spider.oocl.service.ChildAccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class ChildAccountServiceImpl extends BaseServiceImpl<ChildAccountDao, ChildAccount> implements ChildAccountService {

    @Autowired
    AccountService accountService;


    @Override
    public void saveByUserList(List<String> userList, Long lineId) {
        for (String userName : userList) {
            ChildAccount childAccount = ChildAccount.builder()
                    .lineId(lineId)
                    .status(false)
                    .username(userName).build();
            baseDao.insert(childAccount);

        }
    }


    @Override
    public List<Long> getIdsByLineID(Long lineId) {
        return baseDao.getIdsByLineID(lineId);
    }

    /**
     * @param lineId 航线Id
     * @return 子账号列表
     */
    @Override
    public List<ChildAccount> getListByLineId(Long lineId) {
        QueryWrapper<ChildAccount> qw = new QueryWrapper<>();
        LambdaQueryWrapper<ChildAccount> lq = qw.lambda().eq(ChildAccount::getLineId, lineId);

        return baseDao.selectList(lq);
    }

    @Override
    public void deleteByLineId(Long lineId) {
        LambdaQueryWrapper<ChildAccount> lambda = new QueryWrapper<ChildAccount>().lambda();
        lambda.eq(ChildAccount::getLineId,lineId);
        baseDao.delete(lambda);
    }
}
