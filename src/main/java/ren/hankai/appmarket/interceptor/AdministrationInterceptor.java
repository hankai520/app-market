/*
 * Copyright © 2016 Jiangsu Sparknet Software Co., Ltd. All rights reserved.
 *
 * http://www.sparksoft.com.cn
 */

package ren.hankai.appmarket.interceptor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import ren.hankai.appmarket.config.Route;
import ren.hankai.appmarket.config.WebConfig;
import ren.hankai.appmarket.persist.model.User;
import ren.hankai.appmarket.persist.model.UserRole;
import ren.hankai.appmarket.service.UserService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * 后台管理身份检查拦截器
 *
 * @author hankai
 * @version 1.0
 * @since Mar 28, 2016 1:39:53 PM
 */
@Component
public class AdministrationInterceptor implements HandlerInterceptor {

  @Autowired
  private UserService userService;

  @Override
  public boolean preHandle(HttpServletRequest request, HttpServletResponse response,
      Object handler) throws Exception {
    final String contextPath = request.getContextPath();
    String url = request.getRequestURI().substring(contextPath.length());
    if (url.startsWith("/")) {
      url = url.substring(1);
    }
    final HttpSession session = request.getSession();
    // 程序完整版本号
    // session.setAttribute( name, value );
    final Object userObj = session.getAttribute(WebConfig.SESSION_KEY_BG_USER);
    User user = null;
    if ((userObj != null) && (userObj instanceof User)) {
      user = (User) userObj;
      if (user.getRole() == UserRole.Operator) {
        // 更新会话中缓存的用户信息
        user = userService.getUserById(user.getId());
        session.setAttribute(WebConfig.SESSION_KEY_BG_USER, user);
        return true;
      }
    } else {
      String fullUrl = url;
      if (!StringUtils.isEmpty(request.getQueryString())) {
        fullUrl += ("?" + request.getQueryString());
      }
      session.setAttribute(WebConfig.SESSION_KEY_LAST_URL, fullUrl);
    }
    response.sendRedirect(contextPath + Route.BG_LOGIN);
    return false;
  }

  /*
   * (non-Javadoc)
   *
   * @see org.springframework.web.servlet.HandlerInterceptor#postHandle(javax.servlet.http.
   * HttpServletRequest, javax.servlet.http.HttpServletResponse, java.lang.Object,
   * org.springframework.web.servlet.ModelAndView)
   */
  @Override
  public void postHandle(HttpServletRequest request, HttpServletResponse response,
      Object handler, ModelAndView modelAndView) throws Exception {}

  /*
   * (non-Javadoc)
   *
   * @see org.springframework.web.servlet.HandlerInterceptor#afterCompletion(javax.servlet.http.
   * HttpServletRequest, javax.servlet.http.HttpServletResponse, java.lang.Object,
   * java.lang.Exception)
   */
  @Override
  public void afterCompletion(HttpServletRequest request, HttpServletResponse response,
      Object handler, Exception ex) throws Exception {}
}
