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

import java.util.Date;

/**
 * @author Jone
 */
@Data
@Api(tags="流程活动")
public class ProcessActivityDTO {


    @ApiModelProperty(value = "流程定义ID")
    private String processDefinitionId;

    @ApiModelProperty(value = "流程定义名称")
    private String processDefinitionName;

    @ApiModelProperty(value = "流程定义版本")
    private Integer processDefinitionVersion;

    @ApiModelProperty(value = "实例ID")
    private String processInstanceId;

    @ApiModelProperty(value = "业务KEY")
    private String businessKey;

    @ApiModelProperty(value = "发起时间")
    private Date startTime;

    @ApiModelProperty(value = "结束时间")
    private Date endTime;

    @ApiModelProperty(value = "发起人")
    private String startUserId;

    @ApiModelProperty(value = "发起人姓名")
    private String startUserName;

    @ApiModelProperty(value = "受理人")
    private String assignee;

    @ApiModelProperty(value = "受理人姓名")
    private String assigneeName;

}
