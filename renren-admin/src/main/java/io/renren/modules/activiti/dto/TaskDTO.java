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
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author Jone
 */
@Data
@Api(tags="任务")
public class TaskDTO {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "任务ID")
    private String taskId;

    @ApiModelProperty(value = "任务名称")
    private String taskName;

    @ApiModelProperty(value = "用户ID")
    private String userId;

    @ApiModelProperty(value = "任务参数")
    private Map<String,Object> params;

    @ApiModelProperty(value = "流程定义ID")
    private String processDefinitionId;

    @ApiModelProperty(value = "实例ID")
    private String processInstanceId;

    @ApiModelProperty(value = "角色")
    private String roleIds;

    @ApiModelProperty(value = "受理人")
    private String assignee;

    @ApiModelProperty(value = "受理人姓名")
    private String assigneeName;

    @ApiModelProperty(value = "任务所有人")
    private String owner;

    @ApiModelProperty(value = "审核意见")
    private String comment;

    @ApiModelProperty(value = "活动节点ID")
    private String activityId;

    @ApiModelProperty(value = "角色组")
    private List<String> lstGroupId;

    @ApiModelProperty(value = "候选人")
    private List<String> lstUserIds;

    @ApiModelProperty(value = "处理时间")
    private Date dueDate;

    @ApiModelProperty(value = "创建时间")
    private Date createTime;

    @ApiModelProperty(value = "业务ID")
    private String businessKey;

    @ApiModelProperty(value = "流程定义名称")
    private String processDefinitionName;

    @ApiModelProperty(value = "流程定义KEY")
    private String processDefinitionKey;

    @ApiModelProperty(value = "流程发起时间")
    private Date startTime;

}
