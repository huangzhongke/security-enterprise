package io.renren.modules.spider.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import io.renren.common.entity.BaseEntity;
import lombok.*;

import java.util.Date;

/**
 * 
 *
 * @author kee 1059308740@qq.com.com
 * @since 1.0.0 2022-03-02
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
@EqualsAndHashCode(callSuper=false)
@TableName("spider_order")
@Builder
public class OrderEntity extends BaseEntity {
	private static final long serialVersionUID = 1L;

    /**
     * 起始港
     */
	private String from;
    /**
     * 目的港
     */
	private String to;
    /**
     * 柜子
     */
	private String equipment;
    /**
     * 数量
     */
	private Integer quantity;
    /**
     * 下单间隔时间
     */
	private Integer orderSleepTime;
    /**
     * 下单时间
     */
	private Date orderDate;
    /**
     * 是否使用代理
     */
	private Integer isProxy;
    /**
     * 是否指定航线代码
     */
	private Integer isNeedSupplierName;
    /**
     * 是否指定航名航次
     */
	private Integer isNeedLineName;
    /**
     * 航名
     */
	private String vessel;
    /**
     * 航次
     */
	private String voyage;
    /**
     * 航线代码
     */
	private String supplierName;
    /**
     * 账号
     */
	private Integer account;
    /**
     * etd间隔时间
     */
	private Integer etd;
    /**
     * 小提单号
     */
	private String reference;
}