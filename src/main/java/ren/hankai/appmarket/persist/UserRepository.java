
package ren.hankai.appmarket.persist;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.jpa.domain.Specification;
import ren.hankai.appmarket.persist.model.User;
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
public interface UserRepository extends BaseRepository<User, Integer> {

  public class UserSpecs {

    /**
     * TODO Missing method description。
     *
     * @param keyword
     * @param role
     * @return
     * @author hankai
     * @since May 15, 2017 2:48:34 PM
     */
    public static Specification<User> byKeyword(String keyword, UserRole role) {
      return new Specification<User>() {

        @Override
        public Predicate toPredicate(Root<User> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
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
