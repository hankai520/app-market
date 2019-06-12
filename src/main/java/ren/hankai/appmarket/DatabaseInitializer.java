
package ren.hankai.appmarket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ren.hankai.appmarket.persist.UserGroupRepository;
import ren.hankai.appmarket.persist.UserRepository;
import ren.hankai.appmarket.persist.model.UserBean;
import ren.hankai.appmarket.persist.model.UserGroupBean;
import ren.hankai.appmarket.persist.model.UserRole;
import ren.hankai.appmarket.persist.model.UserStatus;
import ren.hankai.cordwood.data.domain.EntitySpecs;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.transaction.Transactional;

/**
 * 数据库初始化器，写入必要的初始数据。
 *
 * @author hankai
 * @version 1.0.0
 * @since Oct 9, 2016 11:31:14 AM
 */
@Component
public class DatabaseInitializer {

  private static final Logger logger = LoggerFactory.getLogger(DatabaseInitializer.class);

  @Autowired
  private UserRepository userRepo;
  @Autowired
  private UserGroupRepository userGroupRepo;

  /**
   * 初始化用户账号。
   *
   * @author hankai
   * @since May 15, 2017 5:51:11 PM
   */
  private void initUsers() {
    UserGroupBean group =
        userGroupRepo.findOne(EntitySpecs.field("name", "江苏星网软件有限公司"));
    if (group == null) {
      group = new UserGroupBean();
      group.setName("江苏星网软件有限公司");
      group.setEnabled(true);
    }
    group = userGroupRepo.save(group);

    final List<UserBean> users = new ArrayList<>();

    UserBean user = new UserBean();
    user.setMobile("666");
    user.setName("管理员");
    user.setPassword("e10adc3949ba59abbe56e057f20f883e");// 123456
    user.setCreateTime(new Date());
    user.setRole(UserRole.Operator);
    user.setStatus(UserStatus.Enabled);
    user.setGroup(group);
    UserBean existUser = userRepo.findOne(EntitySpecs.field("mobile", user.getMobile()));
    if (existUser == null) {
      users.add(user);
    }

    user = new UserBean();
    user.setMobile("888");
    user.setName("手机用户");
    user.setPassword("e10adc3949ba59abbe56e057f20f883e");// 123456
    user.setCreateTime(new Date());
    user.setRole(UserRole.MobileUser);
    user.setStatus(UserStatus.Enabled);
    user.setGroup(group);
    existUser = userRepo.findOne(EntitySpecs.field("mobile", user.getMobile()));
    if (existUser == null) {
      users.add(user);
    }

    if (users.size() > 0) {
      userRepo.save(users);
    }
  }

  @PostConstruct
  @Transactional
  private void initData() throws Exception {
    try {
      // 创建默认帐号，避免帐号意外丢失造成无法登录。
      initUsers();
    } catch (final Exception ex) {
      logger.error("Failed to set up initial data for database.", ex);
    }
  }
}
