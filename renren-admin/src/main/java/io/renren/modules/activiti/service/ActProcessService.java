/**
 * Copyright (c) 2018 人人开源 All rights reserved.
 *
 * https://www.renren.io
 *
 * 版权所有，侵权必究！
 */

package io.renren.modules.activiti.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.renren.common.constant.Constant;
import io.renren.common.exception.ErrorCode;
import io.renren.common.exception.RenException;
import io.renren.common.page.PageData;
import org.activiti.bpmn.converter.BpmnXMLConverter;
import org.activiti.bpmn.model.BpmnModel;
import org.activiti.editor.constants.ModelDataJsonConstants;
import org.activiti.editor.language.json.converter.BpmnJsonConverter;
import org.activiti.engine.HistoryService;
import org.activiti.engine.ProcessEngineConfiguration;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.Model;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.repository.ProcessDefinitionQuery;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipInputStream;

/**
 * 流程管理
 *
 * @author Mark sunlightcs@gmail.com
 */
@Service
public class ActProcessService {
    @Autowired
    protected RepositoryService repositoryService;

    @Autowired
    protected HistoryService historyService;

    @Autowired
    protected ProcessEngineConfiguration processEngineConfiguration;

    /**
     * 流程列表
     */
    public PageData<Map<String, Object>> page(Map<String, Object> params) {
        String key = (String)params.get("key");
        String processName = (String)params.get("processName");
        boolean isLatestVersion = params.get("isLatestVersion")== null? false : (boolean)params.get("isLatestVersion");

        //分页参数
        int curPage = 1;
        int limit = 10;
        if(params.get(Constant.PAGE) != null){
            curPage = Integer.parseInt((String)params.get(Constant.PAGE));
        }
        if(params.get(Constant.LIMIT) != null){
            limit = Integer.parseInt((String)params.get(Constant.LIMIT));
        }

        ProcessDefinitionQuery processDefinitionQuery = repositoryService.createProcessDefinitionQuery()
            .orderByProcessDefinitionId().desc().orderByProcessDefinitionKey().desc();
        if(isLatestVersion){
            processDefinitionQuery.latestVersion();
        }
        if(StringUtils.isNotEmpty(key)){
            processDefinitionQuery.processDefinitionKeyLike(key);
        }
        if(StringUtils.isNotEmpty(processName)){
            processDefinitionQuery.processDefinitionNameLike(processName);
        }

        List<ProcessDefinition> processDefinitionList = processDefinitionQuery.listPage((curPage - 1) * limit, limit);

        List<Map<String, Object>> objectList = new ArrayList<>();
        for (ProcessDefinition processDefinition : processDefinitionList) {
            objectList.add(processDefinitionConvert(processDefinition));
        }

        return new PageData<>(objectList, (int)processDefinitionQuery.count());
    }


    /**
     * 流程定义信息
     */
    private Map<String, Object> processDefinitionConvert(ProcessDefinition processDefinition) {
        String deploymentId = processDefinition.getDeploymentId();
        Deployment deployment = repositoryService.createDeploymentQuery().deploymentId(deploymentId).singleResult();

        Map<String, Object> map = new HashMap<>(9);
        map.put("suspended", processDefinition.isSuspended());
        map.put("id", processDefinition.getId());
        map.put("deploymentId", processDefinition.getDeploymentId());
        map.put("name", processDefinition.getName());
        map.put("key", processDefinition.getKey());
        map.put("version", processDefinition.getVersion());
        map.put("resourceName", processDefinition.getResourceName());
        map.put("diagramResourceName", processDefinition.getDiagramResourceName());
        map.put("deploymentTime", deployment.getDeploymentTime());

        return map;
    }

    /**
     * 部署
     * @param file 文件
     */
    public void deploy(MultipartFile file) throws IOException {
        String fileName = file.getOriginalFilename();
        String extension = FilenameUtils.getExtension(fileName);
        if("zip".equalsIgnoreCase(extension) || "bar".equalsIgnoreCase(extension)) {
            ZipInputStream zip = new ZipInputStream(file.getInputStream());
            repositoryService.createDeployment().addZipInputStream(zip).deploy();
        }else if(fileName.indexOf("bpmn20.xml") != -1){
            repositoryService.createDeployment().addInputStream(fileName, file.getInputStream()).deploy();
        }else if("bpmn".equalsIgnoreCase(extension)){
            repositoryService.createDeployment().addInputStream(fileName, file.getInputStream()).deploy();
        }else{
            throw new RenException(ErrorCode.ACT_DEPLOY_FORMAT_ERROR);
        }
    }

    /**
     * 激活流程
     * @param id 流程ID
     */
    public void active(String id){
        repositoryService.activateProcessDefinitionById(id, true, null);
    }

    /**
     * 挂起流程
     * @param id 流程ID
     */
    public void suspend(String id){
        repositoryService.suspendProcessDefinitionById(id, true, null);
    }

    /**
     * 将部署的流程转换为模型
     * @param id 流程ID
     */
    public Model convertToModel(String id) throws UnsupportedEncodingException, XMLStreamException {
        ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery().processDefinitionId(id).singleResult();
        InputStream bpmnStream = repositoryService.getResourceAsStream(processDefinition.getDeploymentId(),
                processDefinition.getResourceName());
        XMLInputFactory xif = XMLInputFactory.newInstance();
        InputStreamReader in = new InputStreamReader(bpmnStream, "UTF-8");
        XMLStreamReader xtr = xif.createXMLStreamReader(in);
        BpmnModel bpmnModel = new BpmnXMLConverter().convertToBpmnModel(xtr);

        BpmnJsonConverter converter = new BpmnJsonConverter();
        ObjectNode modelNode = converter.convertToJson(bpmnModel);
        org.activiti.engine.repository.Model modelData = repositoryService.newModel();
        modelData.setKey(processDefinition.getKey());
        modelData.setName(processDefinition.getResourceName());
        modelData.setCategory(processDefinition.getCategory());
        modelData.setDeploymentId(processDefinition.getDeploymentId());
        modelData.setVersion(Integer.parseInt(String.valueOf(repositoryService.createModelQuery().modelKey(modelData.getKey()).count()+1)));

        ObjectNode modelObjectNode = new ObjectMapper().createObjectNode();
        modelObjectNode.put(ModelDataJsonConstants.MODEL_NAME, processDefinition.getName());
        modelObjectNode.put(ModelDataJsonConstants.MODEL_REVISION, modelData.getVersion());
        modelObjectNode.put(ModelDataJsonConstants.MODEL_DESCRIPTION, processDefinition.getDescription());
        modelData.setMetaInfo(modelObjectNode.toString());

        repositoryService.saveModel(modelData);

        repositoryService.addModelEditorSource(modelData.getId(), modelNode.toString().getBytes("utf-8"));

        return modelData;
    }

    /**
     * 删除部署
     * @param deploymentId  部署ID
     */
    public void deleteDeployment(String deploymentId){
        repositoryService.deleteDeployment(deploymentId, true);
    }

    /**
     * 获取资源文件
     * @param deploymentId  部署ID
     * @param resourceName 资源名称
     */
    public InputStream getResourceAsStream(String deploymentId, String resourceName) {
        InputStream resourceAsStream = repositoryService.getResourceAsStream(deploymentId, resourceName);

        return resourceAsStream;
    }

}
