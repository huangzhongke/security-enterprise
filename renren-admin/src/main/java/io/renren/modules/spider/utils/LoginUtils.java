package io.renren.modules.spider.utils;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.Header;
import org.apache.http.HttpHost;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author kee
 * @version 1.0
 * @date 2022/6/1 10:57
 */
public class LoginUtils {
    private static HttpClientContext context = new HttpClientContext();
    //存放四个账号的cookie以及 x-Auth-Token
    public static List<Map<String, Object>> tokenAndCookieMap = new ArrayList<>();
    private static String ECTIMGCAPTCHA = "";
    private static String Cookie = "";

    private static String host;
    private static Integer port;
    private static Map<String, Object> ip;
    private static Boolean isProxy;
    private LoginUtils() {

    }

    private static RequestConfig getRequestConfig(){
        if (isProxy){
            HttpHost httpPost = new HttpHost(host, port);
            return  RequestConfig.custom()
                    .setConnectionRequestTimeout(10000)
                    .setSocketTimeout(10000)
                    .setConnectTimeout(10000)
                    .setProxy(httpPost)
                    .build();
        }else {
            return  RequestConfig.custom()
                    .setConnectionRequestTimeout(10000)
                    .setSocketTimeout(10000)
                    .setConnectTimeout(10000)
                    .build();
        }

    }

    private static CloseableHttpClient getHttpCline() {
        return HttpClients.createDefault();
    }

    private static void getCookie() {
        CloseableHttpClient client = getHttpCline();
        CloseableHttpResponse response = null;
        String url = "https://freightsmart.oocl.com/";
        HttpGet httpGet = new HttpGet(url);
        httpGet.addHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/101.0.4951.67 Safari/537.36");
        httpGet.addHeader("Referer", "https://freightsmart.oocl.com/");
        httpGet.addHeader("Accept-Encoding", "gzip");
        httpGet.setConfig(getRequestConfig());
        try {
            response = client.execute(httpGet, context);
            Header[] headers = response.getHeaders("Set-Cookie");
            for (Header header : headers) {
                if (header.getValue().contains("HMF_CI")) {
                    Cookie = StringUtils.substringBefore(header.getValue(), ";");

                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (client != null) {
                    client.close();
                }
                if (response != null) {
                    response.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static void getEctImgCaptcha() {
        CloseableHttpClient client = getHttpCline();
        CloseableHttpResponse response = null;
        String url = "https://freightsmart.oocl.com/api/common/captcha/image";
        HttpGet httpGet = new HttpGet(url);
        httpGet.addHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/101.0.4951.67 Safari/537.36");
        httpGet.addHeader("Host", "freightsmart.oocl.com");
        httpGet.addHeader("Referer", "https://freightsmart.oocl.com/");
        httpGet.addHeader("Cookie", Cookie);
        httpGet.addHeader("Accept-Encoding", "gzip");

        httpGet.setConfig(getRequestConfig());
        try {
            response = client.execute(httpGet, context);
            Header[] headers = response.getHeaders("Set-Cookie");
            for (Header header : headers) {
                if (header.getValue().contains("ECTIMGCAPTCHA")) {

                    ECTIMGCAPTCHA = StringUtils.substringBefore(header.getValue(), ";");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (client != null) {
                    client.close();
                }
                if (response != null) {
                    response.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    private static Map<String, Object> getToken(Map<String, Object> account) {
        getCookie();
        getEctImgCaptcha();
        String cookie = Cookie;
        CloseableHttpClient client = getHttpCline();
        CloseableHttpResponse response = null;
        String url = "https://freightsmart.oocl.com/api/admin/user/login";
        HttpPost httpPost = new HttpPost(url);
        httpPost.setConfig(getRequestConfig());
        httpPost.addHeader("Cookie", Cookie + "; " + ECTIMGCAPTCHA);
        httpPost.addHeader("ECTIMGCAPTCHA", ECTIMGCAPTCHA.substring(ECTIMGCAPTCHA.indexOf("=") + 1));
        httpPost.addHeader("Referer", "https://freightsmart.oocl.com/");
        httpPost.addHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/101.0.4951.67 Safari/537.36");
        httpPost.addHeader("Content-Type", "application/x-www-form-urlencoded");
        httpPost.addHeader("Accept-Encoding", "gzip");
        try {
            List<BasicNameValuePair> params = new ArrayList<>(2);
            params.add(new BasicNameValuePair("username", account.get("username").toString()));
            params.add(new BasicNameValuePair("password", account.get("password").toString()));
            UrlEncodedFormEntity formEntity = new UrlEncodedFormEntity(params);
            httpPost.setEntity(formEntity);
            response = client.execute(httpPost, context);
            String token = response.getFirstHeader("X-Auth-Token").getValue();
            cookie += "; token=" + token;
            Map<String, Object> temp = new HashMap<>(2);
            temp.put("cookie", cookie);
            temp.put("token", token);
            return temp;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (client != null) {
                    client.close();
                }
                if (response != null) {
                    response.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }
    public static Map<String, Object> Login(Map<String, Object> account,Boolean proxy){
        if(account.get("ip") != null){
            ip = JSONObject.parseObject(account.get("ip").toString());
            host = ip.get("host").toString();
            port = (int) ip.get("port");
        }

        isProxy = proxy;
        return getToken(account);
    }
}
