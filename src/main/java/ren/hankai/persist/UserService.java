/*
 * Copyright © 2015 Jiangsu Sparknet Software Co., Ltd. All Rights Reserved
 *
 *    http://www.sparksoft.com.cn
 */

package ren.hankai.persist;

import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import ren.hankai.persist.model.User;
import ren.hankai.persist.model.UserRole;
import ren.hankai.persist.util.CriteriaQueryBuilder;
import ren.hankai.persist.util.JPABasedDAO;
import ren.hankai.persist.util.PaginatedResult;

/**
 * 用户业务
 *
 * @author hankai
 * @version 1.0
 * @since Jul 17, 2015 9:21:02 AM
 */
@Service
public class UserService extends JPABasedDAO<User> {

    public PaginatedResult<User> search( final UserRole role, final String keyword,
                    final String sort,
                    final boolean asc, int offset, int limit ) {
        return jpaServiceUtil.findAll( User.class, new CriteriaQueryBuilder() {

            @Override
            public List<Order> getOrderBys( CriteriaBuilder cb, Root<?> root ) {
                List<Order> orders = new ArrayList<>();
                if ( !StringUtils.isEmpty( sort ) ) {
                    if ( asc ) {
                        orders.add( cb.asc( root.get( sort ) ) );
                    } else {
                        orders.add( cb.desc( root.get( sort ) ) );
                    }
                }
                return orders;
            }

            @Override
            public Predicate buildPredicate( CriteriaBuilder cb, Root<?> root ) {
                Predicate pre = null;
                if ( role != null ) {
                    pre = cb.equal( root.get( "role" ), role );
                }
                if ( !StringUtils.isEmpty( keyword ) ) {
                    String fuzzyKeyword = "%" + keyword + "%";
                    Predicate p = cb.or(
                        cb.like( root.<String>get( "mobile" ), fuzzyKeyword ),
                        cb.like( root.<String>get( "name" ), fuzzyKeyword ) );
                    pre = ( pre == null ) ? p : cb.and( pre, p );
                }
                return pre;
            }
        }, offset, limit );
    }
}
