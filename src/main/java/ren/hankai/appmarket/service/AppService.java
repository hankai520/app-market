
package ren.hankai.appmarket.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.multipart.MultipartFile;
import ren.hankai.appmarket.persist.AppRepository;
import ren.hankai.appmarket.persist.AppRepository.AppSpecs;
import ren.hankai.appmarket.persist.model.App;
import ren.hankai.appmarket.persist.model.AppPlatform;
import ren.hankai.appmarket.persist.support.EntitySpecs;
import ren.hankai.appmarket.persist.util.PageUtil;
import ren.hankai.appmarket.util.MobileAppInfo;
import ren.hankai.appmarket.util.MobileAppScanner;
import ren.hankai.cordwood.core.Preferences;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Random;

/**
 * App业务逻辑。
 *
 * @author hankai
 * @version 1.0.0
 * @since May 15, 2017 2:17:17 PM
 */
@Service
public class AppService {

  private static final Logger logger = LoggerFactory.getLogger(AppService.class);

  @Autowired
  private AppRepository appRepo;
  @Autowired
  private MobileAppScanner appScanner;

  /**
   * TODO Missing method description。
   *
   * @param id
   * @return
   * @author hankai
   * @since May 15, 2017 2:33:23 PM
   */
  public App getAppById(Integer id) {
    return appRepo.findOne(id);
  }

  /**
   * TODO Missing method description。
   *
   * @param sku
   * @return
   * @author hankai
   * @since May 15, 2017 2:33:25 PM
   */
  public App getAppBySku(String sku) {
    return appRepo.findFirst(EntitySpecs.field("sku", sku));
  }

  /**
   * TODO Missing method description。
   *
   * @param app
   * @return
   * @author hankai
   * @since May 15, 2017 2:33:28 PM
   */
  @Transactional
  public App saveApp(App app) {
    return appRepo.save(app);
  }

  /**
   * TODO Missing method description。
   *
   * @param app
   * @param appInfo
   * @return
   * @author hankai
   * @since May 15, 2017 6:12:07 PM
   */
  @Transactional
  public App saveApp(App app, MobileAppInfo appInfo) {
    final App savedApp = appRepo.save(app);
    try {
      if (appInfo != null) {
        final File bundleFile = new File(appInfo.getBundlePath());
        if (bundleFile.exists()) {
          final String appPath = getAppBundlePath(savedApp);
          FileCopyUtils.copy(bundleFile, new File(appPath));
        }
        if ((appInfo.getIcon() != null) && (appInfo.getIcon().length > 0)) {
          final String iconPath = getAppIconPath(savedApp);
          FileCopyUtils.copy(appInfo.getIcon(), new File(iconPath));
        }
      }
    } catch (final IOException ex) {
      logger.error(
          String.format("Failed to save app bundle and icon for app \"%s\".", app.getName()), ex);
    }
    return app;
  }

  /**
   * TODO Missing method description。
   *
   * @param app
   * @return
   * @author hankai
   * @since May 15, 2017 6:19:36 PM
   */
  public String getAppBundlePath(App app) {
    String name = app.getBundleIdentifier() + "_" + app.getVersion();
    name = name.replaceAll("\\s|\\.|#", "_");
    String appPath =
        Preferences.getAttachmentDir() + File.separator + name;
    if (app.getPlatform() == AppPlatform.Android) {
      appPath += ".apk";
    } else if (app.getPlatform() == AppPlatform.iOS) {
      appPath += ".ipa";
    } else {
      appPath += ".unknown";
    }
    return appPath;
  }

  /**
   * TODO Missing method description。
   *
   * @param app
   * @return
   * @author hankai
   * @since May 15, 2017 6:20:19 PM
   */
  public String getAppIconPath(App app) {
    String name = app.getBundleIdentifier() + "_" + app.getVersion();
    name = name.replaceAll("\\s|\\.|#", "_");
    final String iconPath =
        Preferences.getAttachmentDir() + File.separator + name + ".icon";
    return iconPath;
  }

  /**
   * TODO Missing method description。
   *
   * @param id
   * @author hankai
   * @since May 15, 2017 2:38:21 PM
   */
  @Transactional
  public void deleteAppById(Integer id) {
    appRepo.delete(id);
  }

  /**
   * 获取应用包随机缓存路径
   *
   * @param appId 应用ID
   * @param platform 应用运行平台
   * @return 包路径
   * @author hankai
   * @since Apr 6, 2016 2:46:48 PM
   */
  public String getRandomAppPackageCachePath() {
    final String fileName = System.currentTimeMillis() + new Random().nextInt(9999) + 1000 + "";
    final String path = String.format("%s/%s", Preferences.getAttachmentDir(), fileName);
    return path;
  }


  /**
   * 保存 APP 安装包并解析APP安装包信息
   *
   * @param appId 应用ID
   * @param platform 应用运行平台
   * @param packageFile 程序包文件
   * @return APP元数据（返回空表示保存失败）
   * @author hankai
   * @since Apr 6, 2016 2:32:12 PM
   */
  public MobileAppInfo saveAppPackage(AppPlatform platform, MultipartFile packageFile) {
    MobileAppInfo mai = null;
    try {
      final String path = getRandomAppPackageCachePath();
      final FileOutputStream fos = new FileOutputStream(path);
      FileCopyUtils.copy(packageFile.getInputStream(), fos);
      if (platform == AppPlatform.Android) {
        mai = appScanner.scanAndroidApk(path);
      } else if (platform == AppPlatform.iOS) {
        mai = appScanner.scanIosIpa(path);
      }
      if (mai != null) {
        mai.setBundlePath(path);
      }
    } catch (final IOException e) {
      logger.error("Failed to save app package.", e);
    }
    return mai;
  }

  /**
   * TODO Missing method description。
   *
   * @param keyword
   * @param pageable
   * @return
   * @author hankai
   * @since May 15, 2017 2:33:18 PM
   */
  public Page<App> searchApps(String keyword, Pageable pageable) {
    return appRepo.findAll(AppSpecs.byKeyword(keyword), pageable);
  }

  /**
   * 查询可在前台访问的app（若不设置分页，则返回按更新时间降序排列后的前20个app）。
   *
   * @param pageable 分页
   *
   * @return 应用列表
   * @author hankai
   * @since Apr 5, 2016 5:28:23 PM
   */
  public Page<App> getAvailableApps(Pageable pageable) {
    if (pageable == null) {
      pageable = PageUtil.pageWithIndexAndSize(1, 20, "updateTime", false);
    }
    return appRepo.findAll(AppSpecs.readyToSaleApps(), pageable);
  }

}
