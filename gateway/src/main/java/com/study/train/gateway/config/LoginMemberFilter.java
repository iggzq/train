package com.study.train.gateway.config;

import com.study.train.gateway.enums.RedisKeyPreEnum;
import com.study.train.gateway.utils.JWTutil;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.annotation.Order;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
@Order(value = 0)
public class LoginMemberFilter implements GlobalFilter {

    private static final Logger LOG = LoggerFactory.getLogger(LoginMemberFilter.class);

    @Resource
    private RedisTemplate<String, String> redisTemplate;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String path = exchange.getRequest().getURI().getPath();

        if (path.contains("/admin") || path.contains("/hello") || path.contains("/member/member/login") || path.contains("/member/member/send-code") || path.contains("/member/member/register")) {
            LOG.info("不需要登陆验证：『{}』", path);
            return chain.filter(exchange);
        } else {
            LOG.info("需要登陆验证:『{}』", path);
        }

        String token = exchange.getRequest().getHeaders().getFirst("token");

        LOG.info("会员登陆验证开始，token:『{}』", token);
        if (token == null || token.isEmpty()) {
            LOG.info("token为空，请求被拦截");
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }

        boolean validated = JWTutil.validate(token);
        String s = redisTemplate.opsForValue().get(RedisKeyPreEnum.USER_LOGIN.getKey() + token);
        if (s == null || s.isEmpty()) {
            LOG.info("token已过期，拦截请求");
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }
        if (validated) {
            LOG.info("token有效，放行请求");
            return chain.filter(exchange);
        } else {
            LOG.info("token无效，拦截请求");
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }
    }
}
