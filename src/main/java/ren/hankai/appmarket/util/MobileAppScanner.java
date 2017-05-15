/*
 * Copyright © 2016 Jiangsu Sparknet Software Co., Ltd. All rights reserved.
 *
 * http://www.sparksoft.com.cn
 */

package ren.hankai.appmarket.util;

import com.dd.plist.NSDictionary;
import com.dd.plist.PropertyListParser;

import net.dongliu.apk.parser.ApkParser;
import net.dongliu.apk.parser.bean.ApkMeta;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.FileCopyUtils;
import org.springframework.util.StringUtils;

import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * 扫描移动应用包元数据
 *
 * @author hankai
 * @version 1.0
 * @since May 16, 2016 11:44:24 AM
 */
@Component
public class MobileAppScanner {

  private static final Logger logger = LoggerFactory.getLogger(MobileAppScanner.class);

  /**
   * 扫描 Android .apk 文件元数据
   *
   * @param filePath apk 文件路径
   * @return 文件元数据
   * @author hankai
   * @since May 20, 2016 3:46:18 PM
   */
  public MobileAppInfo scanAndroidApk(String filePath) {
    MobileAppInfo mai = new MobileAppInfo();
    ApkParser parser = null;
    ZipFile zipFile = null;
    InputStream inputStream = null;
    try {
      parser = new ApkParser(filePath);
      ApkMeta meta = parser.getApkMeta();
      mai.setVersion(meta.getVersionName() + "#" + meta.getVersionCode());
      mai.setBundleId(meta.getPackageName());
      String iconPath = meta.getIcon();
      if (!StringUtils.isEmpty(iconPath)) {
        zipFile = new ZipFile(filePath, Charset.forName("UTF-8"));
        Enumeration<? extends ZipEntry> entries = zipFile.entries();
        while (entries.hasMoreElements()) {
          ZipEntry entry = entries.nextElement();
          if (entry.getName().contains(iconPath)) {
            inputStream = zipFile.getInputStream(entry);
            mai.setIcon(FileCopyUtils.copyToByteArray(inputStream));
            break;
          }
        }
      }
    } catch (Exception e) {
      logger.error(String.format("Failed to parse apk file at %s", filePath));
    } finally {
      try {
        if (parser != null) {
          parser.close();
        }
        if (zipFile != null) {
          zipFile.close();
        }
        if (inputStream != null) {
          inputStream.close();
        }
      } catch (Exception e) {
      }
    }
    return mai;
  }

  /**
   * 扫描 iOS .ipa 文件元数据
   *
   * @param filePath ipa 文件路径
   * @return 文件元数据
   * @author hankai
   * @since May 20, 2016 3:46:18 PM
   */
  public MobileAppInfo scanIosIpa(String filePath) {
    MobileAppInfo mai = new MobileAppInfo();
    ZipFile zipFile = null;
    try {
      zipFile = new ZipFile(filePath, Charset.forName("UTF-8"));
      Enumeration<? extends ZipEntry> entries = zipFile.entries();
      boolean foundIcon = false, foundMetaData = false;
      while (entries.hasMoreElements()) {
        InputStream stream = null;
        ZipEntry entry = entries.nextElement();
        try {
          if (entry.getName().matches("Payload/.*\\.app/Info.plist")) {
            stream = zipFile.getInputStream(entry);
            NSDictionary dict = (NSDictionary) PropertyListParser.parse(stream);
            mai.setVersion(dict.get("CFBundleShortVersionString").toString() + "#"
                + dict.get("CFBundleVersion").toString());
            mai.setBundleId(dict.get("CFBundleIdentifier").toString());
            foundMetaData = true;
          } else if (entry.getName().toLowerCase()
              .matches("payload/.*\\.app/appicon.*3x\\.png")) {
            stream = zipFile.getInputStream(entry);
            byte[] icon = FileCopyUtils.copyToByteArray(stream);
            mai.setIcon(icon);
            foundIcon = true;
          }
          if (foundMetaData && foundIcon) {
            break;
          }
        } catch (Exception e) {
          logger.error(String.format("Failed to parse entry %s", entry.getName()));
        } finally {
          if (stream != null) {
            stream.close();
          }
        }
      }
    } catch (Exception e) {
      logger.error(String.format("Failed to parse ipa file at %s", filePath));
    } finally {
      try {
        if (zipFile != null) {
          zipFile.close();
        }
      } catch (Exception e) {
      }
    }
    return mai;
  }
}
