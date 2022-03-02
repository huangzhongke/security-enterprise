/**
 * Copyright (c) 2018 人人开源 All rights reserved.
 *
 * https://www.renren.io
 *
 * 版权所有，侵权必究！
 */

package io.renren.modules.activiti.dto;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * 模型
 *
 * @author Mark sunlightcs@gmail.com
 */
@Data
@Api(tags="模型")
public class ModelDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "模型名称")
    @NotBlank(message="{model.name.require}")
    private String name;

    @ApiModelProperty(value = "模型标识")
    @NotBlank(message="{model.key.require}")
    private String key;

    @ApiModelProperty(value = "模型描述")
    private String description;

}