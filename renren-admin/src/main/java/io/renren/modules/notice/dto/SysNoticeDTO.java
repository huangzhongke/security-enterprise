/**
 * Copyright (c) 2019 人人开源 All rights reserved.
 *
 * https://www.renren.io
 *
 * 版权所有，侵权必究！
 */
package io.renren.modules.notice.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
* 通知管理
*
* @author Mark sunlightcs@gmail.com
*/
@Data
@ApiModel(value = "通知管理")
public class SysNoticeDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "id")
    private Long id;
    @ApiModelProperty(value = "通知类型")
    private Integer type;
    @ApiModelProperty(value = "标题")
    private String title;
    @ApiModelProperty(value = "内容")
    private String content;
    @ApiModelProperty(value = "接收者类型  0：全部  1：部门")
    private Integer receiverType;
    @ApiModelProperty(value = "接收者ID，用逗号分开")
    private String receiverTypeIds;
    @ApiModelProperty(value = "接收者ID列表")
    private List<Long> receiverTypeList;
    @ApiModelProperty(value = "发送状态  0：草稿  1：已发布")
    private Integer status;
    @ApiModelProperty(value = "发送者")
    private String senderName;
    @ApiModelProperty(value = "发送时间")
    private Date senderDate;
    @ApiModelProperty(value = "创建者")
    private Long creator;
    @ApiModelProperty(value = "创建时间")
    private Date createDate;
    @ApiModelProperty(value = "接收者")
    private String receiverName;
    @ApiModelProperty(value = "阅读时间")
    private Date readDate;
    @ApiModelProperty(value = "阅读状态  0：未读  1：已读")
    private Integer readStatus;
}