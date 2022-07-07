package io.renren.modules.job.task;

import com.alibaba.fastjson.JSONObject;
import io.renren.modules.spider.oocl.dto.OOCLDataFormDTO;
import org.springframework.stereotype.Component;

/**
 * @author kee
 * @version 1.0
 * @date 2022/7/6 16:45
 */
@Component("oocl")
public class OOCL implements ITask {
    @Override
    public void run(String params) {
        OOCLDataFormDTO dataFormDTO = JSONObject.parseObject(params, OOCLDataFormDTO.class);
        System.out.println(dataFormDTO);
    }
}
