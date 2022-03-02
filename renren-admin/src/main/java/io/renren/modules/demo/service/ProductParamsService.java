package io.renren.modules.demo.service;

import io.renren.common.service.BaseService;
import io.renren.modules.demo.dto.ProductParamsDTO;
import io.renren.modules.demo.entity.ProductParamsEntity;

import java.util.List;

/**
 * 产品参数管理
 *
 * @author Mark sunlightcs@gmail.com
 */
public interface ProductParamsService extends BaseService<ProductParamsEntity> {

    void saveOrUpdate(Long productId, List<ProductParamsDTO> list);

    void deleteByProductIds(Long[] productIds);

    List<ProductParamsDTO> getList(Long productId);
}