/*
 * Copyright © 2016 hankai.ren, All rights reserved.
 *
 * http://www.hankai.ren
 */

package ren.hankai.appmarket.persist.support;

import org.springframework.data.jpa.domain.Specification;

import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

/**
 * JPA 实体通用查询条件。
 *
 * @author hankai
 * @version 1.0
 * @since Oct 9, 2016 3:18:12 PM
 */
public abstract class EntitySpecs {

  /**
   * 单字段查询条件。
   *
   * @param fieldName 字段名称
   * @param value 字段值
   * @param <T> 实体类型
   * @return 查询条件
   * @author hankai
   * @since Aug 18, 2016 10:45:18 AM
   */
  public static <T> Specification<T> field(final String fieldName, final Object value) {
    return new Specification<T>() {

      @Override
      public Predicate toPredicate(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
        return cb.equal(root.get(fieldName), value);
      }
    };
  }

  /**
   * 字段值在给定范围内的查询条件。
   *
   * @param fieldName 字段
   * @param values 取值范围
   * @param <T> 实体类型
   * @return 查询条件
   * @author hankai
   * @since Oct 13, 2016 2:58:59 PM
   */
  public static <T> Specification<T> fieldIn(final String fieldName, final List<?> values) {
    return new Specification<T>() {

      @Override
      public Predicate toPredicate(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
        return root.get(fieldName).in(values);
      }
    };
  }

  /**
   * 查询字段的值不在指定范围内的实体。
   * 
   * @param fieldName 字段
   * @param values 取值范围
   * @param <T> 实体类型
   * @return 查询条件
   * @author hankai
   * @since Oct 14, 2016 1:07:16 PM
   */
  public static <T> Specification<T> fieldNotIn(final String fieldName, final List<?> values) {
    return new Specification<T>() {

      @Override
      public Predicate toPredicate(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
        return cb.not(root.get(fieldName).in(values));
      }
    };
  }
}
