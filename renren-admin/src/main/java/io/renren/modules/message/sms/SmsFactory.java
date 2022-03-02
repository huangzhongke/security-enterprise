/**
 * Copyright (c) 2018 人人开源 All rights reserved.
 *
 * https://www.renren.io
 *
 * 版权所有，侵权必究！
 */

package io.renren.modules.message.sms;

import com.alibaba.fastjson.JSON;
import io.renren.common.constant.Constant;
import io.renren.common.utils.SpringContextUtils;
import io.renren.modules.message.entity.SysSmsEntity;
import io.renren.modules.message.service.SysSmsService;

/**
 * 短信Factory
 *
 * @author Mark sunlightcs@gmail.com
 */
public class SmsFactory {
    private static SysSmsService sysSmsService;

    static {
        SmsFactory.sysSmsService = SpringContextUtils.getBean(SysSmsService.class);
    }

    public static AbstractSmsService build(String smsCode){
        //获取短信配置信息
        SysSmsEntity smsEntity = sysSmsService.getBySmsCode(smsCode);
        SmsConfig config = JSON.parseObject(smsEntity.getSmsConfig(), SmsConfig.class);

        if(smsEntity.getPlatform() == Constant.SmsService.ALIYUN.getValue()){
            return new AliyunSmsService(config);
        }else if(smsEntity.getPlatform() == Constant.SmsService.QCLOUD.getValue()){
            return new QcloudSmsService(config);
        }else if(smsEntity.getPlatform() == Constant.SmsService.QINIU.getValue()){
            return new QiniuSmsService(config);
        }

        return null;
    }
}