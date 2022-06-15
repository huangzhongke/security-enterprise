package io.renren.modules.spider.service;

import io.renren.common.page.PageData;
import io.renren.common.service.BaseService;
import io.renren.modules.spider.entity.OrderEntity;

import java.util.List;
import java.util.Map;

/**
 * 
 *
 * @author kee 1059308740@qq.com.com
 * @since 1.0.0 2022-03-02
 */
public interface OrderService extends BaseService<OrderEntity> {
    PageData<OrderEntity> page(Map<String, Object> params);

    OrderEntity get(Long id);

    List<OrderEntity> list();
}