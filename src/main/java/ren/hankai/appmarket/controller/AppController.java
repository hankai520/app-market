/*
 * Copyright © 2016 Jiangsu Sparknet Software Co., Ltd. All rights reserved.
 *
 * http://www.sparksoft.com.cn
 */

package ren.hankai.appmarket.controller;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import ren.hankai.appmarket.api.payload.PaginatedList;
import ren.hankai.appmarket.config.Route;
import ren.hankai.appmarket.config.WebConfig;
import ren.hankai.appmarket.persist.model.AppBean;
import ren.hankai.appmarket.persist.model.UserBean;
import ren.hankai.appmarket.persist.util.PageUtil;
import ren.hankai.appmarket.service.AppService;
import ren.hankai.appmarket.service.GroupService;
import ren.hankai.appmarket.util.MobileAppInfo;

import java.io.File;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;

/**
 * 应用信息控制器
 *
 * @author hankai
 * @version 1.0
 * @since Mar 16, 2016 9:13:38 AM
 */
@Controller
public class AppController {

  private static final Logger logger = LoggerFactory.getLogger(AppController.class);
  @Autowired
  private AppService appService;
  @Autowired
  private MessageSource messageSource;
  @Autowired
  private GroupService groupService;

  @RequestMapping(Route.BG_APPS)
  public ModelAndView index() {
    return new ModelAndView("admin/apps");
  }

  @RequestMapping(
      value = Route.BG_AJAX_APPS,
      produces = {"application/json; charset=utf-8"})
  @ResponseBody
  public PaginatedList getAppList(
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
      final Page<AppBean> results = appService.searchApps(search, pageable);
      if (results.hasContent()) {
        for (final AppBean app : results.getContent()) {
          String str = messageSource.getMessage(app.getStatus().i18nKey(), null, null);
          app.setStatusDesc(str);
          str = messageSource.getMessage(app.getPlatform().i18nKey(), null, null);
          app.setPlatformDesc(str);
        }
      }
      response = new PaginatedList();
      response.setTotal((int) results.getTotalElements());
      response.setRows(results.getContent());
    } catch (final Exception e) {
      logger.error("Failed to get app list.", e);
    } catch (final Error e) {
      logger.error("Failed to get app list.", e);
    }
    return response;
  }

  @RequestMapping(
      value = Route.BG_APP_ADD,
      method = RequestMethod.GET)
  public ModelAndView showAddAppForm() {
    final ModelAndView mav = new ModelAndView("admin/add_app");
    mav.addObject("app", new AppBean());
    mav.addObject("allGroups", groupService.getAvailableGroups());
    return mav;
  }

  @RequestMapping(
      value = Route.BG_APP_ADD,
      method = RequestMethod.POST)
  public ModelAndView addApp(
      @ModelAttribute("app") @Valid AppBean app,
      BindingResult br) {
    final ModelAndView mav = new ModelAndView("admin/add_app");
    if (app.getPackageFile() == null) {
      br.rejectValue("packageFile", "NotNull.app.packageFile");
    }
    if (br.hasErrors()) {
      mav.addObject("app", app);
    } else {
      try {
        final MobileAppInfo mai = appService.saveAppPackage(app.getPlatform(),
            app.getPackageFile());
        if (mai != null) {
          app.setBundleIdentifier(mai.getBundleId());
          app.setVersion(mai.getVersion());
          app.setCreateTime(new Date());
          appService.saveApp(app, mai);
        }
        mav.addObject(WebConfig.WEB_PAGE_MESSAGE,
            messageSource.getMessage("operation.success", null, null));
        mav.addObject("app", new AppBean());
      } catch (final Exception e) {
        mav.addObject("app", app);
        mav.addObject(WebConfig.WEB_PAGE_ERROR,
            messageSource.getMessage("operation.fail", null, null));
      }
    }
    mav.addObject("allGroups", groupService.getAvailableGroups());
    return mav;
  }

  @RequestMapping(
      value = Route.BG_APP_EDIT,
      method = RequestMethod.GET)
  public ModelAndView showEditAppForm(@PathVariable("appId") Integer appId) {
    final ModelAndView mav = new ModelAndView("admin/edit_app");
    final AppBean app = appService.getAppById(appId);
    if (app != null) {
      mav.addObject("app", app);
      mav.addObject("allGroups", groupService.getAvailableGroups());
    } else {
      mav.setViewName("redirect:/404.html");
    }
    return mav;
  }

  @RequestMapping(
      value = Route.BG_APP_EDIT,
      method = RequestMethod.POST)
  public ModelAndView editApp(
      @PathVariable("appId") Integer appId,
      @ModelAttribute("app") @Valid AppBean app, BindingResult br) {
    final ModelAndView mav = new ModelAndView("admin/edit_app");
    final AppBean localApp = appService.getAppById(appId);
    if (localApp == null) {
      mav.setViewName("redirect:/404.html");
    } else {
      try {
        app.setId(appId);
        final AppBean dup = appService.getAppBySku(app.getSku());
        if ((dup != null) && !dup.getId().equals(appId)) {
          br.rejectValue("sku", "Duplicate.app.sku");
        }
        if (!br.hasErrors()) {
          MobileAppInfo mai = null;
          if ((app.getPackageFile() != null)
              && (app.getPackageFile().getSize() > 0l)) {
            mai = appService.saveAppPackage(app.getPlatform(), app.getPackageFile());
            localApp.setBundleIdentifier(mai.getBundleId());
            localApp.setVersion(mai.getVersion());
          }
          localApp.setName(app.getName());
          localApp.setSku(app.getSku());
          localApp.setPlatform(app.getPlatform());
          localApp.setMetaData(app.getMetaData());
          localApp.setStatus(app.getStatus());
          localApp.setEnableUpdateCheck(app.isEnableUpdateCheck());
          localApp.setUpdateTime(new Date());
          localApp.setGroupIds(app.getGroupIds());
          appService.saveApp(localApp, mai);
          if ((mai != null) && !FileUtils.deleteQuietly(new File(mai.getBundlePath()))) {
            logger.error("Failed to delete uploaded package at path: " + mai.getBundlePath());
          }
          mav.addObject(WebConfig.WEB_PAGE_MESSAGE,
              messageSource.getMessage("operation.success", null, null));
        }
      } catch (final Exception e) {
        mav.addObject(WebConfig.WEB_PAGE_ERROR,
            messageSource.getMessage("operation.fail", null, null));
      }
      mav.addObject("app", app);
      mav.addObject("allGroups", groupService.getAvailableGroups());
    }
    return mav;
  }

  @RequestMapping(
      value = Route.BG_APP_DELETE,
      method = RequestMethod.GET)
  public ModelAndView deleteApp(@PathVariable("appId") Integer appId) {
    final ModelAndView mav = new ModelAndView("redirect:" + Route.BG_APPS);
    final AppBean app = appService.getAppById(appId);
    if (app == null) {
      mav.setViewName("redirect:/404.html");
    } else {
      appService.deleteAppById(appId);
    }
    return mav;
  }

  @RequestMapping(Route.FG_APPS)
  public ModelAndView foregroundIndex(HttpServletRequest request, HttpSession session) {
    final ModelAndView mav = new ModelAndView("site/apps");
    final String path = request.getContextPath();
    final String basePath = request.getScheme() + "://"
        + request.getServerName() + ":" + request.getServerPort()
        + path;
    mav.addObject("basePath", basePath);
    final UserBean currentUser = WebConfig.getForegroundUserInSession(session);
    final Page<AppBean> results = appService.getAvailableApps(currentUser.getId(), null);
    mav.addObject("apps", results.getContent());
    return mav;
  }
}
