package io.renren.modules.spider.utils;


import org.springframework.core.io.ClassPathResource;

import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MyUtils {
    /**
     * @title: dateCompare
     * @description: 比较日期大小,1:保留不变，0：新的比旧的大
     * @param date1 日期1
     * @param date2 日期2
     * @return
     */
    public static Boolean dateCompare(Date date1, Date date2) {
        long dateFirst =date1.getTime();
        long dateLast= date2.getTime();
        return dateFirst < dateLast;
    }

    /**
     * 读取txt文件的内容
     * @param file 想要读取的文件对象
     * @return 返回文件内容
     */
    public static String txt2String(File file){
        StringBuilder result = new StringBuilder();
        try{
            BufferedReader br = new BufferedReader(new FileReader(String.valueOf(file)));//构造一个BufferedReader类来读取文件
            String s = null;
            while((s = br.readLine())!=null){//使用readLine方法，一次读一行
                result.append(System.lineSeparator()).append(s);
            }
            br.close();
        }catch(Exception e){
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
        Calendar ca = Calendar.getInstance();//得到一个Calendar的实例
        ca.setTime(new Date()); //设置时间为当前时间
        ca.add(Calendar.DATE, amount); //几天之后

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
            //json内容转化为Map集合通过遍历集合来进行封装
            while ((context = reader.readLine()) != null) {
                //Context就是读到的json数据
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
     *
     * @param time yyyy-MM-dd HH:mm 只能传入该形式的日期
     * @param pattern
     * @return
     */
    public static String changeTimeFormat(String time,String pattern){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        Date date = null;
        try {
            date = simpleDateFormat.parse(time);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        SimpleDateFormat simpleDateFormat2 = new SimpleDateFormat(pattern);
        return  simpleDateFormat2.format(date);
    }
}
