/**
 * Copyright (c) 2018 人人开源 All rights reserved.
 *
 * https://www.renren.io
 *
 * 版权所有，侵权必究！
 */

package io.renren.modules.sys.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * 国际化
 * 
 * @author Mark sunlightcs@gmail.com
 */
@Data
@TableName("sys_language")
public class SysLanguageEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 表名
	 */
	private String tableName;
	/**
	 * 表主键
	 */
	private Long tableId;
	/**
	 * 字段名
	 */
	private String fieldName;
	/**
	 * 字段值
	 */
	private String fieldValue;
	/**
	 * 语言
	 */
	private String language;

}