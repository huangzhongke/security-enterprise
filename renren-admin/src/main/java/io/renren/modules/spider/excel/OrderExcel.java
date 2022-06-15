package io.renren.modules.spider.excel;

import cn.afterturn.easypoi.excel.annotation.Excel;
import lombok.Data;

import java.util.Date;

/**
 * @author kee
 * @version 1.0
 * @date 2022/6/13 16:12
 */
@Data
public class OrderExcel {
    @Excel(name = "起始港口")
    private String startPort;
    @Excel(name = "终点港口")
    private String endPort;
    @Excel(name = "柜子类型",replace = {"20GP_22G1,40GP_42G1","40HQ_45G1","45RD_L5G1"})
    private String equipment;
    @Excel(name = "数量")
    private Integer quantity;
    @Excel(name = "下单间隔时间")
    private Integer orderSleepTime;
    @Excel(name = "下单时间",format="YYYY-MM-dd HH:mm:ss")
    private Date orderDate;
    @Excel(name = "使用代理",replace = {"否_false", "是_true", })
    private Boolean isProxy;
    @Excel(name = "是否指定供应商名",replace = {"否_false", "是_true", })
    private Boolean isNeedSupplierName;
    @Excel(name = "是否指定航名航次",replace = {"否_false", "是_true", })
    private Boolean isNeedLineName;
    @Excel(name = "航名")
    private String vessel;
    @Excel(name = "航次")
    private String voyage;
    @Excel(name = "供应商名")
    private String supplierName;
    @Excel(name = "下单账号",replace = {"环集账号_0", "泰博账号_1","环集附属账号_2" })
    private Integer account;
    @Excel(name = "etd时间")
    private Integer etd;
    @Excel(name = "小提单号")
    private String reference;
}
