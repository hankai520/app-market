package ren.hankai.appmarket.persist;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;
import ren.hankai.cordwood.data.domain.DeleteSpecification;

import java.io.Serializable;
import java.util.List;

/**
 * JPA 实体仓库基类。
 *
 * @author hankai
 * @version 1.0.0
 * @since Nov 21, 2016 5:22:10 PM
 */
@NoRepositoryBean
public interface BaseRepository<T, I extends Serializable> extends JpaRepository<T, I> {

  /**
   * 分离实体对象，将其转换为游离状态。
   *
   * @param entity 实体对象
   * @author hankai
   * @since Nov 22, 2016 10:17:10 AM
   */
  void detach(Object entity);

  /**
   * 按条件批量删除实体。
   *
   * @param spec 删除条件
   * @return 删除的记录数
   * @author hankai
   * @since Nov 22, 2016 10:45:14 AM
   */
  int delete(DeleteSpecification<T> spec);

  /**
   * 根据查询条件超照复合要求的实体。
   *
   * @param spec 查询条件
   * @return 实体列表
   * @author hankai
   * @since Dec 7, 2016 10:19:08 AM
   */
  List<T> findAll(Specification<T> spec);

  /**
   * 根据查询和排序条件查找所有符合要求的实体。
   *
   * @param spec 查询条件
   * @param sort 排序条件
   * @return 实体列表
   * @author hankai
   * @since Aug 18, 2016 10:27:03 AM
   */
  List<T> findAll(Specification<T> spec, Sort sort);

  /**
   * 根据查询和排序条件分页查找所有符合要求的实体。
   *
   * @param spec 查询条件
   * @param pageable 分页及排序条件
   * @return 实体列表
   * @author hankai
   * @since Aug 18, 2016 10:27:49 AM
   */
  Page<T> findAll(Specification<T> spec, Pageable pageable);

  /**
   * 统计满足条件的实体数量。
   *
   * @param spec 查询条件
   * @return 实体数量
   * @author hankai
   * @since Aug 18, 2016 10:28:52 AM
   */
  long count(Specification<T> spec);

  /**
   * 查询唯一满足条件的实体。
   *
   * @param specification 查询条件
   * @return 实体实例
   * @author hankai
   * @since Aug 18, 2016 10:30:22 AM
   */
  T findOne(Specification<T> specification);

  /**
   * 查询唯一满足条件的实体。
   *
   * @param specification 查询条件
   * @param sort 排序条件
   * @return 实体实例
   * @author hankai
   * @since May 11, 2017 10:41:09 AM
   */
  T findOne(Specification<T> specification, Sort sort);

  /**
   * 查询满足条件的第一个实体。
   *
   * @param specification 查询条件
   * @return 实体实例
   * @author hankai
   * @since May 3, 2017 10:24:54 AM
   */
  T findFirst(Specification<T> specification);

}
