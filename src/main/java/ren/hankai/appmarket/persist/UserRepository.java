
package ren.hankai.appmarket.persist;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.jpa.domain.Specification;
import ren.hankai.appmarket.persist.model.UserBean;
import ren.hankai.appmarket.persist.model.UserRole;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

/**
 * 用户仓库。
 *
 * @author hankai
 * @version 1.0.0
 * @since May 15, 2017 2:12:41 PM
 */
public interface UserRepository extends BaseRepository<UserBean, Integer> {

  public class UserSpecs {

    /**
     * 按关键字查询用户。
     *
     * @param keyword 关键字
     * @param role 用户角色
     * @return 查询条件
     * @author hankai
     * @since May 15, 2017 2:48:34 PM
     */
    public static Specification<UserBean> byKeyword(String keyword, UserRole role) {
      return new Specification<UserBean>() {

        @Override
        public Predicate toPredicate(Root<UserBean> root, CriteriaQuery<?> query,
            CriteriaBuilder cb) {
          Predicate pre = null;
          if (role != null) {
            pre = cb.equal(root.get("role"), role);
          }
          if (!StringUtils.isEmpty(keyword)) {
            final String fuzzyKeyword = "%" + keyword + "%";
            final Predicate p = cb.or(
                cb.like(root.<String>get("mobile"), fuzzyKeyword),
                cb.like(root.<String>get("name"), fuzzyKeyword));
            pre = (pre == null) ? p : cb.and(pre, p);
          }
          return pre;
        }

      };
    }
  }

}