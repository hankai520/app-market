
package ren.hankai.appmarket.service;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.multipart.MultipartFile;
import ren.hankai.appmarket.persist.AppRepository;
import ren.hankai.appmarket.persist.AppRepository.AppSpecs;
import ren.hankai.appmarket.persist.UserGroupRepository;
import ren.hankai.appmarket.persist.UserRepository;
import ren.hankai.appmarket.persist.model.AppBean;
import ren.hankai.appmarket.persist.model.AppPlatform;
import ren.hankai.appmarket.persist.model.UserBean;
import ren.hankai.appmarket.persist.model.UserGroupBean;
import ren.hankai.appmarket.persist.support.EntitySpecs;
import ren.hankai.appmarket.persist.util.PageUtil;
import ren.hankai.appmarket.util.MobileAppInfo;
import ren.hankai.appmarket.util.MobileAppScanner;
import ren.hankai.cordwood.core.Preferences;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

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
  @Autowired
  private UserGroupRepository userGroupRepo;
  @Autowired
  private EntityManager entityManager;
  @Autowired
  private UserRepository userRepo;

  /**
   * TODO Missing method description。
   *
   * @param id
   * @return
   * @author hankai
   * @since May 15, 2017 2:33:23 PM
   */
  public AppBean getAppById(Integer id) {
    final AppBean appBean = appRepo.findOne(id);
    for (final UserGroupBean gb : appBean.getUserGroups()) {
      appBean.getGroupIds().add(gb.getId());
    }
    return appBean;
  }

  /**
   * TODO Missing method description。
   *
   * @param sku
   * @return
   * @author hankai
   * @since May 15, 2017 2:33:25 PM
   */
  public AppBean getAppBySku(String sku) {
    return appRepo.findFirst(EntitySpecs.field("sku", sku));
  }

  /**
   * TODO Missing method description。
   *
   * @param name
   * @return
   * @author hankai
   * @since Aug 3, 2017 3:12:34 PM
   */
  public AppBean getAppByName(String name) {
    return appRepo.findFirst(EntitySpecs.field("name", name));
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
  public AppBean saveApp(AppBean app) {
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
  public AppBean saveApp(AppBean app, MobileAppInfo appInfo) {
    for (final UserGroupBean gb : app.getUserGroups()) {
      gb.getApps().remove(app);
    }
    app.getUserGroups().clear();
    if (app.getGroupIds() != null) {
      for (final Integer gid : app.getGroupIds()) {
        final UserGroupBean gb = userGroupRepo.findOne(gid);
        if (gb != null) {
          gb.getApps().add(app);
          app.getUserGroups().add(gb);
        }
      }
    }
    final AppBean savedApp = appRepo.save(app);
    try {
      if (appInfo != null) {
        final File bundleFile = new File(appInfo.getBundlePath());
        if (bundleFile.exists()) {
          final String appPath = getAppBundlePath(savedApp);
          FileCopyUtils.copy(bundleFile, new File(appPath));
          if (!FileUtils.deleteQuietly(bundleFile)) {
            logger.error("Failed to delete uploaded package at path: " + appInfo.getBundlePath());
          }
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
  public String getAppBundlePath(AppBean app) {
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
  public String getAppIconPath(AppBean app) {
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
    final String path = String.format("%s/%s", Preferences.getCacheDir(), fileName);
    return path;
  }


  /**
   * 保存 APP 安装包并解析APP安装包信息，安装包将被保存到缓存目录。
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
      if ((packageFile != null) && (packageFile.getSize() > 0)) {
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
  public Page<AppBean> searchApps(String keyword, Pageable pageable) {
    return appRepo.findAll(AppSpecs.byKeyword(keyword), pageable);
  }

  /**
   * 查询可在前台访问的app（若不设置分页，则返回按更新时间降序排列后的前20个app）。
   *
   * @param userId 当前登录的用户ID
   * @param pageable 分页
   *
   * @return 应用列表
   * @author hankai
   * @since Apr 5, 2016 5:28:23 PM
   */
  public Page<AppBean> getAvailableApps(Integer userId, Pageable pageable) {
    if (pageable == null) {
      pageable = PageUtil.pageWithIndexAndSize(1, 20, "updateTime", false);
    }
    final UserBean user = userRepo.findOne(userId);
    if (user != null) {
      final TypedQuery<AppBean> query = entityManager.createQuery(
          "select app from AppBean app inner join app.userGroups groups where groups.id=:userGroupId",
          AppBean.class);
      query.setParameter("userGroupId", user.getGroup().getId());
      query.setFirstResult(pageable.getOffset()).setMaxResults(pageable.getPageSize());
      final List<AppBean> apps = query.getResultList();
      return new PageImpl<>(apps);
    }
    return new PageImpl<>(new ArrayList<AppBean>());
  }

}
