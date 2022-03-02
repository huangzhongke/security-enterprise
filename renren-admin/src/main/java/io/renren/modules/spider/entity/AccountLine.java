package io.renren.modules.spider.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 中间表
 * </p>
 *
 * @author kee
 * @since 2022-02-22
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("spider_account_line")
@ApiModel(value="AccountLine对象", description="中间表")
public class AccountLine implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "账号ID")
    private Long accountId;

    @ApiModelProperty(value = "航线ID")
    private Long lineId;


}
