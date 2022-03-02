package io.renren.modules.spider.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author kee
 * @version 1.0
 * @date 2022/2/22 17:58
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class FormParams {
    private Long id;
    private Port startPort;
    private Port endPort;
    private Address notifyAddress;
    private Address consigneeAddress;
    private Boolean brokerageAddress;
    private Integer etdDays;
    private Boolean isProxy;
    private Boolean isNeedLineName;
    private Boolean isNeedSupplierName;
    private String equipment;
    private Integer quantity;
    private String vesselName;
    private String voyage;
    private String supplierName;
    private Integer orderSleepTime;
    private String authorization;
    private String references;
    private String scac;
    private Double price;
    private Integer account;
    private Integer booked; //0未下单 1下单

    @Override
    public String toString() {
        return "{" +
                "\"id\":\"" + id  + '\"' +
                ", \"startPort\":" + startPort  +
                ", \"endPort\":" + endPort  +
                ", \"notifyAddress\":" + notifyAddress  +
                ", \"consigneeAddress\":" + consigneeAddress  +
                ", \"brokerageAddress\":" + brokerageAddress +
                ", \"etdDays\":" + etdDays +
                ", \"isProxy\":" + isProxy +
                ", \"isNeedLineName\":" + isNeedLineName +
                ", \"isNeedSupplierName\":" + isNeedSupplierName +
                ", \"equipment\":\"" + equipment + '\"' +
                ", \"quantity\":" + quantity +
                ", \"vesselName\":\"" + vesselName + '\"' +
                ", \"voyage\":\"" + voyage + '\"' +
                ", \"supplierName\":\"" + supplierName + '\"' +
                ", \"orderSleepTime\":" + orderSleepTime +
                ", \"authorization\":\"" + authorization + '\"' +
                ", \"references\":\"" + references + '\"' +
                ", \"scac\":\"" + scac + '\"' +
                ", \"price\":" + price +
                ", \"account\":" + account +
                ", \"booked\":" + booked +
                '}';
    }
}
