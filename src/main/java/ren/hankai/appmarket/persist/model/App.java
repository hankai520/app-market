/*
 * Copyright © 2015 Jiangsu Sparknet Software Co., Ltd. All Rights Reserved
 *
 * http://www.sparksoft.com.cn
 */

package ren.hankai.appmarket.persist.model;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import org.eclipse.persistence.annotations.Cache;
import org.eclipse.persistence.annotations.CacheCoordinationType;
import org.eclipse.persistence.annotations.CacheType;
import org.springframework.web.multipart.MultipartFile;
import ren.hankai.appmarket.util.DateTimeSerializer;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

/**
 * 无线应用信息
 *
 * @author hankai
 * @version 1.0
 * @since Jul 16, 2015 2:00:29 PM
 */
@Entity
@Table(
    name = "apps")
@Cacheable(true)
@Cache(
    type = CacheType.SOFT,
    size = 1024 * 16,
    expiry = 1000 * 60 * 2,
    coordinationType = CacheCoordinationType.INVALIDATE_CHANGED_OBJECTS)
public class App implements Serializable {

  private static final long serialVersionUID = 1L;
  @Id
  @GeneratedValue(
      strategy = GenerationType.IDENTITY)
  private Integer id;
  /**
   * 应用sku
   */
  @Column(
      length = 45,
      nullable = false)
  @Size(
      min = 1,
      max = 20)
  @Pattern(
      regexp = "[a-zA-Z0-9\\._]*")
  private String sku;
  /**
   * 应用名称
   */
  @Column(
      length = 45,
      nullable = false)
  @Size(
      min = 1,
      max = 20)
  private String name;
  /**
   * 应用标识符
   */
  @Column(
      length = 200,
      nullable = false)
  @Size(
      max = 100)
  private String bundleIdentifier;
  /**
   * 程序包文件
   */
  @Transient
  private MultipartFile packageFile;
  /**
   * 程序版本
   */
  @Column(
      length = 45,
      nullable = false)
  @Size(
      max = 20)
  private String version;
  /**
   * 最近一次信息更新的时间
   */
  @Column
  @Temporal(TemporalType.TIMESTAMP)
  private Date updateTime;
  /**
   * 应用信息创建时间
   */
  @Temporal(TemporalType.TIMESTAMP)
  private Date createTime;
  /**
   * 应用状态
   */
  private AppStatus status;
  /**
   * 应用状态国际化字串
   */
  @Transient
  private String statusDesc;
  /**
   * 应用运行平台
   */
  private AppPlatform platform;
  /**
   * 应用运行平台国际化字串
   */
  @Transient
  private String platformDesc;
  @Column(
      length = 1000)
  @Size(
      min = 0,
      max = 800)
  private String metaData;
  private boolean enableUpdateCheck; // 支持更新检查

  /**
   * 获取 id 字段的值。
   *
   * @return id 字段值
   */
  public Integer getId() {
    return id;
  }

  /**
   * 设置 id 字段的值。
   *
   * @param id id 字段的值
   */
  public void setId(Integer id) {
    this.id = id;
  }

  /**
   * 获取 sku 字段的值。
   *
   * @return sku 字段值
   */
  public String getSku() {
    return sku;
  }

  /**
   * 设置 sku 字段的值。
   *
   * @param sku sku 字段的值
   */
  public void setSku(String sku) {
    this.sku = sku;
  }

  /**
   * 获取 name 字段的值。
   *
   * @return name 字段值
   */
  public String getName() {
    return name;
  }

  /**
   * 设置 name 字段的值。
   *
   * @param name name 字段的值
   */
  public void setName(String name) {
    this.name = name;
  }

  /**
   * 获取 bundleIdentifier 字段的值。
   *
   * @return bundleIdentifier 字段值
   */
  public String getBundleIdentifier() {
    return bundleIdentifier;
  }

  /**
   * 设置 bundleIdentifier 字段的值。
   *
   * @param bundleIdentifier bundleIdentifier 字段的值
   */
  public void setBundleIdentifier(String bundleIdentifier) {
    this.bundleIdentifier = bundleIdentifier;
  }

  /**
   * 获取 packageFile 字段的值。
   *
   * @return packageFile 字段值
   */
  public MultipartFile getPackageFile() {
    return packageFile;
  }

  /**
   * 设置 packageFile 字段的值。
   *
   * @param packageFile packageFile 字段的值
   */
  public void setPackageFile(MultipartFile packageFile) {
    this.packageFile = packageFile;
  }

  /**
   * 获取 version 字段的值。
   *
   * @return version 字段值
   */
  public String getVersion() {
    return version;
  }

  /**
   * 设置 version 字段的值。
   *
   * @param version version 字段的值
   */
  public void setVersion(String version) {
    this.version = version;
  }

  /**
   * 获取 updateTime 字段的值。
   *
   * @return updateTime 字段值
   */
  @JsonSerialize(
      using = DateTimeSerializer.class)
  public Date getUpdateTime() {
    return updateTime;
  }

  /**
   * 设置 updateTime 字段的值。
   *
   * @param updateTime updateTime 字段的值
   */
  public void setUpdateTime(Date updateTime) {
    this.updateTime = updateTime;
  }

  /**
   * 获取 createTime 字段的值。
   *
   * @return createTime 字段值
   */
  @JsonSerialize(
      using = DateTimeSerializer.class)
  public Date getCreateTime() {
    return createTime;
  }

  /**
   * 设置 createTime 字段的值。
   *
   * @param createTime createTime 字段的值
   */
  public void setCreateTime(Date createTime) {
    this.createTime = createTime;
  }

  /**
   * 获取 status 字段的值。
   *
   * @return status 字段值
   */
  public AppStatus getStatus() {
    return status;
  }

  /**
   * 设置 status 字段的值。
   *
   * @param status status 字段的值
   */
  public void setStatus(AppStatus status) {
    this.status = status;
  }

  /**
   * 获取 statusDesc 字段的值。
   *
   * @return statusDesc 字段值
   */
  public String getStatusDesc() {
    return statusDesc;
  }

  /**
   * 设置 statusDesc 字段的值。
   *
   * @param statusDesc statusDesc 字段的值
   */
  public void setStatusDesc(String statusDesc) {
    this.statusDesc = statusDesc;
  }

  /**
   * 获取 platform 字段的值。
   *
   * @return platform 字段值
   */
  public AppPlatform getPlatform() {
    return platform;
  }

  /**
   * 设置 platform 字段的值。
   *
   * @param platform platform 字段的值
   */
  public void setPlatform(AppPlatform platform) {
    this.platform = platform;
  }

  /**
   * 获取 platformDesc 字段的值。
   *
   * @return platformDesc 字段值
   */
  public String getPlatformDesc() {
    return platformDesc;
  }

  /**
   * 设置 platformDesc 字段的值。
   *
   * @param platformDesc platformDesc 字段的值
   */
  public void setPlatformDesc(String platformDesc) {
    this.platformDesc = platformDesc;
  }

  /**
   * 获取 metaData 字段的值。
   *
   * @return metaData 字段值
   */
  public String getMetaData() {
    return metaData;
  }

  /**
   * 设置 metaData 字段的值。
   *
   * @param metaData metaData 字段的值
   */
  public void setMetaData(String metaData) {
    this.metaData = metaData;
  }

  /**
   * 获取 enableUpdateCheck 字段的值。
   *
   * @return enableUpdateCheck 字段值
   */
  public boolean isEnableUpdateCheck() {
    return enableUpdateCheck;
  }

  /**
   * 设置 enableUpdateCheck 字段的值。
   *
   * @param enableUpdateCheck enableUpdateCheck 字段的值
   */
  public void setEnableUpdateCheck(boolean enableUpdateCheck) {
    this.enableUpdateCheck = enableUpdateCheck;
  }

  /**
   * 获取 serialversionuid 字段的值。
   *
   * @return serialversionuid 字段值
   */
  public static long getSerialversionuid() {
    return serialVersionUID;
  }


}
