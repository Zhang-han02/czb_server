package me.zhengjie.utils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.*;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.JSONPObject;
import com.google.common.collect.Maps;
import org.apache.http.*;
import org.apache.http.client.HttpClient;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.routing.HttpRoute;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.BasicHttpClientConnectionManager;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import org.springframework.scheduling.annotation.Async;

/**
 * httpclient 工具类
 *
 * @author tian.hq 2019.05.31
 */
public class HttpUtil {

    public static String arraySign(Map<String, String> params) {
        boolean encode = false;
        Set<String> keysSet = params.keySet();
        Object[] keys = keysSet.toArray();
        Arrays.sort(keys);
        StringBuffer temp = new StringBuffer();
        boolean first = true;
        for (Object key : keys) {
            if (first) {
                first = false;
            } else {
                temp.append("");
            }
            temp.append(key);
            Object value = params.get(key);
            String valueString = "";
            if (null != value) {
                valueString = value.toString();
                if (key.equals("InvalidTime")) {
                    valueString = valueString.replaceAll("[- :]", "");
                }
            }
            if (encode) {
                try {
                    temp.append(URLEncoder.encode(valueString, "UTF-8"));
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            } else {
                temp.append(valueString);
            }
        }
        return temp.toString();
    }

    public static String stringToMD5(String plainText) {
        byte[] secretBytes = null;
        try {
            secretBytes = MessageDigest.getInstance("md5").digest(plainText.getBytes());
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("没有这个md5算法！");
        }
        String md5code = new BigInteger(1, secretBytes).toString(16);
        for (int i = 0; i < 32 - md5code.length(); i++) {
            md5code = "0" + md5code;
        }
        return md5code;
    }

    public static String requestOnce(final String url, String data) {
        try {
            BasicHttpClientConnectionManager connManager;
            connManager = new BasicHttpClientConnectionManager(RegistryBuilder.<ConnectionSocketFactory>create().register("http", PlainConnectionSocketFactory.getSocketFactory()).register("https", SSLConnectionSocketFactory.getSocketFactory()).build(), null, null, null);
            HttpClient httpClient = HttpClientBuilder.create().setConnectionManager(connManager).build();
            HttpPost httpPost = new HttpPost(url);
            RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(30000).setConnectTimeout(30000).setConnectionRequestTimeout(30000).build();
            httpPost.setConfig(requestConfig);
            StringEntity postEntity = new StringEntity(data, "UTF-8");
            System.out.println("postEntity" + postEntity);
            httpPost.addHeader("Content-Type", "application/x-www-form-urlencoded;Charset=UTF-8");
            httpPost.setEntity(postEntity);

            HttpResponse httpResponse = null;

            httpResponse = httpClient.execute(httpPost);

            HttpEntity httpEntity = httpResponse.getEntity();
            String reusltObj = EntityUtils.toString(httpEntity, "UTF-8");
            System.out.println("reusltObj_______________" + reusltObj);
            return reusltObj;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) throws Exception {
        String userId = "10569405";
        String dbName = "称重宝";
        String AppKey = "C676DA6AEBF44FDFA5EA8E0C9A9A6B33";
        String SercretKey = "D13020F53A414FA6967BFC41C46B3FAC";
        Map<String, String> map = Maps.newHashMap();
        map.put("AppKey", AppKey);
        map.put("UserId", userId);
        SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");
        Date time = new Date();
        map.put("InvalidTime", df.format(time));
        Random rand = new Random();
        Integer randNum = rand.nextInt(10);
        map.put("RandamStr", randNum.toString());
        String sign = HttpUtil.arraySign(map) + SercretKey;
        System.out.println("sign" + sign);
        String SignStr = HttpUtil.stringToMD5(sign); // 签名字符串


        System.out.println("SignStr" + SignStr);
        Map<String, Object> sendMap = Maps.newHashMap();
        sendMap.put("MethodName", "graspcm.cmapi.getcustomerapiurl");
        sendMap.put("AppKey", AppKey);
        sendMap.put("UserId", userId);
        SimpleDateFormat df2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        sendMap.put("InvalidTime", df2.format(time));
        sendMap.put("RandamStr", randNum);
        sendMap.put("SignStr", SignStr);
        String data = JSON.toJSONString(sendMap);
        String url = "http://api.cmgrasp.com/CMGraspApi/GateWay";
        String send_res = HttpUtil.requestOnce(url, data);
        JSONObject obj = JSONObject.parseObject(send_res);
        System.out.println("obj" + obj);
    }

    /**
     * 只要确保你的编码输入是正确的,就可以忽略掉 UnsupportedEncodingException
     */
    public static String asUrlParams(Map<String, String> source) {
        Iterator<String> it = source.keySet().iterator();
        StringBuilder paramStr = new StringBuilder();
        while (it.hasNext()) {
            String key = it.next();
            String value = source.get(key);
            if (StringUtils.isNotBlank(value)) {
                try {
                    // URL 编码
                    value = URLEncoder.encode(value, "utf-8");
                } catch (UnsupportedEncodingException e) {
                    // do nothing
                }
            }

            paramStr.append("&").append(key).append("=").append(value);
        }
        // 去掉第一个&
        return paramStr.substring(1);
    }

}
