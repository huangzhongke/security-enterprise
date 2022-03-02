package io.renren.modules.activiti.dto;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.util.Map;

/**
 * @Author:Jone
 */
@Data
@Api(tags="流程启动参数")
public class ProcessStartDTO {

    @ApiModelProperty(value = "流程KEY")
    @NotBlank(message="{ProcessStart.processDefinitionKey.require}")
    private String processDefinitionKey;

    @ApiModelProperty(value = "业务KEY")
    @NotBlank(message="{ProcessStart.businessKey.require}")
    private String businessKey;

    @ApiModelProperty(value = "流程参数")
    private Map<String, Object> variables;
}
