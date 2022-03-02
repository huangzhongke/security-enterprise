/**
 * Copyright (c) 2019 人人开源 All rights reserved.
 *
 * https://www.renren.io
 *
 * 版权所有，侵权必究！
 */
package io.renren.modules.notice.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import io.renren.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

/**
 * 通知管理
 *
 * @author Mark sunlightcs@gmail.com
 */
@Data
@EqualsAndHashCode(callSuper=false)
@TableName("sys_notice")
public class SysNoticeEntity extends BaseEntity {
	private static final long serialVersionUID = 1L;

	/**
	* 通知类型
	*/
	private Integer type;
	/**
	* 标题
	*/
	private String title;
	/**
	* 内容
	*/
	private String content;
	/**
	* 接收者  0：全部  1：部门
	*/
	private Integer receiverType;
	/**
	* 接收者ID，用逗号分开
	*/
	private String receiverTypeIds;
	/**
	* 发送状态  0：草稿  1：已发布
	*/
	private Integer status;
	/**
	* 发送者
	*/
	private String senderName;
	/**
	 * 发送时间
	 */
	private Date senderDate;
	/**
	 * 接收者
	 */
	@TableField(exist = false)
	private String receiverName;
	/**
	 * 阅读状态  0：未读  1：已读
	 */
	@TableField(exist = false)
	private Integer readStatus;
	/**
	 * 阅读时间
	 */
	@TableField(exist = false)
	private Date readDate;
}