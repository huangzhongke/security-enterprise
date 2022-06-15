package io.renren.modules.spider.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import io.renren.common.validator.group.AddGroup;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import javax.validation.constraints.Null;
import java.io.Serializable;

/**
 * <p>
 * 航线参数
 * </p>
 *
 * @author kee
 * @since 2022-02-22
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("spider_line")
@ApiModel(value="Line对象", description="航线参数")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Line implements Serializable {

    private static final long serialVersionUID = 1L;

    @Null(message="{id.null}", groups = AddGroup.class)
    private Long id;

    @ApiModelProperty(value = "参数")
    private String params;

    @ApiModelProperty(value = "定时任务ID")
    private Long jobId;


    @ApiModelProperty(value = "身份码")
    @TableField("authorization")
    private String authorization;

}
