
package ren.hankai.appmarket.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ren.hankai.appmarket.persist.UserGroupRepository;
import ren.hankai.appmarket.persist.UserGroupRepository.UserGroupSpecs;
import ren.hankai.appmarket.persist.model.UserGroupBean;
import ren.hankai.appmarket.persist.support.EntitySpecs;

import java.util.List;

/**
 * 用户组业务逻辑。
 *
 * @author hankai
 * @version 1.0.0
 * @since May 15, 2017 2:16:55 PM
 */
@Service
public class GroupService {

  @Autowired
  private UserGroupRepository userGroupRepo;

  /**
   * TODO Missing method description。
   *
   * @param id
   * @return
   * @author hankai
   * @since May 15, 2017 2:46:24 PM
   */
  public UserGroupBean getGroupById(Integer id) {
    return userGroupRepo.findOne(id);
  }

  /**
   * TODO Missing method description。
   *
   * @param mobile
   * @return
   * @author hankai
   * @since May 15, 2017 2:46:22 PM
   */
  public UserGroupBean getGroupByName(String name) {
    return userGroupRepo.findFirst(EntitySpecs.field("name", name));
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
  public Page<UserGroupBean> searchGroups(String keyword, Pageable pageable) {
    return userGroupRepo.findAll(UserGroupSpecs.byKeyword(keyword), pageable);
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
  public UserGroupBean saveGroup(UserGroupBean group) {
    return userGroupRepo.save(group);
  }

  /**
   * TODO Missing method description。
   *
   * @param id
   * @author hankai
   * @since May 15, 2017 2:55:44 PM
   */
  @Transactional
  public void deleteGroupById(Integer id) {
    userGroupRepo.delete(id);
  }

  /**
   * TODO Missing method description。
   *
   * @return
   * @author hankai
   * @since Aug 31, 2017 4:12:59 PM
   */
  public List<UserGroupBean> getAvailableGroups() {
    return userGroupRepo.findAll(UserGroupSpecs.enabled());
  }
}
