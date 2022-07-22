package io.renren.modules.spider.utils;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * @author kee
 * @version 1.0
 * @date 2022/6/1 10:57
 */
public class LoginUtils {
    private static HttpClientContext context = new HttpClientContext();
    private static String ECTIMGCAPTCHA = "";
    private static String Cookie = "";
    private static String host;
    private static Integer port;
    private static Boolean isProxy = false;
    private static String username = "1059308740";
    private static String password = "3smsqgr9";

    private LoginUtils() {

    }

    private static CloseableHttpClient getHttpCline() {
        CredentialsProvider credsProvider = new BasicCredentialsProvider();
        credsProvider.setCredentials(new AuthScope(host, port),
                new UsernamePasswordCredentials(username, password));
        CloseableHttpClient httpClient = HttpClients.custom().setDefaultCredentialsProvider(credsProvider).build();
        return httpClient;
    }

    private static void getCookie()  {
        HttpHost httpHost = new HttpHost(host, port);
        RequestConfig requestConfig = RequestConfig.custom()
                .setConnectionRequestTimeout(10000)
                .setSocketTimeout(10000)
                .setConnectTimeout(10000)
                .setProxy(httpHost)
                .build();
        CloseableHttpClient client = getHttpCline();
        CloseableHttpResponse response = null;
        try {
        String pageUrl = "https://freightsmart.oocl.com/";
        URL url = new URL(pageUrl);
        HttpHost target = new HttpHost(url.getHost(), url.getDefaultPort(), url.getProtocol());
        HttpGet httpGet = new HttpGet(url.getPath());
        httpGet.addHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/103.0.0.0 Safari/537.36");
        httpGet.addHeader("Referer", "https://freightsmart.oocl.com/");
        httpGet.addHeader("Accept-Encoding", "gzip");
        httpGet.setConfig(requestConfig);

            response = client.execute(target,httpGet);
            Header[] headers = response.getHeaders("Set-Cookie");
            for (Header header : headers) {
                if (header.getValue().contains("HMF_CI")) {
                    Cookie = StringUtils.substringBefore(header.getValue(), ";");

                }
            }
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
    }

    private static void getEctImgCaptcha() {
        HttpHost httpHost = new HttpHost(host, port);
        RequestConfig requestConfig = RequestConfig.custom()
                .setConnectionRequestTimeout(10000)
                .setSocketTimeout(10000)
                .setConnectTimeout(10000)
                .setProxy(httpHost)
                .build();
        CloseableHttpClient client = getHttpCline();
        CloseableHttpResponse response = null;
        try {
        String pageUrl = "https://freightsmart.oocl.com/api/common/captcha/image";
        URL url = new URL(pageUrl);
        HttpHost target = new HttpHost(url.getHost(), url.getDefaultPort(), url.getProtocol());
        HttpGet httpGet = new HttpGet(url.getPath());
        httpGet.addHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/103.0.0.0 Safari/537.36");
        httpGet.addHeader("Host", "freightsmart.oocl.com");
        httpGet.addHeader("Referer", "https://freightsmart.oocl.com/");
        httpGet.addHeader("Cookie", Cookie);
        httpGet.addHeader("Accept-Encoding", "gzip");

        httpGet.setConfig(requestConfig);

            response = client.execute(target,httpGet);
            HttpEntity entity = response.getEntity();
            if (entity != null) {
                System.out.println("Content-type:" + entity.getContentType().getValue());
                InputStream inputStream = entity.getContent();
                FileUtils.copyToFile(inputStream, new File("D:\\" + host + ".png"));

            }
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
        HttpHost httpHost = new HttpHost(host, port);
        RequestConfig requestConfig = RequestConfig.custom()
                .setConnectionRequestTimeout(10000)
                .setSocketTimeout(10000)
                .setConnectTimeout(10000)
                .setProxy(httpHost)
                .build();
        getCookie();
        getEctImgCaptcha();
        String captcha = MyUtils.getCaptcha("D:\\" + host + ".png");
        String cookie = Cookie;
        CloseableHttpClient client = getHttpCline();
        CloseableHttpResponse response = null;
        String pageUrl = "https://freightsmart.oocl.com/api/admin/user/login";
        try {
        URL url = new URL(pageUrl);
        HttpHost target = new HttpHost(url.getHost(), url.getDefaultPort(), url.getProtocol());
        HttpPost httpPost = new HttpPost(url.getPath());
        httpPost.setConfig(requestConfig);
        httpPost.addHeader("Cookie", Cookie + "; " + ECTIMGCAPTCHA);
        httpPost.addHeader("ECTIMGCAPTCHA", ECTIMGCAPTCHA.substring(ECTIMGCAPTCHA.indexOf("=") + 1));
        httpPost.addHeader("Referer", "https://freightsmart.oocl.com/");
        // Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/93.0.4577.82 Safari/537.36 Edg/93.0.961.52
        httpPost.addHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/103.0.0.0 Safari/537.36");
        httpPost.addHeader("Content-Type", "application/x-www-form-urlencoded");
        httpPost.addHeader("Accept-Encoding", "gzip");

            List<BasicNameValuePair> params = new ArrayList<>(3);
            params.add(new BasicNameValuePair("username", account.get("username").toString()));
            params.add(new BasicNameValuePair("password", account.get("password").toString()));
            params.add(new BasicNameValuePair("captcha", captcha));
            UrlEncodedFormEntity formEntity = new UrlEncodedFormEntity(params, "UTF-8");

            // String user = "username=" + account.get("username") + "&password=" + account.get("password");
            httpPost.setEntity(formEntity);
            response = client.execute(target,httpPost);

            if (response.getStatusLine().getStatusCode() == 400) {
                HttpEntity entity = response.getEntity();
                String content = EntityUtils.toString(entity);
                Map<String, Object> resultMap = JSONObject.parseObject(content);
                Map<String, Object> messageMap = JSONObject.parseObject(resultMap.get("message").toString());
                if (Integer.parseInt(messageMap.get("code").toString()) == 100015) {
                    System.out.println(messageMap.get("message"));
                    getToken(account);
                }
                return null;
            }
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

    public static Map<String, Object> Login(Map<String, Object> account, String ip) {
        isProxy = true;
        host = ip.substring(0, ip.lastIndexOf(":"));
        port = Integer.parseInt(ip.substring(ip.lastIndexOf(":") + 1));
        return getToken(account);
    }
}
