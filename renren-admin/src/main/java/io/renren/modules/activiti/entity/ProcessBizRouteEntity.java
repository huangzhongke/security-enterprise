package io.renren.modules.activiti.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * @Author:Jone
 */
@Data
@TableName("tb_process_biz_route")
public class ProcessBizRouteEntity {

    private static final long serialVersionUID = 1L;

    private Long id;

    private String procDefId;

    private String bizRoute;

    private String procDefKey;

    private Integer version;
}
