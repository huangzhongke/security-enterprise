/**
 * Copyright (c) 2019 人人开源 All rights reserved.
 *
 * https://www.renren.io
 *
 * 版权所有，侵权必究！
 */
package io.renren.modules.notice.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.renren.modules.notice.dao.SysNoticeUserDao;
import io.renren.modules.notice.entity.SysNoticeUserEntity;
import io.renren.modules.notice.enums.NoticeReadStatusEnum;
import io.renren.modules.notice.service.SysNoticeUserService;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * 我的通知
 *
 * @author Mark sunlightcs@gmail.com
 */
@Service
public class SysNoticeUserServiceImpl extends ServiceImpl<SysNoticeUserDao, SysNoticeUserEntity> implements SysNoticeUserService {

    @Override
    public void insertAllUser(SysNoticeUserEntity entity) {
        baseMapper.insertAllUser(entity);
    }

    @Override
    public void updateReadStatus(Long receiverId, Long noticeId) {
        SysNoticeUserEntity entity = new SysNoticeUserEntity()
                .setReceiverId(receiverId)
                .setNoticeId(noticeId)
                .setReadStatus(NoticeReadStatusEnum.READ.value())
                .setReadDate(new Date());

        //标记为已读
        QueryWrapper<SysNoticeUserEntity> query = new QueryWrapper<>();
        query.eq("receiver_id", receiverId);
        query.eq("notice_id", noticeId);
        baseMapper.update(entity, query);
    }

    @Override
    public int getUnReadNoticeCount(Long receiverId) {
        return baseMapper.getUnReadNoticeCount(receiverId);
    }
}