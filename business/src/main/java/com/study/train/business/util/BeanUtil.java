package com.study.train.business.util;

import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.request.AlipayTradePagePayRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.study.train.business.config.AliPayConfig;

@Configuration
public class BeanUtil {

    @Bean
    public AlipayClient alipayClient(){
        return new DefaultAlipayClient(AliPayConfig.gatewayUrl,
                AliPayConfig.app_id, AliPayConfig.merchant_private_key, "json",
                AliPayConfig.charset, AliPayConfig.alipay_public_key,
                AliPayConfig.sign_type);
    }


    @Bean
    public AlipayTradePagePayRequest getAlipayTradePagePayRequest() {
        return new AlipayTradePagePayRequest();
    }
}
