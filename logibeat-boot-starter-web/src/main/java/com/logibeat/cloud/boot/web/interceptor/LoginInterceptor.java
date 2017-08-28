package com.logibeat.cloud.boot.web.interceptor;

import com.logibeat.cloud.boot.web.exception.UserNotLoginException;
import com.logibeat.cloud.boot.web.utils.UserUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

@Component
public class LoginInterceptor extends HandlerInterceptorAdapter {
    private static final Logger log = LoggerFactory.getLogger(LoginInterceptor.class);
    private final List<String> whiteList = new ArrayList<>();

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        boolean logined = false; // 用户是否已经登录标志位
        HttpSession session = request.getSession(false);
        if (session != null) {
            Object userId = session.getAttribute("session_userId");
            if (userId != null) {
                UserUtil.putCurrentUserId(Long.valueOf(userId.toString()));
                logined = true;
            }
        }

        // 白名单检查
        if (whiteList.contains(request.getRequestURI())) {
            return true;
        }
        if (!logined) {
            throw new UserNotLoginException();
        }
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        UserUtil.removeCurrentUserId();
    }

    /**
     * 加载白名单配置文件 white.list ，注意：考虑到正则可能导致效率低下，所以暂不支持正则，只允许精确匹配
     */
    @PostConstruct
    public void loadWhiteList() throws IOException {
        InputStreamReader inReader = null;
        try {
            inReader = new InputStreamReader(new ClassPathResource("white.list").getInputStream());
        } catch (IOException e) {
            log.warn("File white.list is not found in class path");
        }
        if (inReader != null) {
            BufferedReader reader = new BufferedReader(inReader);
            while (true) {
                String line = reader.readLine();
                if (line == null) break;
                if (StringUtils.hasText(line)) whiteList.add(line.trim());
            }
            reader.close();
        }
    }
}
