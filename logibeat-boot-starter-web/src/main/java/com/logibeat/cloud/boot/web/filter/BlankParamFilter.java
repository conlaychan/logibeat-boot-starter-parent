package com.logibeat.cloud.boot.web.filter;

import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 将空字符串入参转为 null，即使不是空字符串也会 trim 一下<br>
 * 注意：无法过滤 {@link org.springframework.web.bind.annotation.RequestBody} 中的字段
 */
@Component
public class BlankParamFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        filterChain.doFilter(new HttpServletRequestWrapper(request) {

            @Override
            public String getParameter(String name) {
                return trimToNull(super.getParameter(name));
            }

            @Override
            public String[] getParameterValues(String name) {
                String[] values = super.getParameterValues(name);
                if (values != null) {
                    for (int i = 0; i < values.length; i++) {
                        values[i] = trimToNull(values[i]);
                    }
                }
                return values;
            }
        }, response);
    }

    private String trimToNull(String source) {
        return StringUtils.hasText(source) ? source.trim() : null;
    }
}
