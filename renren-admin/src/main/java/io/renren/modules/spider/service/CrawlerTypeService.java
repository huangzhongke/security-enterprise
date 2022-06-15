package io.renren.modules.spider.service;

import io.renren.common.page.PageData;
import io.renren.common.service.BaseService;
import io.renren.modules.spider.dto.CrawlerTypeDTO;
import io.renren.modules.spider.entity.CrawlerType;

import java.util.Map;

/**
 * @author kee
 * @version 1.0
 * @date 2022/6/8 16:52
 */
public interface CrawlerTypeService extends BaseService<CrawlerType> {
    PageData<CrawlerTypeDTO> page(Map<String, Object> params);

    CrawlerTypeDTO get(Long id);

    Boolean save(CrawlerTypeDTO dto);

    Boolean update(CrawlerTypeDTO dto);

    void delete(Long[] ids);
}
