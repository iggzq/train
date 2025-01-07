package com.study.train.gateway.utils;


import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DateTime;
import cn.hutool.json.JSONObject;
import cn.hutool.jwt.JWT;
import cn.hutool.jwt.JWTPayload;
import cn.hutool.jwt.JWTUtil;

import java.util.HashMap;
import java.util.Map;

public class JWTutil {

    public static final String KEY = "HELLO,WORLD";

    public static String createToken(Long id, String mobile) {

        DateTime now = DateTime.now();

        DateTime expDate = now.offsetNew(DateField.HOUR, 24);

        Map<String, Object> payload = new HashMap<>();

        payload.put(JWTPayload.EXPIRES_AT, expDate);
        payload.put(JWTPayload.ISSUED_AT, now);
        payload.put(JWTPayload.NOT_BEFORE, now);
        payload.put("id", id);
        payload.put("mobile", mobile);

        return JWTUtil.createToken(payload, KEY.getBytes());
    }

    public static boolean validate(String token){
        JWT jwt = JWTUtil.parseToken(token).setKey(KEY.getBytes());
        return jwt.validate(0);
    }

    public static JSONObject getJSONObject(String token){
        JWT jwt = JWTUtil.parseToken(token).setKey(KEY.getBytes());
        JSONObject payloads = jwt.getPayloads();
        payloads.remove(JWTPayload.NOT_BEFORE);
        payloads.remove(JWTPayload.EXPIRES_AT);
        payloads.remove(JWTPayload.ISSUED_AT);

        return payloads;


    }

}
