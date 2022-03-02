package io.renren.modules.spider.dao;

import io.renren.common.dao.BaseDao;
import io.renren.modules.spider.entity.OrderEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * 
 *
 * @author kee 1059308740@qq.com.com
 * @since 1.0.0 2022-03-02
 */
@Mapper
public interface OrderDao extends BaseDao<OrderEntity> {
	void saveOrder(OrderEntity order);
}