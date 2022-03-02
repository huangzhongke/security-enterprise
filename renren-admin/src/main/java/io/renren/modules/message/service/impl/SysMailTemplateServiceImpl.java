/**
 * Copyright (c) 2018 人人开源 All rights reserved.
 *
 * https://www.renren.io
 *
 * 版权所有，侵权必究！
 */

package io.renren.modules.message.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.renren.common.exception.ErrorCode;
import io.renren.common.exception.RenException;
import io.renren.common.service.impl.CrudServiceImpl;
import io.renren.modules.message.dao.SysMailTemplateDao;
import io.renren.modules.message.dto.SysMailTemplateDTO;
import io.renren.modules.message.email.EmailUtils;
import io.renren.modules.message.entity.SysMailTemplateEntity;
import io.renren.modules.message.service.SysMailTemplateService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class SysMailTemplateServiceImpl extends CrudServiceImpl<SysMailTemplateDao, SysMailTemplateEntity, SysMailTemplateDTO> implements SysMailTemplateService {
    @Autowired
    private EmailUtils emailUtils;

    @Override
    public QueryWrapper<SysMailTemplateEntity> getWrapper(Map<String, Object> params) {
        String name = (String)params.get("name");

        QueryWrapper<SysMailTemplateEntity> wrapper = new QueryWrapper<>();
        wrapper.like(StringUtils.isNotBlank(name), "name", name);

        return wrapper;
    }

    @Override
    public boolean sendMail(Long id, String mailTo, String mailCc, String params) throws Exception{
        Map<String, Object> map = null;
        try {
            if(StringUtils.isNotEmpty(params)){
                map = JSON.parseObject(params, Map.class);
            }
        }catch (Exception e){
            throw new RenException(ErrorCode.JSON_FORMAT_ERROR);
        }
        String[] to = new String[]{mailTo};
        String[] cc = StringUtils.isBlank(mailCc) ? null : new String[]{mailCc};

        return emailUtils.sendMail(id, to, cc, map);
    }
}
