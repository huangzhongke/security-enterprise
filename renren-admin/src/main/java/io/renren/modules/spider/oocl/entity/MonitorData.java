package io.renren.modules.spider.oocl.entity;


import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Builder;
import lombok.Data;
@Builder
@TableName("spider_monitor_data")
@Data
public class MonitorData {
    private Long id;
    private Long jobId;
    private String vesselName;
    private String voyage;
    private Integer inventory;
}
