/**
 * Copyright (c) 2018 人人开源 All rights reserved.
 *
 * https://www.renren.io
 *
 * 版权所有，侵权必究！
 */

package io.renren;

import io.renren.modules.spider.dao.OrderDao;
import io.renren.modules.spider.entity.OrderEntity;
import io.renren.service.DynamicDataSourceTestService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;

/**
 * 多数据源测试
 *
 * @author Mark sunlightcs@gmail.com
 */
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class DynamicDataSourceTest {
    @Autowired
    private DynamicDataSourceTestService dynamicDataSourceTestService;
    @Autowired
    OrderDao orderDao;
    @Test
    public void test(){
        OrderEntity order = OrderEntity.builder()
                .startPort("NINGBO")
                .endPort("DAR ES SALAAM")
                .equipment("40HQ")
                .quantity(1)
                .orderSleepTime(0)
                .orderDate(new Date())
                .isProxy(false)
                .isNeedLineName(true)
                .isNeedSupplierName(true)
                .vessel("NORTHERN VALENCE")
                .voyage("226W")
                .supplierName("EAST AFRICA SERVICE 4 (DAR ES SALAAM)")
                .account(0)
                .etd(10)
                .reference("NB2BJ8517600")
                .build();
        orderDao.insert(order);
    }

}
