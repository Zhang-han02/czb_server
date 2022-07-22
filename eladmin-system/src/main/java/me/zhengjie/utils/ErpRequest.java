package me.zhengjie.utils;


import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Maps;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.Random;

public class ErpRequest {

    private static final String APPKEY = "C676DA6AEBF44FDFA5EA8E0C9A9A6B33";
    private static final String DBNAME = "工贸ERPT9";
    private static final String USERID = "10083381";
    private static final String SERCRETKEY = "D13020F53A414FA6967BFC41C46B3FAC";
    private static final String URL = "http://api.cmgrasp.com/CMGraspApi/GateWay";

    public JSONObject ErpRequestApi(String paramkey) {

        SimpleDateFormat df2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        Random rand = new Random();
        Integer randNum = rand.nextInt(10);

        Map<String, String> sendMap = Maps.newHashMap();
        sendMap.put("AppKey", APPKEY);
        sendMap.put("UserId", USERID);
        sendMap.put("InvalidTime", df2.format(new Date()));
        sendMap.put("RandamStr", randNum.toString());

        String sign = HttpUtil.arraySign(sendMap) + SERCRETKEY;
        String SignStr = HttpUtil.stringToMD5(sign); // 签名字符串
        sendMap.put("SignStr", SignStr);

        sendMap.put("MethodName", "graspcm.cmapi.getcustomerapiurl");

        String data = JSON.toJSONString(sendMap);
        String send_res = HttpUtil.requestOnce(URL, data);
        JSONObject obj = JSONObject.parseObject(send_res);
        if (obj.getInteger("RetCode") == 0) {
            String url3 = obj.getJSONObject("RetMsg").getString("ApiServerAddress");
            String ApiParam = obj.getJSONObject("RetMsg").getString("ApiParam");
            System.out.println("ApiParam" + ApiParam);
            Map<String, String> sendMap2 = Maps.newHashMap();
            sendMap2.put("AppKey", APPKEY);
            sendMap2.put("InvalidTime", df2.format(new Date()));
            sendMap2.put("RandamStr", obj.getJSONObject("RetMsg").getString("RandamStr"));

            //生成签名字符串
            String sign2 = HttpUtil.arraySign(sendMap2) + SERCRETKEY;
            String sign2Md5 = HttpUtil.stringToMD5(sign2); // 签名字符串
            sendMap2.put("SignStr", sign2Md5);

            sendMap2.put("MethodName", "graspcm.cmapi.getsignstr");

            String data2 = JSON.toJSONString(sendMap2);
            String send_res2 = HttpUtil.requestOnce(URL, data2);
            JSONObject obj2 = JSONObject.parseObject(send_res2);
            if (obj2.getInteger("RetCode") == 0) {
                String signKey = obj2.getJSONObject("RetMsg").getString("SignKey");
                Map<String, String> sendMap3 = Maps.newHashMap();
                sendMap3.put("apiparam", ApiParam);  // 通过获取程序API地址接口返回
                sendMap3.put("apitype", "query"); // 接口类型 传query
                sendMap3.put("dbname", DBNAME); // 数据库名称
                sendMap3.put("managename", "GraspCMServerApi");
                sendMap3.put("paramjson", "{\"BeginBate\":\"2022-01-01\",\"EndDate\":\"2022-12-31\",\"PageSize\":100,\"PageIndex\":1}"); // 查询条件参数json格式
                sendMap3.put("paramkey", paramkey);  // 接口传入参数ParamsKey


                //生成签名字符串
                String sign3 = HttpUtil.arraySign(sendMap3) + signKey;
                System.out.println("sign3:" + sign3);
                String sign3Md5 = HttpUtil.stringToMD5(sign3); // 签名字符串
                sendMap3.put("sign", sign3Md5);
                String data3 = HttpUtil.asUrlParams(sendMap3);
                System.out.println("______请求参数" + JSONUtil.toJsonStr(sendMap3));
                String send_res3 = HttpUtil.requestOnce(url3, data3);
                JSONObject obj3 = JSONObject.parseObject(send_res3);

                return obj3;
            }
        }
        return null;
    }

}
