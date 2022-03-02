package io.renren.modules.activiti.demo.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;


/**
 * 转正申请
 *
 * @author Mark sunlightcs@gmail.com
 */
@Data
@ApiModel(value = "转正申请")
public class CorrectionDTO implements Serializable {
    private static final long serialVersionUID = 1L;

	@ApiModelProperty(value = "id")
	private Long id;

	@ApiModelProperty(value = "申请岗位")
	private String applyPost;

	@ApiModelProperty(value = "入职日期")
	@JsonFormat(pattern="yyyy-MM-dd")
	private Date entryDate;

	@ApiModelProperty(value = "转正日期")
	@JsonFormat(pattern="yyyy-MM-dd")
	private Date correctionDate;

	@ApiModelProperty(value = "工作内容")
	private String workContent;

	@ApiModelProperty(value = "工作成绩")
	private String achievement;

	@ApiModelProperty(value = "创建者")
	private Long creator;

	@ApiModelProperty(value = "创建时间")
	private Date createDate;

	@ApiModelProperty(value = "实例ID")
	private String instanceId;

}