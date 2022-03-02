/**
 * Copyright (c) 2019 人人开源 All rights reserved.
 *
 * https://www.renren.io
 *
 * 版权所有，侵权必究！
 */
package io.renren.modules.notice.service;

import com.baomidou.mybatisplus.extension.service.IService;
import io.renren.modules.notice.entity.SysNoticeUserEntity;
import org.apache.ibatis.annotations.Param;

/**
 * 我的通知
 *
 * @author Mark sunlightcs@gmail.com
 */
public interface SysNoticeUserService extends IService<SysNoticeUserEntity> {

    /**
     * 通知全部用户
     */
    void insertAllUser(SysNoticeUserEntity entity);

    /**
     * 标记我的通知为已读
     * @param receiverId  接收者ID
     * @param noticeId    通知ID
     */
    void updateReadStatus(@Param("receiverId") Long receiverId, @Param("noticeId") Long noticeId);


    /**
     * 未读的通知数
     * @param receiverId  接收者ID
     */
    int getUnReadNoticeCount(Long receiverId);
}