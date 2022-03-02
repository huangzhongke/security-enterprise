package io.renren.modules.message.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;


/**
 * 短信日志
 *
 * @author Mark sunlightcs@gmail.com
 */
@Data
@ApiModel(value = "短信日志")
public class SysSmsLogDTO implements Serializable {
    private static final long serialVersionUID = 1L;

	@ApiModelProperty(value = "id")
	private Long id;

	@ApiModelProperty(value = "短信编码")
	private String smsCode;

	@ApiModelProperty(value = "平台类型")
	private Integer platform;

	@ApiModelProperty(value = "手机号")
	private String mobile;

	@ApiModelProperty(value = "参数1")
	private String params1;

	@ApiModelProperty(value = "参数2")
	private String params2;

	@ApiModelProperty(value = "参数3")
	private String params3;

	@ApiModelProperty(value = "参数4")
	private String params4;

	@ApiModelProperty(value = "发送状态  0：失败  1：成功")
	private Integer status;

	@ApiModelProperty(value = "创建者")
	private Long creator;

	@ApiModelProperty(value = "创建时间")
	private Date createDate;


}