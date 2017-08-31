
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

  public class UserGroupSpecs {

    /**
     * TODO Missing method description。
     *
     * @param name
     * @return
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
