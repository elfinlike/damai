package com.bonss.system.security.handle;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.bonss.common.core.domain.model.LoginAdmin;
import com.bonss.system.manager.AsyncManager;
import com.bonss.system.manager.factory.AsyncFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import com.alibaba.fastjson2.JSON;
import com.bonss.common.constant.Constants;
import com.bonss.common.core.domain.AjaxResult;
import com.bonss.common.utils.ServletUtils;
import com.bonss.common.utils.StringUtils;
import com.bonss.common.web.service.TokenService;

/**
 * 自定义退出处理类 返回成功
 *
 * @author hzx
 */
@Configuration
public class LogoutSuccessHandlerImpl implements LogoutSuccessHandler
{
    @Autowired
    private TokenService tokenService;

    /**
     * 退出处理
     *
     * @return
     */
    @Override
    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication)
            throws IOException, ServletException
    {
        LoginAdmin loginUser = tokenService.getLoginUser(request);
        if (StringUtils.isNotNull(loginUser))
        {
            String userName = loginUser.getUsername();
            // 删除用户缓存记录
            tokenService.delLoginUser(loginUser.getToken());
            // 记录用户退出日志
            AsyncManager.me().execute(AsyncFactory.recordLogininfor(userName, Constants.LOGOUT, "用户退出登录成功"));
        }
        ServletUtils.renderString(response, JSON.toJSONString(AjaxResult.success("用户退出登录成功")));
    }
}
