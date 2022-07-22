package io.renren.modules.spider.utils;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import io.renren.modules.spider.one.entity.FormParams;
import io.renren.modules.spider.one.entity.Port;
import io.swagger.models.auth.In;
import kdl.Auth;
import kdl.Client;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.util.ImageHelper;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.SimpleMailMessage;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MyUtils {
    /**
     * @param date1 日期1
     * @param date2 日期2
     * @return
     * @title: dateCompare
     * @description: 比较日期大小, 1:保留不变，0：新的比旧的大
     */
    public static Boolean dateCompare(Date date1, Date date2) {
        long dateFirst = date1.getTime();
        long dateLast = date2.getTime();
        return dateFirst < dateLast;
    }

    /**
     * 读取txt文件的内容
     *
     * @param file 想要读取的文件对象
     * @return 返回文件内容
     */
    public static String txt2String(File file) {
        StringBuilder result = new StringBuilder();
        try {
            BufferedReader br = new BufferedReader(new FileReader(String.valueOf(file)));// 构造一个BufferedReader类来读取文件
            String s = null;
            while ((s = br.readLine()) != null) {// 使用readLine方法，一次读一行
                result.append(System.lineSeparator()).append(s);
            }
            br.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result.toString();
    }

    public static String replaceBlank(String str) {
        String dest = "";
        if (str != null) {
            Pattern p = Pattern.compile("\\s*|\t|\r|\n");
            Matcher m = p.matcher(str);
            dest = m.replaceAll("");
        }
        return dest;
    }

    public static String getNowDate(int amount, String format) {

        SimpleDateFormat dateFormat = new SimpleDateFormat(format);
        Calendar ca = Calendar.getInstance();// 得到一个Calendar的实例
        ca.setTime(new Date()); // 设置时间为当前时间
        ca.add(Calendar.DATE, amount); // 几天之后

        return dateFormat.format(ca.getTime());
    }

    /**
     * 读取json文件，返回json串
     */
    public static String readJsonFile(String url) {
        BufferedReader reader = null;
        String json = "";
        String context = null;
        try {
            ClassPathResource resource = new ClassPathResource(url);
            reader = new BufferedReader(new InputStreamReader(resource.getInputStream()));
            // json内容转化为Map集合通过遍历集合来进行封装
            while ((context = reader.readLine()) != null) {
                // Context就是读到的json数据
                json += context;
            }
        } catch (Exception e) {
            System.out.println("接口【DictionaryService getUnitMapping】异常参数:" + e);
        } finally {
            try {
                if (null != reader) {
                    reader.close();
                }
            } catch (IOException e) {
                System.out.println("接口【DictionaryService getUnitMapping】异常参数:" + e);
            }
        }

        return json;
    }

    /**
     * @param time    yyyy-MM-dd HH:mm 只能传入该形式的日期
     * @param pattern
     * @return
     */
    public static String changeTimeFormat(String time, String pattern) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        Date date = null;
        try {
            date = simpleDateFormat.parse(time);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        SimpleDateFormat simpleDateFormat2 = new SimpleDateFormat(pattern);
        return simpleDateFormat2.format(date);
    }

    public static String getIp() {
        // Auth auth = new Auth("935456833870189", "qjv098j7zavbj1t1kdsf9nka7tzxl60u");
        // Client client = new Client(auth);
        // String ip;
        // Map<String, Object> params = new HashMap<String, Object>();
        // params.put("sign_type", "hmacsha1");
        // // params.put("area", "浙江");
        // try {
        //     String[] ips = client.get_dps(1,params);
        //     ip = ips[0];
        // } catch (Exception e) {
        //     throw new RuntimeException(e);
        // }
        // return ip;
        String url = "http://dps.kdlapi.com/api/getdps/?orderid=975830967099047&num=1&signature=u59kunmq26i23hc8pm1210f4wd43erjm&pt=1&sep=1";
        String ip = HttpUtils.sendGet(url, null, null);
        return ip;
    }

    /**
     * @param ip
     * @returns false 未失效 true 失效
     */
    public static Boolean testIpExceed(String ip) {
        String url ="https://dps.kdlapi.com/api/getdpsvalidtime";
        Map<String, Object> params = new HashMap<>();
        params.put("orderid","975830967099047");
        params.put("proxy",ip);
        params.put("signature","u59kunmq26i23hc8pm1210f4wd43erjm");
        String result = HttpUtils.sendGet(url, null, params);
        Map<String, Object> resultMap = JSONObject.parseObject(result);
        Map<String, Object> data = JSONObject.parseObject(resultMap.get("data").toString());
        Integer times = Integer.parseInt(data.get(ip).toString());
        System.out.println(JSONObject.toJSONString(data));
        if (times > 5) {
            return false;
        }else {
            return  true;
        }
    }

    public static String getCaptcha(String path) {
        File file = new File(path);
        Tesseract instance = new Tesseract();
        instance.setDatapath("E:\\backCode\\security-enterprise\\renren-admin\\src\\main\\resources\\tessdata");
        instance.setLanguage("eng");
        BufferedImage img = null;
        try {
            img = ImageIO.read(file);
            img = ImageHelper.convertImageToGrayscale(img);
            String result = instance.doOCR(img).replace(" ", "");
            String reg = "[^0-9a-zA-Z\\u4E00-\\u9FA5]";
            Pattern pattern = Pattern.compile(reg);
            Matcher matcher = pattern.matcher(result);
            String str = matcher.replaceAll("").trim();
            return str;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
