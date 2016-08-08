
package ren.hankai.web.interceptor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import ren.hankai.config.Route;
import ren.hankai.config.WebConfig;
import ren.hankai.persist.UserService;
import ren.hankai.persist.model.User;
import ren.hankai.persist.model.UserRole;

/**
 * 网站前端访问拦截器
 *
 * @author hankai
 * @version 1.0
 * @since Mar 28, 2016 1:39:53 PM
 */
@Component
public class SiteAccessInterceptor implements HandlerInterceptor {

    @Autowired
    private UserService userService;

    @Override
    public boolean preHandle( HttpServletRequest request, HttpServletResponse response,
                    Object handler ) throws Exception {
        String contextPath = request.getContextPath();
        String url = request.getRequestURI().substring( contextPath.length() );
        HttpSession session = request.getSession();
        Object userObj = session.getAttribute( WebConfig.SESSION_KEY_FG_USER );
        User user = null;
        if ( ( userObj != null ) && ( userObj instanceof User ) ) {
            user = (User) userObj;
            if ( user.getRole() == UserRole.MobileUser ) {
                // 更新会话中缓存的用户信息
                user = userService.find( user.getId() );
                session.setAttribute( WebConfig.SESSION_KEY_FG_USER, user );
                if ( url.equalsIgnoreCase( Route.FG_LOGIN ) ) {
                    // 已登录过了，再次访问登录页面，重定向到网站首页
                    response.sendRedirect( Route.FOREGROUND_PREFIX );
                    return false;
                }
                return true;
            }
        }
        if ( url.equalsIgnoreCase( Route.FG_LOGIN ) ) {
            // 为了在用户已登录时再次访问登录页面自动跳转到首页，在拦截器过滤选项中并不过滤登录请求
            // 而是在拦截器业务逻辑中判断是否是已登录，如果是，则跳转
            return true;
        }
        response.sendRedirect( Route.FG_LOGIN );
        return false;
    }

    @Override
    public void postHandle( HttpServletRequest request, HttpServletResponse response,
                    Object handler, ModelAndView modelAndView ) throws Exception {
    }

    @Override
    public void afterCompletion( HttpServletRequest request, HttpServletResponse response,
                    Object handler, Exception ex ) throws Exception {
    }
}
