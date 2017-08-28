package com.logibeat.cloud.boot.web.handler;

import com.logibeat.cloud.boot.web.exception.UserNotLoginException;
import com.logibeat.cloud.common.exception.BusinessException;
import com.logibeat.cloud.common.exception.SystemException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class ExceptionResolver {
    private static final Logger log = LoggerFactory.getLogger(ExceptionResolver.class);

    private final MessageSource messageSource;

    @Autowired
    public ExceptionResolver(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    @ExceptionHandler(value = BusinessException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public String bizExceptionHandler(BusinessException ex) {
        log.debug("业务异常", ex);
        return messageSource.getMessage(ex.getMessage(), ex.getArgs(), ex.getMessage(), null);
    }

    @ExceptionHandler(value = UserNotLoginException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ResponseBody
    public String loginExceptionHandler() {
        return "请（重新）登录";
    }

    @ExceptionHandler(value = Throwable.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    public String exHandler(Throwable ex) {
        log.error("系统异常", ex);
        String defaultMsg;
        if (ex instanceof SystemException && ex.getMessage() != null) {
            defaultMsg = ex.getMessage();
        } else {
            defaultMsg = "【系统异常】" + ex.toString();
        }
        return messageSource.getMessage(ex.getMessage(), null, defaultMsg, null);
    }
}
