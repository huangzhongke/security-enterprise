package io.renren.modules.spider.oocl.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
@Builder
@TableName("spider_child_account")
@Data
public class ChildAccount implements Serializable {
    private static final long serialVersionUID = 1L;
    private Long id;
    private String username;
    private Long lineId;
    private String cookie;
    private String token;
    private String ip;
    private Boolean status;
}
