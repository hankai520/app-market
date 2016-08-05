/*
 * Copyright © 2015 Jiangsu Sparknet Software Co., Ltd. All Rights Reserved
 *
 *    http://www.sparksoft.com.cn
 */

package ren.hankai.persist.model;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

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

import ren.hankai.util.DateTimeSerializer;

/**
 * 用户（后台运维或客户端用户）
 *
 * @author hankai
 * @version 1.0
 * @since Jul 16, 2015 2:00:29 PM
 */
@Entity
@Table(
    name = "users" )
@Cacheable( false )
public class User implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(
        strategy = GenerationType.IDENTITY )
    private Integer           id;
    /**
     * 手机号
     */
    @Column(
        length = 45,
        nullable = false )
    @Size(
        min = 1,
        max = 20 )
    @Pattern(
        regexp = "\\d*" )
    private String            mobile;
    @Column(
        length = 45 )
    @Size(
        min = 0,
        max = 20 )
    private String            name;
    /**
     * 登录密码
     */
    @Column(
        length = 100,
        nullable = false )
    @Size(
        min = 1,
        max = 60 )
    private String            password;
    /**
     * 最近一次信息更新的时间
     */
    @Column( )
    @Temporal( TemporalType.DATE )
    private Date              updateTime;
    /**
     * 创建时间
     */
    @Column(
        nullable = false )
    @Temporal( TemporalType.DATE )
    private Date              createTime;
    /**
     * 账号状态
     */
    private UserStatus        status;
    @Transient
    private String            statusName;
    /**
     * 用户角色
     */
    private UserRole          role;
    @Transient
    private String            roleName;

    public Integer getId() {
        return id;
    }

    public void setId( Integer id ) {
        this.id = id;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile( String mobile ) {
        this.mobile = mobile;
    }

    public String getName() {
        return name;
    }

    public void setName( String name ) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword( String password ) {
        this.password = password;
    }

    @JsonSerialize(
        using = DateTimeSerializer.class )
    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime( Date updateTime ) {
        this.updateTime = updateTime;
    }

    @JsonSerialize(
        using = DateTimeSerializer.class )
    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime( Date createTime ) {
        this.createTime = createTime;
    }

    public UserStatus getStatus() {
        return status;
    }

    public void setStatus( UserStatus status ) {
        this.status = status;
    }

    public String getStatusName() {
        return statusName;
    }

    public void setStatusName( String statusName ) {
        this.statusName = statusName;
    }

    public UserRole getRole() {
        return role;
    }

    public void setRole( UserRole role ) {
        this.role = role;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName( String roleName ) {
        this.roleName = roleName;
    }

    public static long getSerialversionuid() {
        return serialVersionUID;
    }
}
