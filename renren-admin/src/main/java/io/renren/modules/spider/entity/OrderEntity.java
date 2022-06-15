package io.renren.modules.spider.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
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
@TableName("spider_order")
@Builder
public class OrderEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	private Long id;
    /**
     * 起始港
     */
	@TableField("startPort")
	private String startPort;
    /**
     * 目的港
     */
	@TableField("endPort")
	private String endPort;
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
	@TableField("order_sleeptime")
	private Integer orderSleepTime;
    /**
     * 下单时间
     */
	@TableField("order_date")
	private Date orderDate;
    /**
     * 是否使用代理
     */
	@TableField("is_proxy")
	private Boolean isProxy;
    /**
     * 是否指定航线代码
     */
	@TableField("is_need_suppliername")
	private Boolean isNeedSupplierName;
    /**
     * 是否指定航名航次
     */
	@TableField("is_need_linename")
	private Boolean isNeedLineName;
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
	@TableField("suppliername")
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