package io.renren.modules.spider.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import io.renren.common.page.PageData;
import io.renren.common.service.impl.BaseServiceImpl;
import io.renren.common.utils.ConvertUtils;
import io.renren.modules.spider.dao.CrawlerTypeDao;
import io.renren.modules.spider.dto.CrawlerTypeDTO;
import io.renren.modules.spider.entity.CrawlerType;
import io.renren.modules.spider.service.CrawlerTypeService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Map;

/**
 * @author kee
 * @version 1.0
 * @date 2022/6/8 16:52
 */
@Service
public class CrawlerTypeServiceImpl extends BaseServiceImpl<CrawlerTypeDao, CrawlerType> implements CrawlerTypeService {
    @Override
    public PageData<CrawlerTypeDTO> page(Map<String, Object> params) {

        IPage<CrawlerType> page = baseDao.selectPage(
                getPage(params, "sort", true),
                getWrapper(params)
        );

        return getPageData(page, CrawlerTypeDTO.class);
    }

    @Override
    public CrawlerTypeDTO get(Long id) {
        CrawlerType crawlerType = baseDao.selectById(id);
        return ConvertUtils.sourceToTarget(crawlerType,CrawlerTypeDTO.class);
    }

    @Override
    public Boolean save(CrawlerTypeDTO dto) {
        CrawlerType entity = ConvertUtils.sourceToTarget(dto, CrawlerType.class);
        CrawlerType ct = baseDao.getCrawlerTypeByCrawlerName(entity.getCrawlerName());
        if (ct == null){
            baseDao.insert(entity);
            return  true;
        }
        return false;
    }

    @Override
    public Boolean update(CrawlerTypeDTO dto) {
        CrawlerType entity = ConvertUtils.sourceToTarget(dto, CrawlerType.class);
        CrawlerType ct = baseDao.getCrawlerTypeByCrawlerName(entity.getCrawlerName());
        if (ct == null || (ct.getId().toString().equals(entity.getId().toString()))){
            baseDao.updateById(entity);
            return  true;
        }
        return false;
    }

    @Override
    public void delete(Long[] ids) {
        deleteBatchIds(Arrays.asList(ids));
    }

    private QueryWrapper<CrawlerType> getWrapper(Map<String, Object> params){


        String crawlerType = (String) params.get("crawlerType");
        String crawlerName = (String) params.get("crawlerName");

        QueryWrapper<CrawlerType> wrapper = new QueryWrapper<>();

        wrapper.like(StringUtils.isNotBlank(crawlerType), "crawler_type", crawlerType);
        wrapper.like(StringUtils.isNotBlank(crawlerName), "crawler_name", crawlerName);

        return wrapper;
    }
}
