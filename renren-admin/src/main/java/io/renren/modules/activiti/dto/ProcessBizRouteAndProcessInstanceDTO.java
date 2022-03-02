package io.renren.modules.activiti.dto;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @Author:Jone
 */
@Data
@EqualsAndHashCode(callSuper=false)
@Api(tags="流程业务配置和实例信息")
public class ProcessBizRouteAndProcessInstanceDTO extends ProcessBizRouteDTO {

    @ApiModelProperty(value = "实例ID")
    private String processInstanceId;

    @ApiModelProperty(value = "流程定义ID")
    private String processDefinitionId;

    @ApiModelProperty(value = "流程定义名称")
    private String processDefinitionName;

    @ApiModelProperty(value = "流程定义KEY")
    private String processDefinitionKey;

}
