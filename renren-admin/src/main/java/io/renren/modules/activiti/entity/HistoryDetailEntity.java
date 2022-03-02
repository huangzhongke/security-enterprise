/**
 * Copyright (c) 2019 人人开源 All rights reserved.
 *
 * https://www.renren.io
 *
 * 版权所有，侵权必究！
 */

package io.renren.modules.activiti.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

/**
 * @author Jone
 */
@Data
@EqualsAndHashCode(callSuper=false)
public class HistoryDetailEntity {
    private static final long serialVersionUID = 1L;

    private String id;

    private String activityName;

    private String activityType;

    private String processDefinitionId;

    private String processInstanceId;

    private String taskId;

    private String executionId;

    private String assignee;

    private Date startTime;

    private Date endTime;

    private Long durationInSeconds;

    private String comment;

}
