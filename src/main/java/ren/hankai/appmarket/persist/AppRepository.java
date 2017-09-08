/*
 * Copyright © 2015 Jiangsu Sparknet Software Co., Ltd. All Rights Reserved
 *
 * http://www.sparksoft.com.cn
 */

package ren.hankai.appmarket.persist;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.transaction.annotation.Transactional;
import ren.hankai.appmarket.persist.model.AppBean;
import ren.hankai.appmarket.persist.model.AppStatus;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

/**
 * 应用仓库。
 *
 * @author hankai
 * @version 1.0
 * @since Jul 17, 2015 9:21:02 AM
 */
@Transactional
public interface AppRepository extends BaseRepository<AppBean, Integer> {

  /**
   * 应用仓库查询条件。
   *
   * @author hankai
   * @version 1.0.0
   * @since May 15, 2017 2:41:56 PM
   */
  public class AppSpecs {

    /**
     * 按关键字搜索应用。
     *
     * @param keyword 关键字
     * @return 查询条件
     * @author hankai
     * @since May 15, 2017 2:41:52 PM
     */
    public static Specification<AppBean> byKeyword(String keyword) {
      return new Specification<AppBean>() {

        @Override
        public Predicate toPredicate(Root<AppBean> root, CriteriaQuery<?> query,
            CriteriaBuilder cb) {
          final String fuzzyKeyword = "%" + keyword + "%";
          Predicate pre = null;
          if (!StringUtils.isEmpty(keyword)) {
            pre = cb.or(
                cb.like(root.<String>get("name"), fuzzyKeyword),
                cb.like(root.<String>get("bundleIdentifier"), fuzzyKeyword));
          }
          return pre;
        }

      };
    }

    /**
     * 查询已上架的应用。
     *
     * @return 查询条件
     * @author hankai
     * @since May 15, 2017 2:41:50 PM
     */
    public static final Specification<AppBean> readyToSaleApps() {
      return new Specification<AppBean>() {

        @Override
        public Predicate toPredicate(Root<AppBean> root, CriteriaQuery<?> query,
            CriteriaBuilder cb) {
          return cb.equal(root.get("status"), AppStatus.ReadyToSale);
        }

      };
    }
  }

}
