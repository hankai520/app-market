/*
 * Copyright © 2016 Jiangsu Sparknet Software Co., Ltd. All rights reserved.
 *
 * http://www.sparksoft.com.cn
 */

package ren.hankai.appmarket.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import ren.hankai.appmarket.api.payload.PaginatedList;
import ren.hankai.appmarket.api.payload.UserViewModel;
import ren.hankai.appmarket.config.Route;
import ren.hankai.appmarket.config.WebConfig;
import ren.hankai.appmarket.persist.model.UserBean;
import ren.hankai.appmarket.persist.model.UserRole;
import ren.hankai.appmarket.persist.model.UserStatus;
import ren.hankai.appmarket.persist.util.PageUtil;
import ren.hankai.appmarket.service.GroupService;
import ren.hankai.appmarket.service.UserService;

import java.util.Date;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;

/**
 * 用户信息控制器
 *
 * @author hankai
 * @version 1.0
 * @since Mar 28, 2016 2:22:51 PM
 */
@Controller
public class UserController {

  private static final Logger logger = LoggerFactory.getLogger(UserController.class);
  @Autowired
  private UserService userService;
  @Autowired
  private MessageSource messageSource;
  @Autowired
  private GroupService groupService;

  /**
   * 用cookie记住用户的登录信息
   *
   * @param user 用户信息
   * @param response HTTP响应
   * @param path cookie绑定的URL相对路径
   * @author hankai
   * @since Aug 8, 2016 10:02:16 AM
   */
  private void rememberUserViaCookie(UserViewModel user, HttpServletResponse response,
      String path) {
    Cookie cookie = new Cookie(WebConfig.COOKIE_KEY_LOGIN_ID, user.getLoginId());
    cookie.setPath(path);
    cookie.setMaxAge(60 * 60 * 24 * 7);
    response.addCookie(cookie);
    cookie = new Cookie(WebConfig.COOKIE_KEY_PASSWORD, user.getPassword());
    cookie.setPath(path);
    cookie.setMaxAge(60 * 60 * 24 * 7);
    response.addCookie(cookie);
  }

  private String getUserLastAccessedUrl(HttpSession session) {
    String url = "";
    final Object objUrl = session.getAttribute(WebConfig.SESSION_KEY_LAST_URL);
    if ((objUrl != null) && (objUrl instanceof String)) {
      final String str = (String) objUrl;
      if ((str.length() > 0) && !str.equalsIgnoreCase(Route.BG_LOGIN)) {
        url = str;
      }
      session.removeAttribute(WebConfig.SESSION_KEY_LAST_URL);
    }
    return url;
  }

  @RequestMapping(
      value = Route.BG_LOGIN,
      method = {RequestMethod.POST, RequestMethod.GET})
  public ModelAndView bgLogin(
      @CookieValue(
          value = WebConfig.COOKIE_KEY_LOGIN_ID,
          required = false) String loginId,
      @CookieValue(
          value = WebConfig.COOKIE_KEY_PASSWORD,
          required = false) String password,
      @ModelAttribute("user") @Valid UserViewModel user,
      BindingResult br,
      HttpServletRequest request,
      HttpServletResponse response,
      HttpSession session) {
    final ModelAndView mav = new ModelAndView();
    if (StringUtils.isEmpty(user.getLoginId())) {
      user.setLoginId(loginId);
    }
    if (StringUtils.isEmpty(user.getPassword())) {
      user.setPassword(password);
    }
    final String method = request.getMethod().toUpperCase();
    if ("GET".equals(method) && !user.hasContents()) {
      mav.setViewName("admin/login");
    } else {
      if (!br.hasErrors()) {
        final UserBean localUser = userService.getUserByMobile(user.getLoginId());
        if (localUser == null) {
          br.rejectValue("loginId", "admin.login.account.not.found");
        } else if (!user.getPassword().equalsIgnoreCase(localUser.getPassword())) {
          br.rejectValue("password", "admin.login.password.invalid");
        } else if (localUser.getRole() != UserRole.Operator) {
          br.rejectValue("loginId", "admin.login.account.role.invalid");
        } else {
          session.setAttribute(WebConfig.SESSION_KEY_BG_USER, localUser);
          if ((user.getRemember() != null) && user.getRemember()) {
            rememberUserViaCookie(user, response, Route.BACKGROUND_PREFIX);
          }
        }
      }
      if (br.hasErrors()) {
        mav.addObject("user", user);
        mav.setViewName("admin/login");
      } else {
        final String url = getUserLastAccessedUrl(session);
        if (!StringUtils.isEmpty(url)) {
          mav.setViewName("redirect:/" + url);
        } else {
          mav.setViewName("redirect:" + Route.BACKGROUND_PREFIX);
        }
      }
    }
    return mav;
  }

  @RequestMapping(
      value = Route.FG_LOGIN,
      method = {RequestMethod.POST, RequestMethod.GET})
  public ModelAndView fgLogin(
      @CookieValue(
          value = WebConfig.COOKIE_KEY_LOGIN_ID,
          required = false) String loginId,
      @CookieValue(
          value = WebConfig.COOKIE_KEY_PASSWORD,
          required = false) String password,
      @ModelAttribute("user") @Valid UserViewModel user,
      BindingResult br,
      HttpServletRequest request,
      HttpServletResponse response,
      HttpSession session) {
    final ModelAndView mav = new ModelAndView();
    if (StringUtils.isEmpty(user.getLoginId())) {
      user.setLoginId(loginId);
    }
    if (StringUtils.isEmpty(user.getPassword())) {
      user.setPassword(password);
    }
    final String method = request.getMethod().toUpperCase();
    if ("GET".equals(method) && !user.hasContents()) {
      mav.setViewName("site/login");
    } else {
      if (!br.hasErrors()) {
        final UserBean localUser = userService.getUserByMobile(user.getLoginId());
        if (localUser == null) {
          br.rejectValue("loginId", "fg.login.account.not.found");
        } else if (!user.getPassword().equalsIgnoreCase(localUser.getPassword())) {
          br.rejectValue("password", "fg.login.password.invalid");
        } else if (localUser.getRole() != UserRole.MobileUser) {
          br.rejectValue("loginId", "fg.login.account.role.invalid");
        } else {
          session.setAttribute(WebConfig.SESSION_KEY_FG_USER, localUser);
          if ((user.getRemember() != null) && user.getRemember()) {
            rememberUserViaCookie(user, response, Route.FOREGROUND_PREFIX);
          }
        }
      }
      if (br.hasErrors()) {
        mav.addObject("user", user);
        mav.setViewName("site/login");
      } else {
        mav.setViewName("redirect:/");
      }
    }
    return mav;
  }

  @RequestMapping(Route.BG_LOGOUT)
  public ModelAndView bgLogout(HttpSession session, HttpServletResponse response) {
    final ModelAndView mav = new ModelAndView("redirect:" + Route.BG_LOGIN);
    session.removeAttribute(WebConfig.SESSION_KEY_BG_USER);
    Cookie cookie = new Cookie(WebConfig.COOKIE_KEY_LOGIN_ID, "");
    cookie.setPath(Route.BACKGROUND_PREFIX);
    cookie.setMaxAge(0);
    response.addCookie(cookie);
    cookie = new Cookie(WebConfig.COOKIE_KEY_PASSWORD, "");
    cookie.setPath(Route.BACKGROUND_PREFIX);
    cookie.setMaxAge(0);
    response.addCookie(cookie);
    return mav;
  }

  @RequestMapping(Route.FG_LOGOUT)
  public ModelAndView fgLogout(HttpSession session, HttpServletResponse response) {
    final ModelAndView mav = new ModelAndView("redirect:" + Route.FG_LOGIN);
    session.removeAttribute(WebConfig.SESSION_KEY_FG_USER);
    Cookie cookie = new Cookie(WebConfig.COOKIE_KEY_LOGIN_ID, "");
    cookie.setPath(Route.FOREGROUND_PREFIX);
    cookie.setMaxAge(0);
    response.addCookie(cookie);
    cookie = new Cookie(WebConfig.COOKIE_KEY_PASSWORD, "");
    cookie.setPath(Route.FOREGROUND_PREFIX);
    cookie.setMaxAge(0);
    response.addCookie(cookie);
    return mav;
  }

  @RequestMapping(Route.BG_USERS)
  public ModelAndView getUsers() {
    return new ModelAndView("admin/users");
  }

  @RequestMapping(
      value = Route.BG_USERS_JSON,
      produces = {"application/json; charset=utf-8"})
  @ResponseBody
  public PaginatedList getUsersJson(
      @RequestParam(
          value = "search",
          required = false) String search,
      @RequestParam(
          value = "order",
          required = false) String order,
      @RequestParam(
          value = "sort",
          required = false) String sort,
      @RequestParam("limit") int limit,
      @RequestParam("offset") int offset) {
    PaginatedList response = null;
    try {
      final boolean asc = "asc".equalsIgnoreCase(order);
      final Pageable pageable = PageUtil.pageWithOffsetAndCount(offset, limit, sort, asc);
      final Page<UserBean> results = userService.searchUsers(null, search, pageable);
      if (results.hasContent()) {
        for (final UserBean u : results.getContent()) {
          u.setStatusName(
              messageSource.getMessage(u.getStatus().i18nKey(), null, null));
          u.setRoleName(messageSource.getMessage(u.getRole().i18nKey(), null, null));
        }
      }
      response = new PaginatedList();
      response.setTotal((int) results.getTotalElements());
      response.setRows(results.getContent());
    } catch (final Exception e) {
      logger.error("Failed to get user list.", e);
    } catch (final Error e) {
      logger.error("Failed to get user list.", e);
    }
    return response;
  }

  @RequestMapping(
      value = Route.BG_ADD_USER,
      method = RequestMethod.GET)
  public ModelAndView addUserForm() {
    final ModelAndView mav = new ModelAndView("admin/add_user");
    mav.addObject("user", new UserBean());
    mav.addObject("allGroups", groupService.getAvailableGroups());
    return mav;
  }

  @RequestMapping(
      value = Route.BG_ADD_USER,
      method = RequestMethod.POST)
  public ModelAndView addUser(@ModelAttribute("user") @Valid UserBean user,
      BindingResult br) {
    final ModelAndView mav = new ModelAndView("admin/add_user");
    final UserBean duplicate = userService.getUserByMobile(user.getMobile());
    if ((duplicate != null) && !duplicate.getId().equals(user.getId())) {
      br.rejectValue("mobile", "Duplicate.user.mobile");
    }
    if (br.hasErrors()) {
      mav.addObject("user", user);
    } else {
      try {
        user.setCreateTime(new Date());
        userService.saveUser(user);
        mav.addObject(WebConfig.WEB_PAGE_MESSAGE,
            messageSource.getMessage("operation.success", null, null));
        mav.addObject("user", new UserBean());
      } catch (final Exception e) {
        mav.addObject("user", user);
        mav.addObject(WebConfig.WEB_PAGE_ERROR,
            messageSource.getMessage("operation.fail", null, null));
      }
    }
    mav.addObject("allGroups", groupService.getAvailableGroups());
    return mav;
  }

  @RequestMapping(
      value = Route.BG_EDIT_USER,
      method = RequestMethod.GET)
  public ModelAndView editUserForm(@PathVariable("user_id") Integer userId) {
    final ModelAndView mav = new ModelAndView("admin/edit_user");
    final UserBean user = userService.getUserById(userId);
    if (user != null) {
      mav.addObject("user", user);
      mav.addObject("allGroups", groupService.getAvailableGroups());
    } else {
      mav.setViewName("redirect:/404.html");
    }
    return mav;
  }

  @RequestMapping(
      value = Route.BG_EDIT_USER,
      method = RequestMethod.POST)
  public ModelAndView editUser(@PathVariable("user_id") Integer userId,
      @ModelAttribute("user") @Valid UserBean user, BindingResult br,
      HttpSession session) {
    final ModelAndView mav = new ModelAndView("admin/edit_user");
    final UserBean currentUser = WebConfig.getBackgroundUserInSession(session);
    final UserBean existUser = userService.getUserById(userId);
    if (existUser == null) {
      mav.setViewName("redirect:/404.html");
    } else {
      if (currentUser.getId().equals(existUser.getId())) {
        if (user.getStatus() == UserStatus.Disabled) {
          br.rejectValue("status", "user.cannot.disable.self");
        } else if (user.getRole() != existUser.getRole()) {
          br.rejectValue("role", "user.cannot.change.self.role");
        }
      }
      if (!br.hasErrors()) {
        final UserBean duplicate = userService.getUserByMobile(user.getMobile());
        if ((duplicate != null) && !duplicate.getId().equals(userId)) {
          br.rejectValue("mobile", "Duplicate.user.mobile");
        }
      }
      user.setId(userId);
      if (!br.hasErrors()) {
        try {
          existUser.setMobile(user.getMobile());
          existUser.setName(user.getName());
          existUser.setRole(user.getRole());
          existUser.setGroup(user.getGroup());
          existUser.setStatus(user.getStatus());
          existUser.setUpdateTime(new Date());
          userService.saveUser(existUser);
          mav.addObject(WebConfig.WEB_PAGE_MESSAGE,
              messageSource.getMessage("operation.success", null, null));
        } catch (final Exception e) {
          mav.addObject(WebConfig.WEB_PAGE_ERROR,
              messageSource.getMessage("operation.fail", null, null));
        }
      }
      mav.addObject("user", user);
      mav.addObject("allGroups", groupService.getAvailableGroups());
    }
    return mav;
  }

  @RequestMapping(Route.BG_DELETE_USER)
  public ModelAndView deleteUser(@PathVariable("user_id") Integer userId,
      HttpSession session) {
    final ModelAndView mav = new ModelAndView("redirect:" + Route.BG_USERS);
    final UserBean me = WebConfig.getBackgroundUserInSession(session);
    final UserBean user = userService.getUserById(userId);
    if (user == null) {
      mav.setViewName("redirect:/404.html");
    } else {
      if (me.getId().equals(user.getId())) {
        mav.addObject(WebConfig.WEB_PAGE_ERROR,
            messageSource.getMessage("user.cannot.delete.self", null, null));
      } else {
        userService.deleteUserById(userId);
      }
    }
    return mav;
  }

  @RequestMapping(
      value = Route.BG_CHANGE_USER_PWD,
      method = RequestMethod.GET)
  public ModelAndView changePwdForm(@PathVariable("user_id") Integer userId) {
    final ModelAndView mav = new ModelAndView("admin/change_user_pwd");
    final UserBean user = userService.getUserById(userId);
    if (user == null) {
      mav.setViewName("redirect:/404.html");
    } else if (user.getRole() != UserRole.MobileUser) {
      mav.setViewName("redirect:" + Route.BG_USERS);
    } else {
      mav.addObject("user", user);
    }
    return mav;
  }

  @RequestMapping(
      value = Route.BG_CHANGE_USER_PWD,
      method = RequestMethod.POST)
  public ModelAndView changePwd(
      @PathVariable("user_id") Integer userId,
      @ModelAttribute("user") UserBean user) {
    final ModelAndView mav = new ModelAndView("admin/change_user_pwd");
    final UserBean localUser = userService.getUserById(userId);
    if (localUser == null) {
      mav.setViewName("redirect:/404.html");
    } else if (localUser.getRole() != UserRole.MobileUser) {
      mav.addObject("user", localUser);
      mav.addObject(WebConfig.WEB_PAGE_ERROR,
          messageSource.getMessage("user.cannot.change.pwd", null, null));
    } else {
      localUser.setPassword(user.getPassword());
      localUser.setUpdateTime(new Date());
      userService.saveUser(localUser);
      mav.addObject("user", localUser);
      mav.addObject(WebConfig.WEB_PAGE_MESSAGE,
          messageSource.getMessage("operation.success", null, null));
    }
    return mav;
  }
}
