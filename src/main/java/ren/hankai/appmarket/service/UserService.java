
package ren.hankai.appmarket.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ren.hankai.appmarket.persist.UserRepository;
import ren.hankai.appmarket.persist.UserRepository.UserSpecs;
import ren.hankai.appmarket.persist.model.User;
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
   * TODO Missing method description。
   *
   * @param id
   * @return
   * @author hankai
   * @since May 15, 2017 2:46:24 PM
   */
  public User getUserById(Integer id) {
    return userRepo.findOne(id);
  }

  /**
   * TODO Missing method description。
   *
   * @param mobile
   * @return
   * @author hankai
   * @since May 15, 2017 2:46:22 PM
   */
  public User getUserByMobile(String mobile) {
    return userRepo.findFirst(EntitySpecs.field("mobile", mobile));
  }

  /**
   * TODO Missing method description。
   *
   * @param role
   * @param keyword
   * @param pageable
   * @return
   * @author hankai
   * @since May 15, 2017 2:50:25 PM
   */
  public Page<User> searchUsers(UserRole role, String keyword, Pageable pageable) {
    return userRepo.findAll(UserSpecs.byKeyword(keyword, role), pageable);
  }

  /**
   * TODO Missing method description。
   *
   * @param user
   * @return
   * @author hankai
   * @since May 15, 2017 2:54:19 PM
   */
  @Transactional
  public User saveUser(User user) {
    return userRepo.save(user);
  }

  /**
   * TODO Missing method description。
   *
   * @param id
   * @author hankai
   * @since May 15, 2017 2:55:44 PM
   */
  @Transactional
  public void deleteUserById(Integer id) {
    userRepo.delete(id);
  }
}
