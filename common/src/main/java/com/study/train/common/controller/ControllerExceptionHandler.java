package com.study.train.common.controller;

import com.study.train.common.exception.BusinessException;
import com.study.train.common.resp.CommonResp;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 统一异常处理、数据预处理等
 */
@ControllerAdvice
public class ControllerExceptionHandler {

    private static final Logger LOG = LoggerFactory.getLogger(ControllerExceptionHandler.class);

    /**
     * 所有异常统一处理
     */
    @ExceptionHandler(value = Exception.class)
    @ResponseBody
    public CommonResp<String> exceptionHandler(Exception e) {
        // LOG.info("seata全局事务ID: {}", RootContext.getXID());
        // // 如果是在一次全局事务里出异常了，就不要包装返回值，将异常抛给调用方，让调用方回滚事务
        // if (StrUtil.isNotBlank(RootContext.getXID())) {
        //     throw e;
        // }
        CommonResp<String> commonResp = new CommonResp<>();
        LOG.error("系统异常：", e);
        commonResp.setSuccess(false);
        commonResp.setMessage("系统出现异常，请联系管理员");
        return commonResp;
    }

    /**
     * 业务异常统一处理
     */
    @ExceptionHandler(value = BusinessException.class)
    @ResponseBody
    public CommonResp<String> exceptionHandler(BusinessException e) {
        CommonResp<String> commonResp = new CommonResp<>();
        LOG.error("业务异常：{}", e.getBusinessExceptionEnum().getDesc());
        commonResp.setSuccess(false);
        commonResp.setMessage(e.getBusinessExceptionEnum().getDesc());
        return commonResp;
    }

    /**
     * 校验异常统一处理
     */
    @ExceptionHandler(value = BindException.class)
    @ResponseBody
    public CommonResp<String> exceptionHandler(BindException e) {
        CommonResp<String> commonResp = new CommonResp<>();
        LOG.error("校验异常：{}", e.getBindingResult().getAllErrors().get(0).getDefaultMessage());
        commonResp.setSuccess(false);
        commonResp.setMessage(e.getBindingResult().getAllErrors().get(0).getDefaultMessage());
        return commonResp;
    }
//
//    /**
//     * 校验异常统一处理
//     * @param e
//     * @return
//     */
//    @ExceptionHandler(value = RuntimeException.class)
//    @ResponseBody
//    public CommonResp exceptionHandler(RuntimeException e) {
//        throw e;
//    }

}