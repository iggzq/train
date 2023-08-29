package com.study.train.common.interceptor;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.study.train.common.context.LoginMemberContext;
import com.study.train.common.resp.MemberLoginResp;
import com.study.train.common.util.JWTutil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;


@Component
public class MemberInterceptor implements HandlerInterceptor {

    private static final Logger LOG = LoggerFactory.getLogger(MemberInterceptor.class);

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String token = request.getHeader("token");

        if (StrUtil.isNotBlank(token)) {
            LOG.info("获取会员登陆:【{}】", token);
            JSONObject jsonObject = JWTutil.getJSONObject(token);
            LOG.info("当前登陆会员:【{}】", token);
            LoginMemberContext.setMember(JSONUtil.toBean(jsonObject, MemberLoginResp.class));
        }
        return true;

    }
}
