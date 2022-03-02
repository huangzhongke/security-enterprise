/**
 * Copyright (c) 2019 人人开源 All rights reserved.
 * <p>
 * https://www.renren.io
 * <p>
 * 版权所有，侵权必究！
 */

package io.renren.modules.sys.enums;

/**
 * 行政区域  级别枚举
 *
 * @author Mark sunlightcs@gmail.com
 */
public enum RegionLevelEnum {
    ONE(1),
    TWO(2),
    THREE(3);

    private int value;

    RegionLevelEnum(int value) {
        this.value = value;
    }

    public int value() {
        return this.value;
    }
}
