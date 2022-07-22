package io.renren.modules.spider.utils;

import com.alibaba.fastjson.JSONObject;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class OclUtils {
    public static final String TAG = "OOCL_UTILS";
    private static String username = "1059308740";
    private static String password = "3smsqgr9";
    private OclUtils() {

    }

    private static CloseableHttpClient getHttpClient() {

        return HttpClients.custom().build();
    }


    /**
     * @param pageUrl  请求地址
     * @param paramMap 请求参数
     * @param headers  请求头
     * @param
     * @return
     */
    public static String sendPost(String pageUrl, Map<String, Object> paramMap, Map<String, String> headers, String ip) {
        System.setProperty("jdk.http.auth.tunneling.disabledSchemes", "");

        CloseableHttpResponse response = null;
        String content = null;
        RequestConfig requestConfig;
        CloseableHttpClient httpClient = null;
        try {
            URL url = new URL(pageUrl);
            String host = ip.substring(0, ip.lastIndexOf(":"));
            Integer port = Integer.parseInt(ip.substring(ip.lastIndexOf(":") + 1));
            HttpHost target = new HttpHost((url.getHost()), url.getDefaultPort(), url.getProtocol());
            HttpHost httpHost = new HttpHost(host, port);
            requestConfig = RequestConfig
                    .custom()
                    .setProxy(httpHost)
                    .setConnectTimeout(10000)// 设置连接超时时间,单位毫秒
                    .setSocketTimeout(10000)// 设置读取超时时间,单位毫秒
                    .setConnectionRequestTimeout(100000)
                    .build();
            HttpPost post = new HttpPost(url.getPath());
            post.setConfig(requestConfig);

            CredentialsProvider credsProvider = new BasicCredentialsProvider();
            credsProvider.setCredentials(new AuthScope(host, port),
                    new UsernamePasswordCredentials(username, password));
            httpClient = HttpClients.custom().setDefaultCredentialsProvider(credsProvider).build();
//			添加头
            for (String key : headers.keySet()) {

                post.addHeader(key, headers.get(key));
            }
            post.addHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/98.0.4758.82 Safari/537.36");
            post.addHeader("Content-Type", "application/json; charset=UTF-8");
            post.addHeader("Accept", "application/json");
            post.addHeader("Accept-Encoding", "gzip");

            // 封装post请求参数
            if (null != paramMap && paramMap.size() > 0) {
                JSONObject jsonObject = new JSONObject();
                // 通过map集成entrySet方法获取entity
                Set<Map.Entry<String, Object>> entrySet = paramMap.entrySet();
                // 循环遍历，获取迭代器
                for (Map.Entry<String, Object> mapEntry : entrySet) {
                    jsonObject.put(mapEntry.getKey(), mapEntry.getValue());
                }

                // 为httpPost设置封装好的请求参数
                try {
                    post.setEntity(new StringEntity(jsonObject.toString()));
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }

            // 执行请求用execute方法，content用来帮我们附带上额外信息
            response = httpClient.execute(target, post);
            // 得到相应实体、包括响应头以及相应内容
            HttpEntity entity = response.getEntity();
            // 得到response的内容
            content = EntityUtils.toString(entity);
            // System.out.println(TAG + "POST:" + content);
            // 　关闭输入流
            EntityUtils.consume(entity);
            return content;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (response != null) {
                try {
                    response.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (httpClient != null) {
                try {
                    httpClient.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return content;
    }


    /**
     * put请求
     *
     * @param pageUrl  请求地址
     * @param paramMap
     * @param headers
     * @return
     */

    public static String sendPut(String pageUrl, Map<String, Object> paramMap, Map<String, String> headers, String ip) {
        CloseableHttpResponse response = null;
        String content = null;
        CloseableHttpClient httpClient =null;
        try {
//			String result = httpHost();
            // 　HttpClient中的post请求包装类
            System.setProperty("jdk.http.auth.tunneling.disabledSchemes", "");
            RequestConfig requestConfig;
            URL url = new URL(pageUrl);
            String host = ip.substring(0, ip.lastIndexOf(":"));
            Integer port = Integer.parseInt(ip.substring(ip.lastIndexOf(":") + 1));
            HttpHost httpHost = new HttpHost(host, port);
            HttpHost target = new HttpHost(url.getPath(), url.getDefaultPort(), url.getProtocol());
            requestConfig = RequestConfig
                    .custom()
                    .setProxy(httpHost)
                    .setConnectTimeout(10000)// 设置连接超时时间,单位毫秒
                    .setSocketTimeout(10000)// 设置读取超时时间,单位毫秒
                    .setConnectionRequestTimeout(100000)
                    .build();


            HttpPut put = new HttpPut(url.getPath());
            put.setConfig(requestConfig);

            CredentialsProvider credsProvider = new BasicCredentialsProvider();
            credsProvider.setCredentials(new AuthScope(host, port),
                    new UsernamePasswordCredentials(username, password));
            httpClient = HttpClients.custom().setDefaultCredentialsProvider(credsProvider).build();
//			添加头
            for (String key : headers.keySet()) {
//				System.out.println("key= "+ key + " and value= " + params.get(key));
                put.addHeader(key, headers.get(key));
            }
            put.addHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/98.0.4758.82 Safari/537.36");
            put.addHeader("Content-Type", "application/json; charset=UTF-8");
            put.addHeader("Accept", "application/json");
            put.addHeader("Accept-Encoding", "gzip");

            // 封装post请求参数
            if (null != paramMap && paramMap.size() > 0) {
                JSONObject jsonObject = new JSONObject();
                // 通过map集成entrySet方法获取entity
                Set<Map.Entry<String, Object>> entrySet = paramMap.entrySet();
                // 循环遍历，获取迭代器
                for (Map.Entry<String, Object> mapEntry : entrySet) {
                    jsonObject.put(mapEntry.getKey(), mapEntry.getValue());
                }

                // 为httpPost设置封装好的请求参数
                try {
                    put.setEntity(new StringEntity(jsonObject.toString()));
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
            // 执行请求用execute方法，content用来帮我们附带上额外信息
            response = httpClient.execute(target, put);
            // 得到相应实体、包括响应头以及相应内容
            HttpEntity entity = response.getEntity();
            // 得到response的内容
            content = EntityUtils.toString(entity);
            // 　关闭输入流
            EntityUtils.consume(entity);
            return content;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (response != null) {
                try {
                    response.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (httpClient != null) {
                try {
                    httpClient.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return content;
    }

    /**
     * @param pageUrl  请求地址
     * @param paramMap 请求参数
     * @param headers  请求头
     * @return
     */
    public static String sendGet(String pageUrl, Map<String, Object> paramMap, Map<String, String> headers, String ip) {
        CloseableHttpResponse response = null;
        String content = null;
        CloseableHttpClient httpClient =null;
        try {
            System.setProperty("jdk.http.auth.tunneling.disabledSchemes", "");
            RequestConfig requestConfig;
            String host = ip.substring(0, ip.lastIndexOf(":"));
            Integer port = Integer.parseInt(ip.substring(ip.lastIndexOf(":") + 1));
            HttpHost httpHost = new HttpHost(host, port);
            requestConfig = RequestConfig
                    .custom()
                    .setProxy(httpHost)
                    .setConnectTimeout(10000)// 设置连接超时时间,单位毫秒
                    .setSocketTimeout(10000)// 设置读取超时时间,单位毫秒
                    .setConnectionRequestTimeout(100000)
                    .build();
            // 创建URIBuilder
            URIBuilder uriBuilder = new URIBuilder(pageUrl);
            // 封装get请求参数
            if (null != paramMap && paramMap.size() > 0) {
                // 循环遍历，获取迭代器
                for (String keyMap : paramMap.keySet()) {
                    // 设置参数
//					url += keyMap + "=" + URLEncoder.encode(paramMap.get(keyMap), "utf-8") + "&";
                    uriBuilder.setParameter(keyMap, paramMap.get(keyMap).toString());

                }
            }

            HttpGet get = new HttpGet(uriBuilder.build());
            URI uri = uriBuilder.build();
            URL url = new URL(uri.getPath());
            HttpHost target = new HttpHost(url.getPath(), url.getDefaultPort(), url.getProtocol());
            get.setConfig(requestConfig);

            CredentialsProvider credsProvider = new BasicCredentialsProvider();
            credsProvider.setCredentials(new AuthScope(host, port),
                    new UsernamePasswordCredentials(username, password));
            httpClient = HttpClients.custom().setDefaultCredentialsProvider(credsProvider).build();

            //			添加头
            for (String key : headers.keySet()) {
//				System.out.println("key= "+ key + " and value= " + params.get(key));
                get.addHeader(key, headers.get(key));
            }
            get.addHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/98.0.4758.82 Safari/537.36");
            get.addHeader("Content-Type", "application/json; charset=UTF-8");
            get.addHeader("Accept", "application/json");
            get.addHeader("Accept-Encoding", "gzip");


            response = httpClient.execute(target, get);
            HttpEntity entity = response.getEntity();
            content = EntityUtils.toString(entity);
            // System.out.println(TAG + "GET:" + content);
            EntityUtils.consume(entity);
            return content;
        } catch (Exception e) {
            e.printStackTrace();

        } finally {
            try {
                if (response != null) {
                    response.close();
                }
                if (httpClient != null) {
                    httpClient.close();
                }
            } catch (Exception e) {
                System.out.println(e);
            }

        }
        return content;
    }

    /**
     *
     * @param pageUrl 请求地址
     * @param headers 请求头
     * @param params 请求参数
     * @param ip 代理ip
     * @return
     * @param <T>
     */
    public static <T> String sendPost(String pageUrl, Map<String, String> headers, List<T> params, String ip) {


        CloseableHttpResponse response = null;
        String content = null;
        CloseableHttpClient httpClient = null;
        try {
            System.setProperty("jdk.http.auth.tunneling.disabledSchemes", "");
            RequestConfig requestConfig;
            URL url = new URL(pageUrl);
            String host = ip.substring(0, ip.lastIndexOf(":"));
            Integer port = Integer.parseInt(ip.substring(ip.lastIndexOf(":") + 1));
            HttpHost httpHost = new HttpHost(host, port);
            HttpHost target = new HttpHost(url.getPath(), url.getDefaultPort(), url.getProtocol());

            requestConfig = RequestConfig
                    .custom()
                    .setProxy(httpHost)
                    .setConnectTimeout(10000)// 设置连接超时时间,单位毫秒
                    .setSocketTimeout(10000)// 设置读取超时时间,单位毫秒
                    .setConnectionRequestTimeout(100000)
                    .build();
            HttpPost post = new HttpPost(url.getPath());
            post.setConfig(requestConfig);
            CredentialsProvider credsProvider = new BasicCredentialsProvider();
            credsProvider.setCredentials(new AuthScope(host, port),
                    new UsernamePasswordCredentials(username, password));
            httpClient = HttpClients.custom().setDefaultCredentialsProvider(credsProvider).build();
//			添加头
            for (String key : headers.keySet()) {

                post.addHeader(key, headers.get(key));
            }
            post.addHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/98.0.4758.82 Safari/537.36");
            post.addHeader("Content-Type", "application/json; charset=UTF-8");
            post.addHeader("Accept", "application/json");
            post.addHeader("Accept-Encoding", "gzip");

            // 封装post请求参数
            if (null != params && params.size() > 0) {

                // 为httpPost设置封装好的请求参数
                try {
                    post.setEntity(new StringEntity(params.toString()));
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }

            // 执行请求用execute方法，content用来帮我们附带上额外信息
            response = httpClient.execute(target,post);
            // 得到相应实体、包括响应头以及相应内容
            HttpEntity entity = response.getEntity();
            // 得到response的内容
            content = EntityUtils.toString(entity);
            // System.out.println(TAG + "POST:" + content);
            // 　关闭输入流
            EntityUtils.consume(entity);
            return content;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (response != null) {
                    response.close();
                }
                if (httpClient != null) {
                    httpClient.close();
                }
            } catch (Exception e) {
                System.out.println(e);
            }
        }
        return content;
    }
}
