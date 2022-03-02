/**
 * Copyright (c) 2019 人人开源 All rights reserved.
 *
 * https://www.renren.io
 *
 * 版权所有，侵权必究！
 */


package io.renren.modules.activiti.entity;

import io.renren.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

/**
 * @author Jone
 */
@Data
@EqualsAndHashCode(callSuper=false)
public class ProcessActivityEntity extends BaseEntity {

    private static final long serialVersionUID = 1L;

    private String processDefinitionId;

    private String processDefinitionName;

    private Integer processDefinitionVersion;

    private String processInstanceId;

    private String businessKey;

    private Date startTime;

    private Date endTime;

    private String startUserId;

    private String assignee;

}
