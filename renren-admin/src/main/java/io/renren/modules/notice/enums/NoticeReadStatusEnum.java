/**
 * Copyright (c) 2016-2020 人人开源 All rights reserved.
 * <p>
 * https://www.renren.io
 * <p>
 * 版权所有，侵权必究！
 */

package io.renren.modules.notice.enums;

/**
 * 通知阅读状态枚举
 *
 * @author Mark sunlightcs@gmail.com
 */
public enum NoticeReadStatusEnum {
    /**
     * 未读
     */
    UNREAD(0),
    /**
     * 已读
     */
    READ(1);

    private int value;

    NoticeReadStatusEnum(int value) {
        this.value = value;
    }

    public int value() {
        return this.value;
    }
}
