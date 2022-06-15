package io.renren.modules.spider.dto;

import io.renren.modules.spider.entity.Address;
import io.renren.modules.spider.entity.Port;
import lombok.Data;

/**
 * @author kee
 * @version 1.0
 * @date 2022/6/7 17:54
 */
@Data
public class DataFormDto {
    private Long id;
    private String beanName;
    private String params;
    private String cronExpression;
    private String remark;
    private Integer status;
    private String equipment;
    private Integer account;
    private Port startPort;
    private Port endPort;
    private Address notifyAddress;
    private Address consigneeAddress;
    private Boolean isNeedBrokerageAddress;
    private Boolean isNeedNotifyAddress;
    private Boolean isNeedConsigneeAddress;
    private Boolean isProxy;
    private Boolean isNeedLineName;
    private Boolean isNeedSupplierName;
    private Integer etdDays;
    private Integer quantity;
    private String vesselName;
    private String voyage;
    private String supplierName;
    private Integer orderSleepTime;
    private String authorization;
    private String references;
    private String scac;
    private Integer price;
    private Integer weight;

}
