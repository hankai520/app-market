
package ren.hankai.appmarket.persist.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Cacheable;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * 用户分组。
 *
 * @author hankai
 * @version 1.0.0
 * @since Aug 31, 2017 2:21:25 PM
 */
@Entity
@Table(
    name = "user_groups")
@Cacheable(false)
public class UserGroupBean implements Serializable {

  private static final long serialVersionUID = 1L;

  @Id
  @GeneratedValue(
      strategy = GenerationType.IDENTITY)
  private Integer id;

  @ManyToMany(fetch = FetchType.LAZY)
  @JoinTable(name = "group_app_accesses",
      joinColumns = @JoinColumn(name = "groupId", referencedColumnName = "id"),
      inverseJoinColumns = @JoinColumn(name = "appId", referencedColumnName = "id"))
  @JsonIgnore
  private List<AppBean> apps;

  @OneToMany(fetch = FetchType.LAZY, mappedBy = "group")
  @JsonIgnore
  private List<UserBean> users;

  @Size(min = 1, max = 50)
  @NotNull
  private String name;

  private boolean enabled;

  @Transient
  private Integer numberOfUsers;

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
   * 获取 apps 字段的值。
   *
   * @return apps 字段值
   */
  public List<AppBean> getApps() {
    return apps;
  }

  /**
   * 设置 apps 字段的值。
   *
   * @param apps apps 字段的值
   */
  public void setApps(List<AppBean> apps) {
    this.apps = apps;
  }

  /**
   * 获取 users 字段的值。
   *
   * @return users 字段值
   */
  public List<UserBean> getUsers() {
    return users;
  }

  /**
   * 设置 users 字段的值。
   *
   * @param users users 字段的值
   */
  public void setUsers(List<UserBean> users) {
    this.users = users;
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
   * 获取 enabled 字段的值。
   *
   * @return enabled 字段值
   */
  public boolean isEnabled() {
    return enabled;
  }

  /**
   * 设置 enabled 字段的值。
   *
   * @param enabled enabled 字段的值
   */
  public void setEnabled(boolean enabled) {
    this.enabled = enabled;
  }

  /**
   * 获取 numberOfUsers 字段的值。
   *
   * @return numberOfUsers 字段值
   */
  public Integer getNumberOfUsers() {
    if (users != null) {
      return users.size();
    }
    return 0;
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
