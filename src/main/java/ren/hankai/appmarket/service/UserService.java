
package ren.hankai.appmarket.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ren.hankai.appmarket.persist.UserRepository;
import ren.hankai.appmarket.persist.UserRepository.UserSpecs;
import ren.hankai.appmarket.persist.model.UserBean;
import ren.hankai.appmarket.persist.model.UserRole;
import ren.hankai.appmarket.persist.support.EntitySpecs;

/**
 * 用户业务逻辑。
 *
 * @author hankai
 * @version 1.0.0
 * @since May 15, 2017 2:16:55 PM
 */
@Service
public class UserService {

  @Autowired
  private UserRepository userRepo;

  /**
   * 根据ID查询用户。
   *
   * @param id 用户ID
   * @return 用户信息
   * @author hankai
   * @since May 15, 2017 2:46:24 PM
   */
  public UserBean getUserById(Integer id) {
    return userRepo.findOne(id);
  }

  /**
   * 根据手机号查询用户。
   *
   * @param mobile 手机号
   * @return 用户信息
   * @author hankai
   * @since May 15, 2017 2:46:22 PM
   */
  public UserBean getUserByMobile(String mobile) {
    return userRepo.findFirst(EntitySpecs.field("mobile", mobile));
  }

  /**
   * 按关键字搜索用户。
   *
   * @param role 用户角色
   * @param keyword 关键字
   * @param pageable 分页
   * @return 用户信息列表
   * @author hankai
   * @since May 15, 2017 2:50:25 PM
   */
  public Page<UserBean> searchUsers(UserRole role, String keyword, Pageable pageable) {
    return userRepo.findAll(UserSpecs.byKeyword(keyword, role), pageable);
  }

  /**
   * 保存用户信息。
   *
   * @param user 用户信息
   * @return 保存后的用户信息
   * @author hankai
   * @since May 15, 2017 2:54:19 PM
   */
  @Transactional
  public UserBean saveUser(UserBean user) {
    return userRepo.save(user);
  }

  /**
   * 根据用户ID删除用户。
   *
   * @param id 用户ID
   * @author hankai
   * @since May 15, 2017 2:55:44 PM
   */
  @Transactional
  public void deleteUserById(Integer id) {
    userRepo.delete(id);
  }
}
