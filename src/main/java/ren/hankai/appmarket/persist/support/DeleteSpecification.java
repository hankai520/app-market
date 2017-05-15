package ren.hankai.appmarket.persist.support;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaDelete;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

/**
 * 删除条件。
 *
 * @author hankai
 * @version 1.0.0
 * @since Nov 22, 2016 10:43:09 AM
 */
public interface DeleteSpecification<T> {

  Predicate toPredicate(Root<T> root, CriteriaDelete<?> query, CriteriaBuilder cb);

}
