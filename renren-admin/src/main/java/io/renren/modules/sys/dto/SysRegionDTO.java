/**
 * Copyright (c) 2018 人人开源 All rights reserved.
 *
 * https://www.renren.io
 *
 * 版权所有，侵权必究！
 */

package io.renren.modules.sys.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.renren.common.validator.group.DefaultGroup;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;


/**
 * 行政区域
 *
 * @author Mark sunlightcs@gmail.com
 */
@Data
@ApiModel(value = "行政区域")
public class SysRegionDTO implements Serializable {
    private static final long serialVersionUID = 1L;

	@ApiModelProperty(value = "区域标识")
	@NotNull(message="{id.require}", groups = DefaultGroup.class)
	private Long id;

	@ApiModelProperty(value = "上级区域ID")
	@NotNull(message="{region.pid.require}", groups = DefaultGroup.class)
	private Long pid;

	@ApiModelProperty(value = "区域名称")
	@NotBlank(message="{region.name.require}", groups = DefaultGroup.class)
	private String name;

	@ApiModelProperty(value = "排序")
	@Min(value = 0, message = "{sort.number}", groups = DefaultGroup.class)
	private Long sort;

	@ApiModelProperty(value = "上级区域名称")
	private String parentName;

	@ApiModelProperty(value = "是否有子节点")
	private Boolean hasChildren;

	@ApiModelProperty(value = "层级")
	private Integer treeLevel;

	@ApiModelProperty(value = "更新时间")
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	private Date updateDate;
}