package io.renren.modules.spider.one.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

/**
 * @author kee
 * @version 1.0
 * @date 2022/3/15 16:37
 */

@Builder
@Data
@TableName("spider_references")
public class SpiderReference implements Serializable {
    private static final long serialVersionUID = 1L;

    @TableId("id")
    private Long id;

    private String reference;

    private Integer account; //0 环集 1 泰博 2 附属

    private Boolean used;

    private Long lineId;

    private Boolean success;
}
