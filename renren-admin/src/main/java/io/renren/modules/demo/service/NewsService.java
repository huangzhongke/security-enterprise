/**
 * Copyright (c) 2018 人人开源 All rights reserved.
 *
 * https://www.renren.io
 *
 * 版权所有，侵权必究！
 */

package io.renren.modules.demo.service;

import io.renren.common.page.PageData;
import io.renren.common.service.BaseService;
import io.renren.modules.demo.dto.NewsDTO;
import io.renren.modules.demo.entity.NewsEntity;

import java.util.Map;

/**
 * 新闻
 *
 * @author Mark sunlightcs@gmail.com
 */
public interface NewsService extends BaseService<NewsEntity> {

    PageData<NewsDTO> page(Map<String, Object> params);

    NewsDTO get(Long id);

    void save(NewsDTO dto);

    void update(NewsDTO dto);
}

