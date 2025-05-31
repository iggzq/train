package com.study.train.business.config;

import feign.RequestInterceptor;
//import org.apache.seata.core.context.RootContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FeignConfig {

    @Bean
    public RequestInterceptor feignInterceptor() {
        return template -> {
//            String xid = RootContext.getXID();
//            if (xid != null) {
//                template.header(RootContext.KEY_XID, xid);
//            }
        };
    }
}
