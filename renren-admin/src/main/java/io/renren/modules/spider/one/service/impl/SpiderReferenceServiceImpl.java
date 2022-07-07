package io.renren.modules.spider.one.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import io.renren.common.service.impl.BaseServiceImpl;
import io.renren.modules.spider.one.dao.SpiderReferenceDao;
import io.renren.modules.spider.menu.entity.Line;
import io.renren.modules.spider.one.entity.SpiderReference;
import io.renren.modules.spider.one.service.SpiderReferenceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * @author kee
 * @version 1.0
 * @date 2022/3/15 16:42
 */
@Service
public class SpiderReferenceServiceImpl extends BaseServiceImpl<SpiderReferenceDao, SpiderReference> implements SpiderReferenceService {

    @Autowired
    private SpiderReferenceDao spiderReferenceDao;
    @Override
    public List<SpiderReference> getListByLineId(Long lineId) {
        return spiderReferenceDao.selectList(new LambdaQueryWrapper<SpiderReference>().eq(SpiderReference::getLineId,lineId));
    }

    @Override
    public void saveReferences(Line line,Long id) {
        Map<String, Object> map = JSONObject.parseObject(line.getParams());
        String references = map.get("references").toString();
        int account = Integer.parseInt(map.get("account").toString());
        String[] rs = references.split(",");
        for (String reference : rs) {
            SpiderReference sr = SpiderReference.builder()
                    .reference(reference)
                    .account(account)
                    .used(false)
                    .lineId(id).build();
            spiderReferenceDao.insert(sr);
        }
    }

    @Override
    public void updateReferencesByLindId(Line line) {
        //先把原来的删除在新增新的。
        spiderReferenceDao.delete(new LambdaQueryWrapper<SpiderReference>().eq(SpiderReference::getLineId,line.getId()));
        Map<String, Object> map = JSONObject.parseObject(line.getParams());
        String references = map.get("references").toString();
        int account = Integer.parseInt(map.get("account").toString());
        String[] rs = references.split(",");
        for (String reference : rs) {
            SpiderReference sr = SpiderReference.builder()
                    .reference(reference)
                    .account(account)
                    .used(false)
                    .lineId(line.getId()).build();
            spiderReferenceDao.insert(sr);
        }
    }

    @Override
    public void deleteByLineId(Long id) {
        spiderReferenceDao.delete(new LambdaQueryWrapper<SpiderReference>().eq(SpiderReference::getLineId,id));
    }

    @Override
    public List<String> getSuccessListByLineId(Long lineId) {
        return spiderReferenceDao.getSuccessListByLineId(lineId);
    }

    @Override
    public List<String> getFailListByLineId(Long lineId) {
        return spiderReferenceDao.getFailListByLineId(lineId);
    }
}
