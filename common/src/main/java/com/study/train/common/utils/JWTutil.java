package com.study.train.common.utils;

import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DateTime;
import cn.hutool.json.JSONObject;
import cn.hutool.jwt.JWT;
import cn.hutool.jwt.JWTPayload;
import cn.hutool.jwt.JWTUtil;

import java.util.HashMap;
import java.util.Map;

public class JWTutil {

    // 密钥
    public static final String KEY = "HELLO,WORLD";

    public static final String PRIVATE_KEY = "privateKey";

    public static String createToken(Long id, String mobile){
        // 签发时间
        DateTime now = DateTime.now();
        // 过期时间
        DateTime expDate = now.offsetNew(DateField.HOUR, 24);
        // 载荷
        Map<String, Object> payload = new HashMap<>();
        payload.put(JWTPayload.EXPIRES_AT, expDate);
        payload.put(JWTPayload.ISSUED_AT, now);
        payload.put(JWTPayload.NOT_BEFORE, now);
        payload.put("id", id);
        payload.put("mobile", mobile);

        //使用KEY创建token
        return JWTUtil.createToken(payload, KEY.getBytes());
        //使用rsa256
//        JWTSigner rs256 = JWTSignerUtil.createSigner("RS256", PRIVATE_KEY.getBytes());
        // 生成token
//        return JWTUtil.createToken(payload, rs256);
    }

    public static boolean validate(String token){
        JWT jwt = JWTUtil.parseToken(token).setKey(KEY.getBytes());
        return jwt.validate(0);
    }

    public static JSONObject getJSONObject(String token){
//        JWTSigner rs256 = JWTSignerUtil.createSigner("RS256", PRIVATE_KEY.getBytes());
//        JWT jwt = JWTUtil.parseToken(token).setSigner(rs256);
        JWT jwt = JWTUtil.parseToken(token).setKey(KEY.getBytes());
        JSONObject payloads = jwt.getPayloads();
        payloads.remove(JWTPayload.NOT_BEFORE);
        payloads.remove(JWTPayload.EXPIRES_AT);
        payloads.remove(JWTPayload.ISSUED_AT);
        return payloads;
    }
}
