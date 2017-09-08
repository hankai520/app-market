
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
   * 根据ID查询用户组。
   *
   * @param id 用户组ID
   * @return 用户组
   * @author hankai
   * @since May 15, 2017 2:46:24 PM
   */
  public UserGroupBean getGroupById(Integer id) {
    return userGroupRepo.findOne(id);
  }

  /**
   * 根据用户组名称查询用户组。
   *
   * @param name 用户组名称
   * @return 用户组
   * @author hankai
   * @since May 15, 2017 2:46:22 PM
   */
  public UserGroupBean getGroupByName(String name) {
    return userGroupRepo.findFirst(EntitySpecs.field("name", name));
  }

  /**
   * 根据关键字搜索用户组。
   *
   * @param keyword 关键字
   * @param pageable 分页
   * @return 搜索结果
   * @author hankai
   * @since Sep 8, 2017 1:00:05 PM
   */
  public Page<UserGroupBean> searchGroups(String keyword, Pageable pageable) {
    return userGroupRepo.findAll(UserGroupSpecs.byKeyword(keyword), pageable);
  }

  /**
   * 保存用户组。
   *
   * @param group 用户组信息
   * @return 保存后的用户组
   * @author hankai
   * @since May 15, 2017 2:54:19 PM
   */
  @Transactional
  public UserGroupBean saveGroup(UserGroupBean group) {
    return userGroupRepo.save(group);
  }

  /**
   * 根据ID删除用户组。
   *
   * @param id 用户组ID
   * @author hankai
   * @since May 15, 2017 2:55:44 PM
   */
  @Transactional
  public void deleteGroupById(Integer id) {
    userGroupRepo.delete(id);
  }

  /**
   * 获取可用的用户组。
   *
   * @return 用户组列表
   * @author hankai
   * @since Aug 31, 2017 4:12:59 PM
   */
  public List<UserGroupBean> getAvailableGroups() {
    return userGroupRepo.findAll(UserGroupSpecs.enabled());
  }
}
