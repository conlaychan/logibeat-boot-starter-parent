package com.logibeat.cloud.boot.web.utils;

import com.logibeat.cloud.boot.web.exception.UserNotLoginException;

/**
 * 登录拦截器会把已登录用户的 id 存到这里来
 */
public class UserUtil {
    private static final ThreadLocal<Long> userThreadLocal = new ThreadLocal<>();

    public static void putCurrentUserId(Long userId) {
        userThreadLocal.set(userId);
    }

    /**
     * 如果用户未登录，将抛出 UserNotLoginException，所以一定不会返回 null
     *
     * @return 非 null 的当前登录用户的 id
     */
    public static Long getLoginedUserId() {
        Long id = userThreadLocal.get();
        if (id == null) throw new UserNotLoginException();
        return id;
    }

    /**
     * 如果用户未登录，则返回null
     *
     * @return 当前登录用户的 id，未登录则返回null
     */
    public static Long getCurrentUserId() {
        return userThreadLocal.get();
    }

    public static void removeCurrentUserId() {
        userThreadLocal.remove();
    }
}
