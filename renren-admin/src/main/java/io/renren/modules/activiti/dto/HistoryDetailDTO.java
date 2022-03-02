/**
 * Copyright (c) 2019 人人开源 All rights reserved.
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
 * 历史细节
 *
 * @author Jone
 */
@Data
@Api(tags="历史细节")
public class HistoryDetailDTO {

    @ApiModelProperty(value = "ID")
    private String id;

    @ApiModelProperty(value = "环节名称")
    private String activityName;

    @ApiModelProperty(value = "环节类型")
    private String activityType;

    @ApiModelProperty(value = "流程定义ID")
    private String processDefinitionId;

    @ApiModelProperty(value = "实例ID")
    private String processInstanceId;

    @ApiModelProperty(value = "任务ID")
    private String taskId;

    @ApiModelProperty(value = "执行ID")
    private String executionId;

    @ApiModelProperty(value = "受理人")
    private String assignee;

    @ApiModelProperty(value = "开始时间")
    private Date startTime;

    @ApiModelProperty(value = "结束时间")
    private Date endTime;

    @ApiModelProperty(value = "时长（秒）")
    private Long durationInSeconds;

    @ApiModelProperty(value = "审批意见")
    private String comment;
}
