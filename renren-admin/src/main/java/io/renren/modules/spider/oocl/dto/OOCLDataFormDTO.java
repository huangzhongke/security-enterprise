package io.renren.modules.spider.oocl.dto;

import io.renren.modules.spider.oocl.entity.OOCLPort;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * @author kee
 * @version 1.0
 * @date 2022/7/6 10:45
 */
@Data
public class OOCLDataFormDTO {

    private Long id;
    private String beanName;
    private String params;
    private String cronExpression;
    private String remark;
    private Integer status;
    private OOCLPort startPort;
    private OOCLPort endPort;
    private String equipment;
    private String vesselName;
    private String voyage;
    private Integer quantity;
    private Integer price;
    private String account;
    private String cookie;
    private String token;
    private Date startDate;
    private Date endDate;
    private List<String> childAccount;
    private Boolean isNeedVesselName;
    private Integer type;
    private Boolean isColdType;
}
