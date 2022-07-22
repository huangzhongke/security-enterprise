package io.renren.modules.spider.oocl.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.renren.common.service.impl.BaseServiceImpl;
import io.renren.modules.spider.oocl.dao.ChildAccountDao;
import io.renren.modules.spider.oocl.dao.MonitorDataDao;
import io.renren.modules.spider.oocl.entity.ChildAccount;
import io.renren.modules.spider.oocl.entity.MonitorData;
import io.renren.modules.spider.oocl.service.MonitorDataService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MonitorDataServiceImpl extends BaseServiceImpl<MonitorDataDao, MonitorData> implements MonitorDataService {
    @Override
    public List<MonitorData> getListByLineId(Long jobId) {
        LambdaQueryWrapper<MonitorData> lambda = new QueryWrapper<MonitorData>().lambda();
        lambda.eq(MonitorData::getJobId,jobId);
        return baseDao.selectList(lambda);
    }

    @Override
    public void deleteByJobIds(Long[] ids) {
        for (Long id : ids) {
            LambdaQueryWrapper<MonitorData> lambda = new QueryWrapper<MonitorData>().lambda();
            lambda.eq(MonitorData::getJobId,id);
            baseDao.delete(lambda);
        }
    }
}
