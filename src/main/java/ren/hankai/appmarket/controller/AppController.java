/*
 * Copyright © 2016 Jiangsu Sparknet Software Co., Ltd. All rights reserved.
 * http://www.sparksoft.com.cn
 */

package ren.hankai.appmarket.controller;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
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
import ren.hankai.appmarket.persist.model.AppPlatform;
import ren.hankai.appmarket.persist.model.UserBean;
import ren.hankai.appmarket.persist.util.PageUtil;
import ren.hankai.appmarket.service.AppService;
import ren.hankai.appmarket.service.GroupService;
import ren.hankai.appmarket.util.MobileAppInfo;

import java.io.File;
import java.io.IOException;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
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

  @Value("${proxy.name}")
  private String proxyName;
  @Value("${proxy.port}")
  private Integer proxyPort;
  @Value("${proxy.scheme}")
  private String proxyScheme;

  /**
   * 获取部署后的实际 web 根地址（地址不以 "/" 结尾）。
   *
   * @param request servlet 请求
   * @return 根地址
   * @author hankai
   * @since Jul 25, 2018 8:42:05 PM
   */
  private String getActualBasePath(final HttpServletRequest request) {
    String basePath = null;
    if (StringUtils.isNotEmpty(proxyName)) {
      basePath = proxyScheme + "://" + proxyName + ":" + proxyPort;
    } else {
      basePath =
          request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort();
    }
    return basePath;
  }

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
          required = false) final String search,
      @RequestParam(
          value = "order",
          required = false) final String order,
      @RequestParam(
          value = "sort",
          required = false) final String sort,
      @RequestParam("limit") final int limit,
      @RequestParam("offset") final int offset) {
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
      @ModelAttribute("app") @Valid final AppBean app,
      final BindingResult br) {
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
  public ModelAndView showEditAppForm(@PathVariable("appId") final Integer appId) {
    final ModelAndView mav = new ModelAndView("admin/edit_app");
    final AppBean app = appService.getAppById(appId);
    if (app != null) {
      // app.setChecksum(appService.getAppBundleChecksum(app));
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
      @PathVariable("appId") final Integer appId,
      @ModelAttribute("app") @Valid final AppBean app, final BindingResult br) {
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
  public ModelAndView deleteApp(@PathVariable("appId") final Integer appId) {
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
  public ModelAndView foregroundIndex(final HttpServletRequest request, final HttpSession session) {
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

  @RequestMapping(value = Route.BG_APP_QRCODE, produces = MediaType.IMAGE_PNG_VALUE)
  public void showAppQrCode(@PathVariable("appId") final Integer appId,
      final HttpServletRequest request, final HttpServletResponse response) {
    final AppBean app = appService.getAppById(appId);
    if (app == null) {
      response.setStatus(HttpStatus.NOT_FOUND.value());
    } else {
      try {
        final String basePath = getActualBasePath(request);
        final String url = basePath + Route.BG_DOWNLOAD_APP + "?sku=" + app.getSku();
        appService.generateQrCodeForApp(url, response.getOutputStream());
      } catch (final IOException ex) {
        response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
      }
    }
  }

  @RequestMapping(value = Route.BG_DOWNLOAD_APP)
  public ModelAndView downloadApp(@RequestParam("sku") final String sku,
      final HttpServletRequest request) {
    final ModelAndView mav = new ModelAndView("admin/download_app");
    final AppBean app = appService.getAppBySku(sku);
    if (app == null) {
      mav.setViewName("redirect:/404.html");
    } else {
      final String basePath = getActualBasePath(request);
      if (AppPlatform.Android == app.getPlatform()) {
        mav.addObject("downloadUrl", basePath +
            Route.API_APP_ANDROID_PACKAGE.replaceAll("\\{appId\\}", app.getId() + ""));
      } else if (AppPlatform.iOS == app.getPlatform()) {
        mav.addObject("downloadUrl", basePath +
            Route.API_APP_IOS_PACKAGE.replaceAll("\\{appId\\}", app.getId() + ""));
      } else {
        mav.setViewName("redirect:/404.html");
      }
    }
    return mav;
  }
}
