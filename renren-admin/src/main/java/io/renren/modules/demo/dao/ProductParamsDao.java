package io.renren.modules.demo.dao;

import io.renren.common.dao.BaseDao;
import io.renren.modules.demo.entity.ProductParamsEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * 产品参数管理
 *
 * @author Mark sunlightcs@gmail.com
 */
@Mapper
public interface ProductParamsDao extends BaseDao<ProductParamsEntity> {

    /**
     * 根据产品id，删除产品参数
     */
    void deleteByProductIds(Long[] productIds);
}