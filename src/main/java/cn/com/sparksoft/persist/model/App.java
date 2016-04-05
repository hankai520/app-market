/*
 * Copyright © 2015 Jiangsu Sparknet Software Co., Ltd. All Rights Reserved
 *
 *    http://www.sparksoft.com.cn
 */

package cn.com.sparksoft.persist.model;

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
    name = "apps" )
@Cacheable( false )
public class App implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(
        strategy = GenerationType.IDENTITY )
    private Integer           id;
    /**
     * 应用名称
     */
    @Column(
        length = 45,
        nullable = false )
    @Size(
        min = 1,
        max = 20 )
    private String            name;
    /**
     * 应用标识符
     */
    @Column(
        length = 200,
        nullable = false )
    @Size(
        min = 1,
        max = 100 )
    private String            bundleIdentifier;
    /**
     * 应用图标 URL（小图片）
     */
    @Column(
        length = 1000,
        nullable = false )
    @Size(
        min = 1,
        max = 400 )
    private String            smallImageUrl;
    /**
     * 应用图标 URL（大图片）
     */
    @Column(
        length = 1000,
        nullable = false )
    @Size(
        min = 1,
        max = 400 )
    private String            largeImageUrl;
    /**
     * 程序安装包 URL（ios: .ipa文件地址，android：.apk文件地址）
     */
    @Column(
        length = 1000,
        nullable = false )
    @Size(
        min = 1,
        max = 400 )
    private String            packageUrl;
    /**
     * 程序版本
     */
    @Column(
        length = 45,
        nullable = false )
    @Size(
        min = 1,
        max = 10 )
    private String            version;
    /**
     * 最近一次信息更新的时间
     */
    @Column
    @Temporal( TemporalType.DATE )
    private Date              updateTime;
    /**
     * 应用信息创建时间
     */
    @Temporal( TemporalType.DATE )
    private Date              createTime;
    /**
     * 应用状态
     */
    private AppStatus         status;
    /**
     * 应用状态国际化字串
     */
    @Transient
    private String            statusDesc;
    /**
     * 应用运行平台
     */
    private AppPlatform       platform;
    /**
     * 应用运行平台国际化字串
     */
    @Transient
    private String            platformDesc;

    public Integer getId() {
        return id;
    }

    public void setId( Integer id ) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName( String name ) {
        this.name = name;
    }

    public String getBundleIdentifier() {
        return bundleIdentifier;
    }

    public void setBundleIdentifier( String bundleIdentifier ) {
        this.bundleIdentifier = bundleIdentifier;
    }

    public String getSmallImageUrl() {
        return smallImageUrl;
    }

    public void setSmallImageUrl( String smallImageUrl ) {
        this.smallImageUrl = smallImageUrl;
    }

    public String getLargeImageUrl() {
        return largeImageUrl;
    }

    public void setLargeImageUrl( String largeImageUrl ) {
        this.largeImageUrl = largeImageUrl;
    }

    public String getPackageUrl() {
        return packageUrl;
    }

    public void setPackageUrl( String packageUrl ) {
        this.packageUrl = packageUrl;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion( String version ) {
        this.version = version;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime( Date updateTime ) {
        this.updateTime = updateTime;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime( Date createTime ) {
        this.createTime = createTime;
    }

    public AppStatus getStatus() {
        return status;
    }

    public void setStatus( AppStatus status ) {
        this.status = status;
    }

    public String getStatusDesc() {
        return statusDesc;
    }

    public void setStatusDesc( String statusDesc ) {
        this.statusDesc = statusDesc;
    }

    public AppPlatform getPlatform() {
        return platform;
    }

    public void setPlatform( AppPlatform platform ) {
        this.platform = platform;
    }

    public String getPlatformDesc() {
        return platformDesc;
    }

    public void setPlatformDesc( String platformDesc ) {
        this.platformDesc = platformDesc;
    }

    public static long getSerialversionuid() {
        return serialVersionUID;
    }
}
