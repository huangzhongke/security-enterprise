/**
 * Copyright (c) 2018 人人开源 All rights reserved.
 *
 * https://www.renren.io
 *
 * 版权所有，侵权必究！
 */

package io.renren.modules.message.service;

import io.renren.common.service.CrudService;
import io.renren.modules.message.dto.SysSmsDTO;
import io.renren.modules.message.entity.SysSmsEntity;

/**
 * 短信
 *
 * @author Mark sunlightcs@gmail.com
 */
public interface SysSmsService extends CrudService<SysSmsEntity, SysSmsDTO> {

    /**
     * 发送短信
     * @param smsCode   短信编码
     * @param mobile   手机号
     * @param params   短信参数
     */
    void send(String smsCode, String mobile, String params);

    SysSmsEntity getBySmsCode(String smsCode);

}

