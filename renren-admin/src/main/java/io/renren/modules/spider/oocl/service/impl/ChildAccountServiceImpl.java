package io.renren.modules.spider.oocl.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.renren.common.service.impl.BaseServiceImpl;
import io.renren.modules.spider.menu.entity.Account;
import io.renren.modules.spider.menu.service.AccountService;
import io.renren.modules.spider.oocl.dao.ChildAccountDao;
import io.renren.modules.spider.oocl.entity.ChildAccount;
import io.renren.modules.spider.oocl.service.ChildAccountService;
import io.renren.modules.spider.utils.LoginUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ChildAccountServiceImpl extends BaseServiceImpl<ChildAccountDao, ChildAccount> implements ChildAccountService {

    @Autowired
    AccountService accountService;

    @Override
    public void saveByUserList(List<String> userList,String params,Long lineId) {
        for (String userName : userList) {
            Account account = accountService.getAccountByUserName(userName);
            Map<String, Object> map = new HashMap<>();
            map.put("username",account.getUser());
            map.put("password",account.getPassword());
            String agentIp = account.getAgentIp();
            boolean isProxy = agentIp == null ? false : true;
            if (null != agentIp){
                map.put("ip",account.getAgentIp());
            }
            Map<String, Object> login = LoginUtils.Login(map, isProxy);
            String cookie = login.get("cookie").toString();
            String token = login.get("token").toString();
            ChildAccount childAccount = ChildAccount.builder()
                    .params(params)
                    .cookie(cookie)
                    .token(token)
                    .ip(agentIp)
                    .lineId(lineId)
                    .build();
            baseDao.insert(childAccount);
        }
    }

    @Override
    public  List<Long> getIdsByLineID(Long lineId) {
        return baseDao.getIdsByLineID(lineId);
    }

    /**
     *
     * @param lineId 航线Id
     * @return 子账号列表
     */
    @Override
    public List<ChildAccount> getListByLineId(Long lineId) {
        QueryWrapper<ChildAccount> qw = new QueryWrapper<>();
        LambdaQueryWrapper<ChildAccount> lq = qw.lambda().eq(ChildAccount::getLineId, lineId);

        return baseDao.selectList(lq) ;
    }
}
