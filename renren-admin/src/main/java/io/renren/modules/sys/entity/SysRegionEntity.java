/**
 * Copyright (c) 2018 人人开源 All rights reserved.
 *
 * https://www.renren.io
 *
 * 版权所有，侵权必究！
 */

package io.renren.modules.sys.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 行政区域
 *
 * @author Mark sunlightcs@gmail.com
 */
@Data
@TableName("sys_region")
public class SysRegionEntity implements Serializable {
	private static final long serialVersionUID = 1L;

    /**
     * id
     */
    @TableId(type =  IdType.INPUT)
	private Long id;
    /**
     * 上级ID，一级为0
     */
	private Long pid;
    /**
     * 名称
     */
	private String name;
    /**
     * 层级
     */
	private Integer treeLevel;
    /**
     * 排序
     */
	private Long sort;
    /**
     * 是否叶子节点  0：否   1：是
     */
    private Integer leaf;
    /**
     * 创建者
     */
    @TableField(fill = FieldFill.INSERT)
    private Long  creator;
    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private Date createDate;
    /**
     * 更新者
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Long updater;
    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date updateDate;
    /**
     * 上级名称
     */
    @TableField(exist = false)
    private String parentName;
}