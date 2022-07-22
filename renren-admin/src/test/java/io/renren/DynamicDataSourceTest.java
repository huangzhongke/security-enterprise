/**
 * Copyright (c) 2018 人人开源 All rights reserved.
 * <p>
 * https://www.renren.io
 * <p>
 * 版权所有，侵权必究！
 */

package io.renren;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import io.renren.modules.job.service.ScheduleJobService;
import io.renren.modules.spider.menu.entity.Account;
import io.renren.modules.spider.menu.service.AccountService;
import io.renren.modules.spider.utils.MyUtils;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.util.ImageHelper;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.quartz.Scheduler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.context.junit4.SpringRunner;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 多数据源测试
 *
 * @author Mark sunlightcs@gmail.com
 */
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class DynamicDataSourceTest {
    @Autowired
    ScheduleJobService scheduleJobService;
    @Autowired
    AccountService accountService;

    @Autowired
    JavaMailSender javaMailSender;
    private static String pageUrl = "https://freightsmart.oocl.com/"; // 要访问的目标网页
    private static String proxyIp = "140.250.92.243"; // 代理服务器IP
    private static int proxyPort = 16608; // 端口号

    // 用户名密码认证(私密代理/独享代理)



    @Test
    public void test() {
        String s = MyUtils.readJsonFile("account3.json");
        List<Map<String, Object>> accountList = JSONObject.parseObject(s, new TypeReference<List<Map<String, Object>>>() {
        });
        for (Map<String, Object> map : accountList) {
            Account account = new Account();
            account.setUser(map.get("username").toString());
            account.setPassword(map.get("password").toString());
            account.setPayPassword(map.get("pay").toString());
            account.setOrderAccount(false);
            if (map.get("ip") != null) {
                account.setAgentIp(map.get("ip").toString());
            }
            accountService.insert(account);
        }
        System.out.println("插入完毕");

    }


    @Autowired
    private Scheduler scheduler;

    @Test
    public void t3() throws Exception {

      String reg = "[^0-9a-zA-Z\\u4E00-\\u9FA5]";
        Pattern pattern = Pattern.compile(reg);
        Matcher matcher = pattern.matcher("bA53.\n");
        String str = matcher.replaceAll("").trim();
        System.out.println(str);
    }

    @Test
    public void t4() {
        File file = new File("D:\\image4.png");
        try {
            BufferedImage img = ImageIO.read(file);
            img = ImageHelper.convertImageToGrayscale(img);
            // img = ImageHelper.convertImageToBinary(img);
            // img = ImageHelper.getScaledInstance(img,img.getWidth(),img.getHeight() * 5);
            Tesseract instance = new Tesseract();
            instance.setDatapath("./src/main/resources/tessdata");
            instance.setLanguage("eng");
            // instance.setOcrEngineMode(0);
            String result = instance.doOCR(img);
            result = result.replace(" ","");
            System.out.println(result);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void t5() {
        String ip = MyUtils.getIp();
        System.out.println(ip);
        MyUtils.testIpExceed(ip);

    }
}
