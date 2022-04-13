package ren.hankai.appmarket.persist;

import java.io.File;

/**
 * 腾讯云存储（用于屏蔽具体实现细节，如对象存储、文件存储）。
 *
 * @author hankai
 * @since 1.0.0
 */
public interface TencentCloudRepository {

  /**
   * 保存/更新本地文件到腾讯云。
   *
   * @param key 文件唯一标识（标识相同则覆盖）
   * @param localFilePath 本地文件路径
   * @return 是否保存成功
   */
  boolean save(String key, String localFilePath);

  /**
   * 保存/更新本地文件到腾讯云。
   *
   * @param key 文件唯一标识（标识相同则覆盖）
   * @param localFile 本地文件
   * @return 是否保存成功
   */
  boolean save(String key, File localFile);

  /**
   * 获取文件下载地址。
   *
   * @param key 文件唯一标识（保存时指定）
   * @return
   */
  String getDownloadUrl(String key);

}
