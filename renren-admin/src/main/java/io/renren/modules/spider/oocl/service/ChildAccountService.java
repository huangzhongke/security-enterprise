package io.renren.modules.spider.oocl.service;

import io.renren.common.service.BaseService;
import io.renren.modules.spider.oocl.entity.ChildAccount;

import java.util.List;
import java.util.Map;

public interface ChildAccountService extends BaseService<ChildAccount> {
    /**
     *
     * @param userList 子账号列表
     * @param params 下单账号搜寻参数
     * @param lineId 下单航线id
     */
    void saveByUserList(List<String> userList, String params,Long lineId);

    /**
     *
     * @param lineId 下单航线id
     * @return  搜索子账号列表
     */
    List<Long> getIdsByLineID(Long lineId);

    List<ChildAccount> getListByLineId(Long lineId);
}
