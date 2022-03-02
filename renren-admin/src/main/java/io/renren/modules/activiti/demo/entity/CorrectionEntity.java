package io.renren.modules.activiti.demo.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import io.renren.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

/**
 * 转正申请
 *
 * @author Mark sunlightcs@gmail.com
 */
@Data
@EqualsAndHashCode(callSuper=false)
@TableName("tb_correction")
public class CorrectionEntity extends BaseEntity {
	private static final long serialVersionUID = 1L;

    /**
     * 申请岗位
     */
	private String applyPost;
    /**
     * 入职日期
     */
	private Date entryDate;
    /**
     * 转正日期
     */
	private Date correctionDate;
    /**
     * 工作内容
     */
	private String workContent;
    /**
     * 工作成绩
     */
	private String achievement;

	private String instanceId;
}