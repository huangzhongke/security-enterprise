package io.renren.modules.spider.service.impl;

import io.renren.common.service.impl.BaseServiceImpl;
import io.renren.modules.spider.dao.OrderDao;
import io.renren.modules.spider.entity.OrderEntity;
import io.renren.modules.spider.service.OrderService;
import org.springframework.stereotype.Service;

/**
 * @author kee
 * @version 1.0
 * @date 2022/3/2 18:46
 */
@Service
public class OrderServiceImpl extends BaseServiceImpl<OrderDao, OrderEntity> implements OrderService {

}
