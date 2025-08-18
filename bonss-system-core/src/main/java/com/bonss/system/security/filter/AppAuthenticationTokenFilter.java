package com.bonss.system.security.filter;

import com.bonss.common.core.domain.model.LoginUser;
import com.bonss.common.utils.SecurityUtils;
import com.bonss.common.utils.StringUtils;
import com.bonss.common.web.service.AppTokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * token过滤器 验证token有效性
 *
 * @author ct
 */
@Component
public class AppAuthenticationTokenFilter extends OncePerRequestFilter {

    @Autowired
    private AppTokenService appTokenService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        String requestUri = request.getRequestURI();

        // 判断请求路径是否以 /system 开头
        if (requestUri.startsWith("/app")) {
            // 如果请求路径是 /system/**，则进行 token 验证
            LoginUser loginUser = appTokenService.getLoginUser(request);

            if (StringUtils.isNotNull(loginUser) && StringUtils.isNull(SecurityUtils.getAuthentication())) {
                // 创建认证信息
                UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                        loginUser, null, loginUser.getAuthorities()
                );
                authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                // 将认证信息设置到 SecurityContext 中
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            }
        }

        // 继续执行过滤器链
        chain.doFilter(request, response);
    }
}
