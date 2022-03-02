/**
 * Copyright (c) 2018 人人开源 All rights reserved.
 *
 * https://www.renren.io
 *
 * 版权所有，侵权必究！
 */

package io.renren.modules.sys.service;

import io.renren.common.service.BaseService;
import io.renren.modules.sys.entity.SysLanguageEntity;


/**
 * 国际化
 *
 * @author Mark sunlightcs@gmail.com
 */
public interface SysLanguageService extends BaseService<SysLanguageEntity> {

    /**
     * 保存或更新
     * @param tableName   表名
     * @param tableId     表主键
     * @param fieldName   字段名
     * @param fieldValue  字段值
     * @param language    语言
     */
    void saveOrUpdate(String tableName, Long tableId, String fieldName, String fieldValue, String language);

    /**
     * 删除国际化
     * @param tableName   表名
     * @param tableId     表主键
     */
    void deleteLanguage(String tableName, Long tableId);
}

