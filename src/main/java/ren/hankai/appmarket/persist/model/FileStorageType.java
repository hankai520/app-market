/*
 * Copyright © 2015 Jiangsu Sparknet Software Co., Ltd. All Rights Reserved
 * http://www.sparksoft.com.cn
 */

package ren.hankai.appmarket.persist.model;

/**
 * 文件存储类型。
 *
 * @author hankai
 * @version 1.0.0
 */
public enum FileStorageType {

  /**
   * 本地。
   */
  LocalFS("0"),
  /**
   * 腾讯云对象存储。
   */
  TencentCOS("1"),

  ;

  public static FileStorageType fromString(final String value) {
    if (LocalFS.value.equals(value)) {
      return LocalFS;
    } else if (TencentCOS.value.equals(value)) {
      return TencentCOS;
    }
    return null;
  }

  private final String value;

  private FileStorageType(final String value) {
    this.value = value;
  }

  public String value() {
    return value;
  }
}
