package io.renren.modules.spider.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * <p>
 * 账号
 * </p>
 *
 * @author kee
 * @since 2022-02-22
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("spider_account")
@ApiModel(value="Account对象", description="账号")
public class Account implements Serializable {

    private static final long serialVersionUID = 1L;


    private Long id;

    @ApiModelProperty(value = "账号")
    private String user;

    @ApiModelProperty(value = "密码")
    private String password;

    @ApiModelProperty(value = "代理IP")
    private String agentIp;

    @ApiModelProperty(value = "网站起始IP")
    private String originalIp;

    @ApiModelProperty(value = "分类")
    private Integer type;


}
