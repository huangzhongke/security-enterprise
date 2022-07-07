/**
 * Copyright (c) 2018 人人开源 All rights reserved.
 *
 * https://www.renren.io
 *
 * 版权所有，侵权必究！
 */

package io.renren.modules.job.service;

import io.renren.common.page.PageData;
import io.renren.common.service.BaseService;
import io.renren.modules.job.dto.ScheduleJobDTO;
import io.renren.modules.job.entity.ScheduleJobEntity;
import io.renren.modules.spider.one.dto.DataFormDto;
import io.renren.modules.spider.oocl.dto.OOCLDataFormDTO;

import java.util.Map;

/**
 * 定时任务
 *
 * @author Mark sunlightcs@gmail.com
 */
public interface ScheduleJobService extends BaseService<ScheduleJobEntity> {

	PageData<ScheduleJobDTO> page(Map<String, Object> params);

	ScheduleJobDTO get(Long id);

	/**
	 * 保存定时任务
	 */
	void save(DataFormDto dto);

	/**
	 * 更新定时任务
	 */
	void update(DataFormDto dto);

	/**
	 * 保存OOCL定时任务
	 */
	void save(OOCLDataFormDTO dto);

	/**
	 * 更新OOCL定时任务
	 */
	void update(OOCLDataFormDTO dto);
	
	/**
	 * 批量删除定时任务
	 */
	void deleteBatch(Long[] ids);
	
	/**
	 * 批量更新定时任务状态
	 */
	int updateBatch(Long[] ids, int status);
	
	/**
	 * 立即执行
	 */
	void run(Long[] ids);
	
	/**
	 * 暂停运行
	 */
	void pause(Long[] ids);
	
	/**
	 * 恢复运行
	 */
	void resume(Long[] ids);
}
