/**
 * Copyright (c) 2018 人人开源 All rights reserved.
 *
 * https://www.renren.io
 *
 * 版权所有，侵权必究！
 */

package io.renren.modules.activiti.config;

import org.activiti.spring.SpringProcessEngineConfiguration;
import org.activiti.spring.boot.ProcessEngineConfigurationConfigurer;
import org.springframework.context.annotation.Configuration;

/**
 * 流程配置信息
 *
 * @author Mark sunlightcs@gmail.com
 */
@Configuration
public class ProcessEngineConfig implements ProcessEngineConfigurationConfigurer {

    @Override
    public void configure(SpringProcessEngineConfiguration processEngineConfiguration) {
        processEngineConfiguration.setActivityFontName("宋体");
        processEngineConfiguration.setLabelFontName("宋体");
        processEngineConfiguration.setAnnotationFontName("宋体");

        processEngineConfiguration.setJobExecutorActivate(false);
        processEngineConfiguration.setAsyncExecutorEnabled(false);

        //自定义流程图样式
        //processEngineConfiguration.setProcessDiagramGenerator(customProcessDiagramGenerator);
    }
}
