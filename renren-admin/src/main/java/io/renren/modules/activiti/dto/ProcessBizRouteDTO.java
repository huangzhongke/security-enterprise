package io.renren.modules.activiti.dto;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @Author:Jone
 */
@Data
@Api(tags="流程业务配置")
public class ProcessBizRouteDTO {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "id")
    private Long id;

    @ApiModelProperty(value = "流程定义ID")
    @NotBlank(message="{processBizRoute.procDefId.require}")
    private String procDefId;

    @ApiModelProperty(value = "业务路由")
    @NotBlank(message="{processBizRoute.bizRoute.require}")
    private String bizRoute;

    @ApiModelProperty(value = "流程定义KEY")
    @NotBlank(message="{processBizRoute.procDefKey.require}")
    private String procDefKey;

    @ApiModelProperty(value = "版本号")
    @NotNull(message="{processBizRoute.version.require}")
    private Integer version;
}
