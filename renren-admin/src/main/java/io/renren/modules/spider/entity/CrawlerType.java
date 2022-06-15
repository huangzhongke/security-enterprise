package io.renren.modules.spider.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @author kee
 * @version 1.0
 * @date 2022/6/8 16:33
 */
@Data
@TableName("spider_crawler_type")
public class CrawlerType implements Serializable {

    private static final long serialVersionUID = 1L;
    /**
     * 字典类型ID
     */
    private Long id;
    /**
     * 字典类型
     */
    private String crawlerType;
    /**
     * 字典名称
     */
    private String crawlerName;
    /**
     * 备注
     */
    private String remark;
    /**
     * 排序
     */
    private Integer sort;
    /**
     * 更新者
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Long updater;
    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date updateDate;

    @TableField(fill = FieldFill.INSERT)
    private Long creator;
    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.INSERT)
    private Date createDate;
}
