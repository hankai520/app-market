/*
 * Copyright © 2016 Jiangsu Sparknet Software Co., Ltd. All rights reserved.
 * http://www.sparksoft.com.cn
 */

package ren.hankai.appmarket.api.payload;

import org.springframework.util.StringUtils;

import java.io.Serializable;

/**
 * 用户模型
 *
 * @author hankai
 * @version 1.0
 * @since Apr 1, 2016 11:11:49 AM
 */
public class UserViewModel implements Serializable {

  private static final long serialVersionUID = 1L;

  private String loginId;
  private String password;
  private Boolean remember;

  public String getLoginId() {
    return loginId;
  }

  public void setLoginId(final String loginId) {
    this.loginId = loginId;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(final String password) {
    this.password = password;
  }

  public Boolean getRemember() {
    return remember;
  }

  public void setRemember(final Boolean remember) {
    this.remember = remember;
  }

  /**
   * 检查模型数据是否为空
   *
   * @return 是否为空
   * @author hankai
   * @since Apr 1, 2016 11:22:13 AM
   */
  public boolean hasContents() {
    return !StringUtils.isEmpty(loginId) && !StringUtils.isEmpty(password);
  }
}
