package ren.hankai.appmarket.persist.support;

import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import ren.hankai.appmarket.persist.BaseRepository;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaDelete;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

/**
 * JPA 实体仓库基类实现。
 *
 * @author hankai
 * @version 1.0.0
 * @since Nov 21, 2016 5:21:28 PM
 */
public class BaseRepositoryImpl<T, I extends Serializable> extends SimpleJpaRepository<T, I>
    implements BaseRepository<T, I> {

  protected final EntityManager entityManager;
  protected final Class<T> domainClass;

  /**
   * 构造实体仓库基类。
   *
   * @param domainClass 实体类的类型
   * @param entityManager JPA 实体管理器
   */
  public BaseRepositoryImpl(Class<T> domainClass, EntityManager entityManager) {
    super(domainClass, entityManager);
    this.entityManager = entityManager;
    this.domainClass = domainClass;
  }

  /**
   * 实体仓库基类初始化。
   *
   * @param entityInformation 实体信息
   * @param entityManager 实体管理器（JPA核心对象）
   */
  public BaseRepositoryImpl(JpaEntityInformation<T, ?> entityInformation,
      EntityManager entityManager) {
    super(entityInformation, entityManager);
    this.entityManager = entityManager;
    this.domainClass = entityInformation.getJavaType();
  }

  @Override
  public void detach(Object entity) {
    entityManager.detach(entity);
  }

  @Override
  public int delete(DeleteSpecification<T> spec) {
    final CriteriaBuilder cb = entityManager.getCriteriaBuilder();
    final CriteriaDelete<T> cd = cb.createCriteriaDelete(domainClass);
    final Root<T> root = cd.from(domainClass);
    cd.where(spec.toPredicate(root, cd, cb));
    return entityManager.createQuery(cd).executeUpdate();
  }

  @Override
  public T findFirst(Specification<T> specification) {
    return findOne(specification, null);
  }

  @Override
  public T findOne(Specification<T> specification, Sort sort) {
    final CriteriaBuilder cb = entityManager.getCriteriaBuilder();
    final CriteriaQuery<T> cq = cb.createQuery(domainClass);
    final Root<T> root = cq.from(domainClass);
    cq.where(specification.toPredicate(root, cq, cb));
    if (sort != null) {
      final List<javax.persistence.criteria.Order> orders = new ArrayList<>();
      final Iterator<Order> it = sort.iterator();
      while (it.hasNext()) {
        final Order order = it.next();
        if (order.isAscending()) {
          orders.add(cb.asc(root.get(order.getProperty())));
        } else {
          orders.add(cb.desc(root.get(order.getProperty())));
        }
      }
      if (orders.size() > 0) {
        cq.orderBy(orders);
      }
    }
    final List<T> results = entityManager.createQuery(cq).setMaxResults(1).getResultList();
    if ((results != null) && (results.size() > 0)) {
      return results.get(0);
    }
    return null;
  }
}
