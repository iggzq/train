package com.study.train.business.config;

import java.io.FileWriter;
import java.io.IOException;

public class AliPayConfig {

    // 应用ID,您的APPID，收款账号既是您的APPID对应支付宝账号
    public static String app_id = "9021000133632079";
    // 商户私钥，您的PKCS8格式RSA2私钥
    public static String merchant_private_key = "MIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQCkDp9bYXDWOdl4zVBFS7idrDmvVtj1fY9bycpzLmlWQZ5CTkVRYjzE0TaX7BNWdmqgE4/QYYiDzm3Y8eUAF3FeIBrWwFkH1bxcOSEX5QLMBu62JrnWpgeNIbjTTZR4IiqSUSj/zRJVEVwb9rTAEcUBCG+ZyFxY22EdcLZMD2Cp0+M4zy72c1VMc4Wx4Rs4yzYAESgkqC8xx7HNB75r6WIY71WceevlFHz74XEXxoBBxN4zjxLRu2pcRgWKnmwyUasknebAmPxzvlIhxhaeiMrLrJNKrupislO4zED8KvZ5bPXubmu1sIYrUcD+GaAPxLaygphmq+rNlo+/wNwgw0o1AgMBAAECggEABh2fGQCO0gbvNuFifL7SokVqvhZSTq56/bc4nebCRFX8YL47DR+zd7vRLu8nlmQiMUGhbWl2tcEoLPucJ47Nf1kviZy7L5SrFFNd4DPoO4kGSjyIgM+zfYefCfjSUCXSk/NEwGpu5JnEfFdhTBKtgc5vHfAPPzxoXaOswV+btnZGw+9xupZKiVWLCsQzAE+vx0s3s3iZ7s4OvqEzrAtDXRduihrr1UJSVwPfbohRcH5xwpE0Sw7Ag9y4fk1gViV77GZXCSlCow+tHJg+kkQ+QQRwmdErltKmHybNp9KaMvD0PIbWLx5iYIuhBhfMYgoWUCdPatyHPSofbb4kduvr4QKBgQDWXmBgYHlAcGS3J56eM+ty0eQ2yHHQFvIw32H7zd2rHicJFzH05jEAHq86gLPevj3BmblMWc8mihjD9iQ/ycxhaE6W9rBaKm+gDgShOZAhw5WyzaniOkX4VDY78is7+ccX/IKvK1J0Zvr75GqQFkjiu/9VGCK1xsNdYO1Bp4jxFQKBgQDD6vDYn+CEsAofM4h3QTX92EugyVdT8j1DLChg+Z4i2qtqr7BAHfwpwoyxROLMmbGBfaSKaFcNIxuSPruLj1qSY/BvX2pe9nw3MAlk2sdN2XutUTwJY82T4nD/iIkVLWJHctxR1GrjfDpz/Y8Ni+p/PqdIxcdpv6EA35DTE8f8oQKBgF8+Y1euxXCp/zWZNtJXgx7qvpjQijDIYSbxuRklqH1k0jcpxRpoRBEnkPdPQ5uNruabpEaWCIG5DbPTYtusj1AgBfrw8/27qQjV8ZyodK9rtNFSq05GUdw5njcYK7lkCt9PY8jsWKA7OPw9ylLmc8+5c4KdTJ6zBw4wCHoJM8DZAoGAITo4bNT4tChTHyWTUH571LiZYwSizfb4kurrUj8jpc+mYOq5Hra3LLH1QJ9pQ8ARTd/sRxkZIn5Az9XZ4vqGJuvSPGFXXhG50XZRMYy1XquzARsU7pbBqAIwnfYfIbtXvrxpR2uaUUihdUicuXRwafJLVtlMT9ATU12XZ2UkduECgYEAvT8I/LjOyDMuX6iX3mMtSkYuhxvmmN/1LDbEVxFwtYCjrU5pvBdsoXr8u5pyVSmQILAQXI2ihkHv5gyqj37BU4cBw5Z2Ry6ZcoGlUEV3ej6q+37aG0QxdnN0Wo1mRaBzdsXMIpi5QkYILXxoe7Ddd+sxxjFhzo80Kx71nNE9bPI=";
    // 支付宝公钥
    public static String alipay_public_key = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEApA6fW2Fw1jnZeM1QRUu4naw5r1bY9X2PW8nKcy5pVkGeQk5FUWI8xNE2l+wTVnZqoBOP0GGIg85t2PHlABdxXiAa1sBZB9W8XDkhF+UCzAbutia51qYHjSG4002UeCIqklEo/80SVRFcG/a0wBHFAQhvmchcWNthHXC2TA9gqdPjOM8u9nNVTHOFseEbOMs2ABEoJKgvMcexzQe+a+liGO9VnHnr5RR8++FxF8aAQcTeM48S0btqXEYFip5sMlGrJJ3mwJj8c75SIcYWnojKy6yTSq7qYrJTuMxA/Cr2eWz17m5rtbCGK1HA/hmgD8S2soKYZqvqzZaPv8DcIMNKNQIDAQAB";

    // 服务器异步通知页面路径
    //需http://格式的完整路径，不能加?id=123这类自定义参数，必须外网可以正常访问
    public static String notify_url = "";

    // 页面跳转同步通知页面路径
    //需http://格式的完整路径，不能加?id=123这类自定义参数，必须外网可以正常访问
    public static String return_url = "";
    // 签名方式
    public static String sign_type = "RSA2";
    // 字符编码格式
    public static String charset = "utf-8";
    // 支付宝网关,注意这些使用的是沙箱的支付宝网关，与正常网关的区别是多了dev
    public static String gatewayUrl = "https://openapi-sandbox.dl.alipaydev.com/gateway.do";
    // 日记地址
    public static String log_path = "/home/lituizi/";


    /**
     * 写日志，方便测试（看网站需求，也可以改成把记录存入数据库）
     *
     * @param sWord 要写入日志里的文本内容
     */
    public static void logResult(String sWord) {
        FileWriter writer = null;
        try {
            writer = new FileWriter(log_path + "alipay_log_" + System.currentTimeMillis() + ".txt");
            writer.write(sWord);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
