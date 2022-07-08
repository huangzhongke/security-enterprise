package io.renren.modules.spider.oocl.dao;

import io.renren.common.dao.BaseDao;
import io.renren.modules.spider.oocl.entity.ChildAccount;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;


@Mapper
public interface ChildAccountDao extends BaseDao<ChildAccount> {

  List<Long> getIdsByLineID(Long lineId);
}
