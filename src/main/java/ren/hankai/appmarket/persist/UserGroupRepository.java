
package ren.hankai.appmarket.persist;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.jpa.domain.Specification;
import ren.hankai.appmarket.persist.model.UserGroupBean;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

/**
 * 用户组仓库。
 *
 * @author hankai
 * @version 1.0.0
 * @since May 15, 2017 2:12:41 PM
 */
public interface UserGroupRepository extends BaseRepository<UserGroupBean, Integer> {

  /**
   * 用户仓库查询条件。
   *
   * @author hankai
   * @version 1.0.0
   * @since Sep 8, 2017 12:53:13 PM
   */
  public class UserGroupSpecs {

    /**
     * 根据名称查询用户组。
     *
     * @param name 名称
     * @return 查询条件
     * @author hankai
     * @since Aug 31, 2017 2:41:11 PM
     */
    public static Specification<UserGroupBean> byName(String name) {
      return new Specification<UserGroupBean>() {

        @Override
        public Predicate toPredicate(Root<UserGroupBean> root, CriteriaQuery<?> query,
            CriteriaBuilder cb) {
          return cb.equal(root.get("name"), name);
        }

      };
    }

    /**
     * 用关键字查询用户组。
     *
     * @param keyword 关键字
     * @return 查询条件
     * @author hankai
     * @since Sep 8, 2017 12:53:25 PM
     */
    public static Specification<UserGroupBean> byKeyword(String keyword) {
      return new Specification<UserGroupBean>() {

        @Override
        public Predicate toPredicate(Root<UserGroupBean> root, CriteriaQuery<?> query,
            CriteriaBuilder cb) {
          if (StringUtils.isNotEmpty(keyword)) {
            return cb.like(root.get("name"), "%" + keyword + "%");
          }
          return null;
        }

      };
    }

    /**
     * 查询已启用的用户组。
     *
     * @return 查询条件
     * @author hankai
     * @since Sep 8, 2017 12:53:40 PM
     */
    public static Specification<UserGroupBean> enabled() {
      return new Specification<UserGroupBean>() {

        @Override
        public Predicate toPredicate(Root<UserGroupBean> root, CriteriaQuery<?> query,
            CriteriaBuilder cb) {
          return cb.equal(root.get("enabled"), true);
        }

      };
    }
  }

}
