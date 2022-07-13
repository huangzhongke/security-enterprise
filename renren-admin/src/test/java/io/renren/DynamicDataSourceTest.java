/**
 * Copyright (c) 2018 人人开源 All rights reserved.
 *
 * https://www.renren.io
 *
 * 版权所有，侵权必究！
 */

package io.renren;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import io.renren.modules.spider.menu.entity.Account;
import io.renren.modules.spider.menu.service.AccountService;
import io.renren.modules.spider.utils.MyUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;
import java.util.Map;

/**
 * 多数据源测试
 *
 * @author Mark sunlightcs@gmail.com
 */
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class DynamicDataSourceTest {

    @Autowired
    AccountService accountService;

    @Test
    public void test(){
        String s = MyUtils.readJsonFile("account.json");
        List<Map<String, Object>> accountList = JSONObject.parseObject(s, new TypeReference<List<Map<String, Object>>>() {
        });
        for (Map<String, Object> map : accountList) {
            Account account = new Account();
            account.setUser(map.get("username").toString());
            account.setPassword(map.get("password").toString());
            account.setPayPassword(map.get("pay").toString());
            account.setOrderAccount(false);
            if(map.get("ip")!=null){
                account.setAgentIp(map.get("ip").toString());
            }
            accountService.insert(account);
        }
        System.out.println("插入完毕");

    }

}
