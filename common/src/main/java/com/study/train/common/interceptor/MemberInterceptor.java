package com.study.train.common.interceptor;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.study.train.common.context.LoginMemberHolder;
import com.study.train.common.resp.MemberLoginResp;
import com.study.train.common.utils.JWTutil;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;


@Component
public class MemberInterceptor implements HandlerInterceptor {

    private static final Logger LOG = LoggerFactory.getLogger(MemberInterceptor.class);
    private static final String TOKEN_HEADER = "token";

    @Resource
    private LoginMemberHolder loginMemberHolder;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String token = request.getHeader(TOKEN_HEADER);
        LOG.info("获取会员登陆:【{}】，请求 URI: {}", token, request.getRequestURI());
        JSONObject jsonObject = JWTutil.getJSONObject(token);
        LOG.info("当前登陆会员:【{}】", token);
        loginMemberHolder.setMember(JSONUtil.toBean(jsonObject, MemberLoginResp.class));
        return true;
    }
}
