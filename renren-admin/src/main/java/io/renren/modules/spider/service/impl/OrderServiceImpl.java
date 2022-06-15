package io.renren.modules.spider.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import io.renren.common.page.PageData;
import io.renren.common.service.impl.BaseServiceImpl;
import io.renren.modules.spider.dao.OrderDao;
import io.renren.modules.spider.entity.OrderEntity;
import io.renren.modules.spider.service.OrderService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * @author kee
 * @version 1.0
 * @date 2022/3/2 18:46
 */
@Service
public class OrderServiceImpl extends BaseServiceImpl<OrderDao, OrderEntity> implements OrderService {
    @Override
    public PageData<OrderEntity> page(Map<String, Object> params) {
        IPage<OrderEntity> page = baseDao.selectPage(
                getPage(params, "order_date", false),
                getWrapper(params)
        );

        return getPageData(page, OrderEntity.class);
    }
    private QueryWrapper<OrderEntity> getWrapper(Map<String, Object> params){
        String id = (String)params.get("id");

        QueryWrapper<OrderEntity> wrapper = new QueryWrapper<>();
        wrapper.eq(StringUtils.isNotBlank(id), "id", id);

        return wrapper;
    }
    @Override
    public OrderEntity get(Long id) {
        return baseDao.selectById(id);
    }

    @Override
    public List<OrderEntity> list() {
        return baseDao.selectList(null);
    }


}
