package io.renren.modules.spider.utils;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Map;
import java.util.Set;

/**
 * @author 高远</ n>
 * 编写日期   2016-10-5下午5:13:37</n>
 * 邮箱  wgyscsf@163.com</n>
 * 博客  http://blog.csdn.net/wgyscsf</n>
 * TODO</n>
 */
@Slf4j
public class HttpUtils {

    public static final String TAG = "HttpUtils";
    public static HttpClientContext context = new HttpClientContext();
    private static HttpHost proxy = new HttpHost("tps371.kdlapi.com", 15818);

    private HttpUtils() {

    }

    private static CloseableHttpClient getHttpClient() {
        CloseableHttpClient httpClient = null;
        try {
            httpClient = HttpClients.custom().
                    setSSLHostnameVerifier(NoopHostnameVerifier.INSTANCE).
                    setSSLContext(new SSLContextBuilder().loadTrustMaterial(null, new TrustStrategy() {
                        public boolean isTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {
                            return true;
                        }
                    }).build()).build();
        } catch (KeyManagementException e) {
            log.error("KeyManagementException in creating http client instance", e);
        } catch (NoSuchAlgorithmException e) {
            log.error("NoSuchAlgorithmException in creating http client instance", e);
        } catch (KeyStoreException e) {
            log.error("KeyStoreException in creating http client instance", e);
        }
        return httpClient;
    }

    private static RequestConfig getRequestConfig(Boolean isProxy) {
        if (isProxy){
            return RequestConfig.custom().setProxy(proxy)
                    .setConnectTimeout(10000000)//设置连接超时时间,单位毫秒
                    .setSocketTimeout(10000000)//设置读取超时时间,单位毫秒
                    .setConnectionRequestTimeout(100000000)
                    .build();
        }else {
            return RequestConfig.custom()
                    .setConnectTimeout(10000000)//设置连接超时时间,单位毫秒
                    .setSocketTimeout(10000000)//设置读取超时时间,单位毫秒
                    .setConnectionRequestTimeout(100000000)
                    .build();
        }

    }

    /**
     * @param url      请求地址
     * @param paramMap 请求体参数
     * @param params   请求头
     * @return
     */
    public static String sendGet(String url, Map<String, String> paramMap, Map<String, String> params,Boolean isProxy) {
        CloseableHttpResponse response = null;
        String content = null;
        CloseableHttpClient httpClient = getHttpClient();
        try {
            //设置请求地址是：http://yun.itheima.com/search?keys=Java
            //创建URIBuilder
            URIBuilder uriBuilder = new URIBuilder(url);
            // 封装get请求参数
            if (null != paramMap && paramMap.size() > 0) {
//				url = url+"?";
                // 循环遍历，获取迭代器
                for (String keyMap : paramMap.keySet()) {
                    //设置参数
//					url += keyMap + "=" + URLEncoder.encode(paramMap.get(keyMap), "utf-8") + "&";
                    uriBuilder.setParameter(keyMap, paramMap.get(keyMap));

                }
            }

            HttpGet get = new HttpGet(uriBuilder.build());

            get.setConfig(getRequestConfig(isProxy));

            //			添加头
            for (String key : params.keySet()) {
//				System.out.println("key= "+ key + " and value= " + params.get(key));
                get.addHeader(key, params.get(key));
            }
            get.addHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/98.0.4758.82 Safari/537.36");
            get.addHeader("Content-Type", "application/json; charset=UTF-8");
            get.addHeader("Accept", "application/json");


            response = httpClient.execute(get, context);
            HttpEntity entity = response.getEntity();
            content = EntityUtils.toString(entity);
            // System.out.println(TAG + "GET:" + content);
            EntityUtils.consume(entity);
            return content;
        } catch (Exception e) {
            e.printStackTrace();

        } finally {
            if (response != null) {
                try {
                    response.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
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
     * @param url      请求地址
     * @param headers  请求头
     * @param params   请求参数
     * @return
     */
    public static String sendGet(String url, Map<String, String> headers, Map<String, Object> params) {
        CloseableHttpResponse response = null;
        String content = null;
        CloseableHttpClient httpClient = getHttpClient();
        try {
            //设置请求地址是：http://yun.itheima.com/search?keys=Java
            //创建URIBuilder
            URIBuilder uriBuilder = new URIBuilder(url);
            // 封装get请求参数
            if (null != params && params.size() > 0) {
//				url = url+"?";
                // 循环遍历，获取迭代器
                for (String keyMap : params.keySet()) {
                    //设置参数
//					url += keyMap + "=" + URLEncoder.encode(paramMap.get(keyMap), "utf-8") + "&";
                    uriBuilder.setParameter(keyMap, params.get(keyMap).toString());

                }
            }

            HttpGet get = new HttpGet(uriBuilder.build());
            RequestConfig requestConfig = RequestConfig.custom()
                    .setConnectTimeout(10000000)//设置连接超时时间,单位毫秒
                    .setSocketTimeout(10000000)//设置读取超时时间,单位毫秒
                    .setConnectionRequestTimeout(100000000)
                    .build();
            get.setConfig(requestConfig);

            //			添加头
            if (headers != null){
                for (String key : headers.keySet()) {
//				System.out.println("key= "+ key + " and value= " + params.get(key));
                    get.addHeader(key, headers.get(key));
                }
            }

            get.addHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/98.0.4758.82 Safari/537.36");
            get.addHeader("Content-Type", "application/json; charset=UTF-8");
            get.addHeader("Accept", "application/json");
            get.addHeader("Accept-Encoding", "gzip");


            response = httpClient.execute(get, context);
            HttpEntity entity = response.getEntity();
            content = EntityUtils.toString(entity);
            // System.out.println(TAG + "GET:" + content);
            EntityUtils.consume(entity);
            return content;
        } catch (Exception e) {
            e.printStackTrace();

        } finally {
            if (response != null) {
                try {
                    response.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
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
     * post请求
     *
     * @param url
     * @param paramMap
     * @param params
     * @return
     */
    public static String sendPost(String url, Map<String, Object> paramMap, Map<String, String> params,Boolean isProxy) {
        CloseableHttpResponse response = null;
        String content = null;
        CloseableHttpClient httpClient = getHttpClient();
        try {

            HttpPost post = new HttpPost(url);
            post.setConfig(getRequestConfig(isProxy));

//			添加头
            for (String key : params.keySet()) {

                post.addHeader(key, params.get(key));
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
            response = httpClient.execute(post, context);
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
     *
     * @param url 请求地址
     * @param paramMap 请求参数
     * @param params 请求头
     * @param ip 代理Ip
     * @return
     */
    public static String sendPost(String url, Map<String, Object> paramMap, Map<String, String> params,Map<String, Object> ip) {
        CloseableHttpResponse response = null;
        String content = null;
        CloseableHttpClient httpClient = getHttpClient();
        try {

            HttpPost post = new HttpPost(url);
            String host = ip.get("host").toString();
            Integer port = (int)ip.get("port");
            HttpHost httpHost = new HttpHost(host,port);

            RequestConfig requestConfig = RequestConfig.custom().setProxy(httpHost)
                    .setConnectTimeout(10000000)//设置连接超时时间,单位毫秒
                    .setSocketTimeout(10000000)//设置读取超时时间,单位毫秒
                    .setConnectionRequestTimeout(100000000)
                    .build();
            post.setConfig(requestConfig);

//			添加头
            for (String key : params.keySet()) {

                post.addHeader(key, params.get(key));
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
            response = httpClient.execute(post, context);
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
     * @param url
     * @param paramMap
     * @param params
     * @return
     */

    public static String sendPut(String url, Map<String, Object> paramMap, Map<String, String> params,Boolean isProxy) {
        CloseableHttpResponse response = null;
        String content = null;
        CloseableHttpClient httpClient = getHttpClient();
        try {
//			String result = httpHost();
            // 　HttpClient中的post请求包装类
            HttpPut put = new HttpPut(url);
            put.setConfig(getRequestConfig(isProxy));
//			添加头
            for (String key : params.keySet()) {
//				System.out.println("key= "+ key + " and value= " + params.get(key));
                put.addHeader(key, params.get(key));
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
            response = httpClient.execute(put, context);
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

}
