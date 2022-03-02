/**
 * Copyright (c) 2018 人人开源 All rights reserved.
 *
 * https://www.renren.io
 *
 * 版权所有，侵权必究！
 */

package io.renren.modules.message.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.renren.common.validator.group.AddGroup;
import io.renren.common.validator.group.DefaultGroup;
import io.renren.common.validator.group.UpdateGroup;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;
import java.io.Serializable;
import java.util.Date;

/**
 * 邮件模板
 *
 * @author Mark sunlightcs@gmail.com
 */
@Data
@ApiModel(value = "邮件模板")
public class SysMailTemplateDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "id")
    @Null(message="{id.null}", groups = AddGroup.class)
    @NotNull(message="{id.require}", groups = UpdateGroup.class)
    private Long id;

    @ApiModelProperty(value = "模板名称")
    @NotBlank(message="{mail.name.require}", groups = DefaultGroup.class)
    private String name;

    @ApiModelProperty(value = "邮件主题")
    @NotBlank(message="{mail.subject.require}", groups = DefaultGroup.class)
    private String subject;

    @ApiModelProperty(value = "邮件正文")
    @NotBlank(message="{mail.content.require}", groups = DefaultGroup.class)
    private String content;

    @ApiModelProperty(value = "创建时间")
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Date createDate;

}