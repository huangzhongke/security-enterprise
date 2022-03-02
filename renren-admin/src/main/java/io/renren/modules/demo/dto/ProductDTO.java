package io.renren.modules.demo.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;


/**
 * 产品管理
 *
 * @author Mark sunlightcs@gmail.com
 */
@Data
@ApiModel(value = "产品管理")
public class ProductDTO implements Serializable {
    private static final long serialVersionUID = 1L;

	@ApiModelProperty(value = "id")
	private Long id;

	@ApiModelProperty(value = "产品名称")
	private String name;

	@ApiModelProperty(value = "产品介绍")
	private String content;

	@ApiModelProperty(value = "创建时间")
	private Date createDate;

	@ApiModelProperty(value = "产品参数列表")
	private List<ProductParamsDTO> subList;

}