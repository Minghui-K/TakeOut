package com.ke.takeout.filter;

import com.alibaba.fastjson.JSON;
import com.ke.takeout.common.BaseContext;
import com.ke.takeout.common.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebFilter(filterName = "loginCheckFilter", urlPatterns = "/*")
@Slf4j
@Component
public class LoginCheckFilter implements Filter {

    // 提供检查url匹配
    public static final AntPathMatcher PATH_MATCHER = new AntPathMatcher();

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        // 1 获得本次请求URI
        String requestURI = request.getRequestURI();
        // TODO distinguish the front and back
        String[] urls = new String[] {
                "/employee/login",
                "/employee/logout",
                "/backend/**",
                "/front/**",
                "/user/sendMsg",
                "/user/login"
        };
        // 2 判断是否需要处理
        if (check(urls, requestURI)) {
            // 3 不需要直接放行
            filterChain.doFilter(request, response);
            return;
        }



        // 4 判读登陆状态，决定放不放
        if (request.getSession().getAttribute("employee") == null) {
            if (request.getSession().getAttribute("user") == null) {
                response.getWriter().write(JSON.toJSONString(R.error("NOTLOGIN")));
            } else {
                BaseContext.setCurrentId((Long) request.getSession().getAttribute("user"));
                filterChain.doFilter(request, response);
            }
        } else {
            //存入当前用户id到线程局部变量
            //***也可以使用  RequestContextHolder
            BaseContext.setCurrentId((Long) request.getSession().getAttribute("employee"));
            filterChain.doFilter(request, response);
        }
    }

    // 检查是否需要放行
    public boolean check(String[] urls, String URL) {
        for (String url : urls) {
            boolean match = PATH_MATCHER.match(url, URL);
            if (match) return true;
        }
        return false;
    }
}
