package io.renren.modules.spider.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.renren.common.validator.group.AddGroup;
import io.renren.common.validator.group.DefaultGroup;
import io.renren.common.validator.group.UpdateGroup;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;
import java.io.Serializable;
import java.util.Date;

/**
 * @author kee
 * @version 1.0
 * @date 2022/6/9 10:00
 */
@Data
public class CrawlerTypeDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "id")
    @Null(message="{id.null}", groups = AddGroup.class)
    @NotNull(message="{id.require}", groups = UpdateGroup.class)
    private Long id;

    @ApiModelProperty(value = "爬虫类型")
    @NotBlank
    private String crawlerType;

    @ApiModelProperty(value = "爬虫任务名称")
    @NotBlank
    private String crawlerName;

    @ApiModelProperty(value = "备注")
    private String remark;

    @ApiModelProperty(value = "排序")
    @Min(value = 0, message = "{sort.number}", groups = DefaultGroup.class)
    private Integer sort;

    @ApiModelProperty(value = "创建时间")
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Date createDate;

    @ApiModelProperty(value = "更新时间")
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Date updateDate;
}
