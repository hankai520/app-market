/*
 * Copyright © 2016 Jiangsu Sparknet Software Co., Ltd. All rights reserved.
 * http://www.sparksoft.com.cn
 */

package ren.hankai.appmarket.api;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import ren.hankai.appmarket.api.payload.ApiCode;
import ren.hankai.appmarket.api.payload.ApiResponse;
import ren.hankai.appmarket.api.payload.BusinessError;
import ren.hankai.appmarket.config.Route;
import ren.hankai.appmarket.persist.model.AppBean;
import ren.hankai.appmarket.persist.model.AppPlatform;
import ren.hankai.appmarket.persist.model.FileStorageType;
import ren.hankai.appmarket.service.AppService;
import ren.hankai.appmarket.util.MobileAppInfo;
import ren.hankai.cordwood.core.Preferences;
import ren.hankai.cordwood.core.cache.LightWeight;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

/**
 * 应用信息 API
 *
 * @author hankai
 * @version 1.0
 * @since Mar 15, 2016 2:17:24 PM
 */
@Controller
public class AppApi {

  private static final Logger logger = LoggerFactory.getLogger(AppApi.class);
  @Autowired
  private AppService appService;
  @Autowired
  private Configuration freeMarkerConfig;

  @RequestMapping(value = {Route.API_APP_IOS_PACKAGE, Route.API_APP_ANDROID_PACKAGE},
      produces = {MediaType.APPLICATION_OCTET_STREAM_VALUE})
  public ResponseEntity<Resource> getAppPackage(
      @PathVariable("appId") final Integer appId) {
    final AppBean app = appService.getAppById(appId);
    if (app != null) {
      final FileStorageType fst =
          FileStorageType.fromString(Preferences.getCustomConfig("fileStorageType"));
      if (FileStorageType.TencentCOS == fst) {
        final HttpHeaders header = new HttpHeaders();
        header.add("Location", appService.getAppBundleUrl(app));
        return new ResponseEntity<>(header, HttpStatus.FOUND);
      } else {
        final String path = appService.getAppBundlePath(app);
        return new ResponseEntity<>(new FileSystemResource(path), HttpStatus.OK);
      }
    } else {
      return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
  }

  @RequestMapping(value = {Route.API_APP_ICON}, produces = {MediaType.IMAGE_PNG_VALUE})
  public ResponseEntity<FileSystemResource> getAppIcon(@PathVariable("appId") final Integer appId) {
    final AppBean app = appService.getAppById(appId);
    if (app != null) {
      final String path = appService.getAppIconPath(app);
      return new ResponseEntity<>(new FileSystemResource(path), HttpStatus.OK);
    } else {
      return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
  }

  @RequestMapping(value = Route.API_IOS_MANIFEST, produces = {MediaType.TEXT_XML_VALUE})
  public ResponseEntity<String> generateIosManifest(@PathVariable("appId") final Integer appId,
      final HttpServletRequest request) {
    final AppBean app = appService.getAppById(appId);
    if (app != null) {
      final String baseUrl = request.getScheme() + "://" + request.getServerName() + ":"
          + request.getServerPort() + request.getContextPath();
      final String packageUrl =
          baseUrl + Route.API_APP_IOS_PACKAGE.replaceAll("\\{appId\\}", appId + "");
      final Map<String, Object> model = new HashMap<>();
      model.put("packageUrl", packageUrl);
      final String iconUrl = baseUrl + Route.API_APP_ICON.replaceAll("\\{appId\\}", appId + "");
      model.put("smallImageUrl", iconUrl);
      model.put("largeImageUrl", iconUrl);
      model.put("bundleIdentifier", app.getBundleIdentifier());
      model.put("bundleVersion", app.getVersion());
      model.put("appName", app.getName());
      try {
        final Template template = freeMarkerConfig.getTemplate("ios_manifest.xml");
        final String manifest = FreeMarkerTemplateUtils.processTemplateIntoString(template, model);
        return new ResponseEntity<>(manifest, HttpStatus.OK);
      } catch (final IOException ex) {
        logger.error("Failed to read template \"ios_manifest.xml\"", ex);
        return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
      } catch (final TemplateException ex) {
        logger.error("Failed to render template \"ios_manifest.xml\"", ex);
        return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
      }
    } else {
      return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
  }

  @LightWeight
  @RequestMapping(value = Route.API_APP_METADATA, produces = {"application/json; charset=utf-8"})
  @ResponseBody
  public ApiResponse getAppMetaData(@PathVariable("sku") final String sku) {
    final ApiResponse response = new ApiResponse();
    try {
      final AppBean app = appService.getAppBySku(sku);
      if (app != null) {
        app.setUserGroups(null);
        app.setGroupIds(null);
        if (!app.isEnableUpdateCheck()) {
          response.getBody().setError(BusinessError.AppNotFound);
        } else {
          response.getBody().setData(app);
          response.getBody().setSuccess(true);
        }
      } else {
        response.getBody().setError(BusinessError.AppNotFound);
      }
      response.setCode(ApiCode.Success);
    } catch (final Exception e) {
      logger.error(Route.API_APP_METADATA, e);
    } catch (final Error e) {
      logger.error(Route.API_APP_METADATA, e);
    }
    return response;
  }

  @RequestMapping(value = Route.API_UPDATE_APP)
  public ResponseEntity<String> updateApp(@PathVariable("sku") final String sku,
      @RequestPart("package") final MultipartFile file) {
    try {
      final AppBean app = appService.getAppBySku(sku);
      if (app != null) {
        if ((file != null) && (file.getSize() > 0)
            && file.getOriginalFilename().matches("(?i)^.*(\\.ipa|\\.apk)$")) {
          final String[] parts = file.getOriginalFilename().toLowerCase().split("\\.");
          final String ext = parts[parts.length - 1];
          final AppPlatform platform = ext.equals("ipa") ? AppPlatform.iOS : AppPlatform.Android;
          final MobileAppInfo mai = appService.saveAppPackage(platform, file);
          app.setBundleIdentifier(mai.getBundleId());
          app.setVersion(mai.getVersion());
          app.setUpdateTime(new Date());
          appService.saveApp(app, mai);
          if (!FileUtils.deleteQuietly(new File(mai.getBundlePath()))) {
            logger.error("Failed to delete uploaded package at path: " + mai.getBundlePath());
          }
          return new ResponseEntity<>(HttpStatus.OK);
        } else {
          return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
      } else {
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
      }
    } catch (final Exception e) {
      return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    } catch (final Error e) {
      return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }
}
