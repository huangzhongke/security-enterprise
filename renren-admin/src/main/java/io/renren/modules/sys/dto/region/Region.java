/**
 * Copyright (c) 2019 人人开源 All rights reserved.
 * <p>
 * https://www.renren.io
 * <p>
 * 版权所有，侵权必究！
 */

package io.renren.modules.sys.dto.region;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 地区管理
 *
 * @author Mark sunlightcs@gmail.com
 */
@Data
@ApiModel(value = "地区管理")
public class Region implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "地区ID")
    private Long id;

    @JsonIgnore
    private Long pid;

    @ApiModelProperty(value = "名称")
    private String name;
}
