/**
 * Copyright (c) 2018 人人开源 All rights reserved.
 *
 * https://www.renren.io
 *
 * 版权所有，侵权必究！
 */

package io.renren.modules.activiti.service;

import com.fasterxml.jackson.databind.JsonNode;
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
import org.activiti.engine.RepositoryService;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.Model;
import org.activiti.engine.repository.ModelQuery;
import org.activiti.engine.repository.ProcessDefinition;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;

/**
 * 模型管理
 *
 * @author Mark sunlightcs@gmail.com
 */
@Service
public class ActModelService {
    @Autowired
    protected RepositoryService repositoryService;
    @Autowired
    private ObjectMapper objectMapper;

    public PageData<Model> page(Map<String, Object> params) {
        String key = (String)params.get("key");
        String name = (String)params.get("name");

        //分页参数
        int curPage = 1;
        int limit = 10;
        if(params.get(Constant.PAGE) != null){
            curPage = Integer.parseInt((String)params.get(Constant.PAGE));
        }
        if(params.get(Constant.LIMIT) != null){
            limit = Integer.parseInt((String)params.get(Constant.LIMIT));
        }

        ModelQuery modelQuery = repositoryService.createModelQuery().latestVersion().orderByLastUpdateTime().desc();

        if(StringUtils.isNotEmpty(key)){
            modelQuery.modelKey(key);
        }
        if(StringUtils.isNotEmpty(name)){
            modelQuery.modelName(name);
        }

        List<Model> list = modelQuery.listPage((curPage - 1) * limit, limit);
        return new PageData<>(list, (int)modelQuery.count());
    }

    public void save(String name, String key, String description) throws UnsupportedEncodingException {
        //新建一个空模型
        Model model = repositoryService.newModel();

        //metaInfo信息
        ObjectNode metaInfo = objectMapper.createObjectNode();
        metaInfo.put(ModelDataJsonConstants.MODEL_NAME, name);
        metaInfo.put(ModelDataJsonConstants.MODEL_DESCRIPTION, description);
        metaInfo.put(ModelDataJsonConstants.MODEL_REVISION, model.getVersion());

        model.setKey(key);
        model.setName(name);
        model.setMetaInfo(metaInfo.toString());

        repositoryService.saveModel(model);

        ObjectNode editorNode = objectMapper.createObjectNode();
        editorNode.put("id", "canvas");
        editorNode.put("resourceId", "canvas");
        ObjectNode stencilset = objectMapper.createObjectNode();
        stencilset.put("namespace", "http://b3mn.org/stencilset/bpmn2.0#");
        editorNode.set("stencilset", stencilset);
        repositoryService.addModelEditorSource(model.getId(), editorNode.toString().getBytes("utf-8"));
    }

    public void deploy(String id) {
        try {
            Model model = repositoryService.getModel(id);
            BpmnJsonConverter jsonConverter = new BpmnJsonConverter();
            JsonNode editorNode = new ObjectMapper().readTree(repositoryService.getModelEditorSource(model.getId()));
            BpmnModel bpmnModel = jsonConverter.convertToBpmnModel(editorNode);
            BpmnXMLConverter xmlConverter = new BpmnXMLConverter();
            byte[] bpmnBytes = xmlConverter.convertToXML(bpmnModel);
            if(bpmnModel.getProcesses().isEmpty()){
                throw new RenException(ErrorCode.ACT_DEPLOY_ERROR);
            }
            String processName = model.getName();
            if (!StringUtils.endsWith(processName, ".bpmn20.xml")){
                processName += ".bpmn20.xml";
            }

            ByteArrayInputStream in = new ByteArrayInputStream(bpmnBytes);
            Deployment deployment = repositoryService.createDeployment().name(model.getName()).addInputStream(processName, in).deploy();

            List<ProcessDefinition> list = repositoryService.createProcessDefinitionQuery().deploymentId(deployment.getId()).list();
            if (list.size() == 0){
                throw new RenException(ErrorCode.ACT_DEPLOY_ERROR);
            }
        } catch (Exception e) {
            throw new RenException(ErrorCode.ACT_MODEL_IMG_ERROR, e);
        }
    }

    /**
     * 导出模型
     */
    public void export(String id, HttpServletResponse response) {
        try {
            Model model = repositoryService.getModel(id);
            BpmnJsonConverter jsonConverter = new BpmnJsonConverter();
            JsonNode editorNode = new ObjectMapper().readTree(repositoryService.getModelEditorSource(model.getId()));
            BpmnModel bpmnModel = jsonConverter.convertToBpmnModel(editorNode);
            BpmnXMLConverter xmlConverter = new BpmnXMLConverter();
            byte[] bpmnBytes = xmlConverter.convertToXML(bpmnModel);

            ByteArrayInputStream in = new ByteArrayInputStream(bpmnBytes);
            IOUtils.copy(in, response.getOutputStream());
            String filename = bpmnModel.getMainProcess().getId() + ".bpmn20.xml";
            response.setHeader("Content-Disposition", "attachment; filename=" + filename);
            response.flushBuffer();
        } catch (Exception e) {
            throw new RenException(ErrorCode.ACT_MODEL_EXPORT_ERROR, id);
        }
    }

    /**
     * 删除模型
     * @param id  模型ID
     */
    public void delete(String id) {
        repositoryService.deleteModel(id);
    }

    public void deployImage(String deploymentId, HttpServletResponse response) {
        List <String> names = repositoryService.getDeploymentResourceNames(deploymentId);
        String imageName = null;
        for(String name: names){
            if(name.indexOf(".png")>=0){
                imageName = name;
                break;
            }
        }
        InputStream in = null;
        InputStream in1 = null;
        try {
            if(StringUtils.isNotEmpty(imageName)){
                in = repositoryService.getResourceAsStream(deploymentId,imageName);
                response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(imageName, "UTF-8"));
                response.setHeader("Content-Type","image/png");
                response.setHeader("Cache-Control", "no-store, no-cache");
                BufferedImage bufferedImage =  ImageIO.read(in);
                ImageIO.write(bufferedImage, "png", response.getOutputStream());
            } else {
                response.getWriter().println("No image Info!");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if(null != in){
                    in.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
