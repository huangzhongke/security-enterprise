package io.renren.modules.spider.oocl.dto;

import io.renren.modules.spider.oocl.entity.OOCLPort;
import lombok.Data;

import java.util.Date;

@Data
public class MonitorDTO {
    private Long id;
    private String beanName;
    private String params;
    private String cronExpression;
    private String remark;
    private Integer status;
    private OOCLPort startPort;
    private OOCLPort endPort;
    private String account;
    private String cookie;
    private String token;
    private Date startDate;
    private Date endDate;
    private Integer type;
    private Boolean flag;
}
