
package ren.hankai.appmarket.service;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import org.apache.commons.codec.digest.DigestUtils;
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
import ren.hankai.appmarket.persist.TencentCloudRepository;
import ren.hankai.appmarket.persist.UserGroupRepository;
import ren.hankai.appmarket.persist.UserRepository;
import ren.hankai.appmarket.persist.model.AppBean;
import ren.hankai.appmarket.persist.model.AppPlatform;
import ren.hankai.appmarket.persist.model.FileStorageType;
import ren.hankai.appmarket.persist.model.UserBean;
import ren.hankai.appmarket.persist.model.UserGroupBean;
import ren.hankai.appmarket.persist.util.PageUtil;
import ren.hankai.appmarket.util.MobileAppInfo;
import ren.hankai.appmarket.util.MobileAppScanner;
import ren.hankai.cordwood.core.Preferences;
import ren.hankai.cordwood.data.domain.EntitySpecs;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
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
  @Autowired
  private TencentCloudRepository tcRepo;

  /**
   * 根据ID查询应用，同时返回应用对哪些用户组可见。
   *
   * @param id 应用ID
   * @return 查询条件
   * @author hankai
   * @since May 15, 2017 2:33:23 PM
   */
  public AppBean getAppById(final Integer id) {
    final AppBean appBean = appRepo.findOne(id);
    for (final UserGroupBean gb : appBean.getUserGroups()) {
      appBean.getGroupIds().add(gb.getId());
    }
    return appBean;
  }

  /**
   * 根据SKU查询应用。
   *
   * @param sku 应用SKU
   * @return 应用信息
   * @author hankai
   * @since May 15, 2017 2:33:25 PM
   */
  public AppBean getAppBySku(final String sku) {
    return appRepo.findFirst(EntitySpecs.field("sku", sku));
  }

  /**
   * 根据名称查询应用。
   *
   * @param name 名称
   * @return 应用信息
   * @author hankai
   * @since Aug 3, 2017 3:12:34 PM
   */
  public AppBean getAppByName(final String name) {
    return appRepo.findFirst(EntitySpecs.field("name", name));
  }

  /**
   * 保存应用。
   *
   * @param app 应用信息
   * @return 保存后的应用信息
   * @author hankai
   * @since May 15, 2017 2:33:28 PM
   */
  @Transactional
  public AppBean saveApp(final AppBean app) {
    return appRepo.save(app);
  }

  /**
   * 保存应用及应用安装包元数据。
   *
   * @param app 应用
   * @param appInfo 应用元数据
   * @return 保存的应用信息
   * @author hankai
   * @since May 15, 2017 6:12:07 PM
   */
  @Transactional
  public AppBean saveApp(final AppBean app, final MobileAppInfo appInfo) {
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
          final FileStorageType fst =
              FileStorageType.fromString(Preferences.getCustomConfig("fileStorageType"));
          if (FileStorageType.TencentCOS == fst) {
            final String key = getAppBundleName(savedApp);
            final boolean saved = tcRepo.save(key, bundleFile);
            if (!saved) {
              throw new RuntimeException("上传应用包到腾讯云失败");
            }
          } else {
            final String appPath = getAppBundlePath(savedApp);
            FileCopyUtils.copy(bundleFile, new File(appPath));
          }
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
   * 获取应用的安装包路径。
   *
   * @param app 应用信息
   * @return 安装包路径
   * @author hankai
   * @since May 15, 2017 6:19:36 PM
   */
  public String getAppBundlePath(final AppBean app) {
    final String name = getAppBundleName(app);
    String appPath = Preferences.getAttachmentDir() + File.separator + name;
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
   * 获取应用安装包文件名。内部使用应用信息构造唯一名称并确保名称对文件路径友好。
   *
   * @param app 应用信息
   * @return 安装包文件名
   */
  public String getAppBundleName(final AppBean app) {
    String name = app.getSku() + "_" + app.getBundleIdentifier() + "_" + app.getVersion();
    name = name.replaceAll("\\s|\\.|#", "_");
    if (app.getPlatform() == AppPlatform.Android) {
      name += ".apk";
    } else if (app.getPlatform() == AppPlatform.iOS) {
      name += ".ipa";
    } else {
      name += ".unknown";
    }
    return name;
  }

  /**
   * 获取应用安装包的云端下载地址。
   *
   * @param app 应用信息
   * @return 下载地址
   */
  public String getAppBundleUrl(final AppBean app) {
    final String key = getAppBundleName(app);
    return tcRepo.getDownloadUrl(key);
  }

  /**
   * 获取App安装包的校验和。
   *
   * @param app app信息
   * @return SHA256 校验和
   * @author hankai
   * @since Jul 25, 2018 7:16:01 PM
   */
  public String getAppBundleChecksum(final AppBean app) {
    final String path = getAppBundlePath(app);
    final File file = new File(path);
    if ((file != null) && file.exists()) {
      try {
        final String checksum = DigestUtils.sha256Hex(new FileInputStream(file));
        return checksum;
      } catch (final IOException ex) {
        logger.warn("Failed to get checksum of app package " + path);
      }
    }
    return "";
  }

  /**
   * 获取应用的logo图标文件路径。
   *
   * @param app 应用信息
   * @return 图标文件路径
   * @author hankai
   * @since May 15, 2017 6:20:19 PM
   */
  public String getAppIconPath(final AppBean app) {
    String name = app.getBundleIdentifier() + "_" + app.getVersion();
    name = name.replaceAll("\\s|\\.|#", "_");
    final String iconPath =
        Preferences.getAttachmentDir() + File.separator + name + ".icon";
    return iconPath;
  }

  /**
   * 根据ID删除应用。
   *
   * @param id 应用ID
   * @author hankai
   * @since May 15, 2017 2:38:21 PM
   */
  @Transactional
  public void deleteAppById(final Integer id) {
    appRepo.delete(id);
  }

  /**
   * 获取应用包随机缓存路径。
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
  public MobileAppInfo saveAppPackage(final AppPlatform platform, final MultipartFile packageFile) {
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
   * 根据关键字搜索应用。
   *
   * @param keyword 关键字
   * @param pageable 分页
   * @return 搜索结果
   * @author hankai
   * @since May 15, 2017 2:33:18 PM
   */
  public Page<AppBean> searchApps(final String keyword, final Pageable pageable) {
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
  public Page<AppBean> getAvailableApps(final Integer userId, Pageable pageable) {
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

  /**
   * 为 App 生成下载二维码。
   *
   * @param url 下载 URL（即二维码内容）
   * @param outputStream 二维码图片输出流
   * @author hankai
   * @since Jul 25, 2018 7:59:57 PM
   */
  public void generateQrCodeForApp(final String url, final OutputStream outputStream) {
    final QRCodeWriter qw = new QRCodeWriter();
    final Map<EncodeHintType, Object> hints = new EnumMap<>(EncodeHintType.class);
    hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
    hints.put(EncodeHintType.MARGIN, 0);
    try {
      final BitMatrix bm = qw.encode(url, BarcodeFormat.QR_CODE, 300, 300, hints);
      MatrixToImageWriter.writeToStream(bm, "PNG", outputStream);
    } catch (final Exception ex) {
      logger.error(String.format("Failed to generate qrcode for app url \"%s\".", url), ex);
    }
  }

}
