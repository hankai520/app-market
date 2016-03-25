/*
 * Copyright © 2015 Jiangsu Sparknet Software Co., Ltd. All rights reserved
 *
 * http://www.sparksoft.com.cn
 */

package cn.com.sparksoft.persist;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;

import cn.com.sparksoft.persist.util.JpaServiceUtil;
import cn.com.sparksoft.persist.util.PaginatedResult;

/**
 * 提供基础CRUD操作的JPA基类。
 *
 * @author hankai
 * @version 1.0
 * @since Jul 14, 2015 12:38:04 PM
 */
public abstract class JpaBasedService<T> {

    private static final Logger logger = LoggerFactory.getLogger( JpaBasedService.class );
    /*
     * The actual entity class subclass is operating.
     */
    private final Class<T>      entityClass;
    @Autowired
    protected JpaServiceUtil    jpaServiceUtil;

    protected JpaBasedService( Class<T> clazz ) {
        this.entityClass = clazz;
    }

    public long count() {
        return jpaServiceUtil.countEntity( entityClass );
    }

    
    @Transactional
    public void delete( T entity ) {
        jpaServiceUtil.getEntityManager().remove( entity );
    }

    @Transactional
    public void deleteAll() {
        int changed = jpaServiceUtil.deleteAll( entityClass );
        logger.debug( String.format( "%d %s rows deleted.", changed, entityClass ) );
    }

    /**
     * Delete entity by using its unique id ( usually is primary key ).
     */
    @Transactional
    public void deleteById( Object id ) {
        T entity = find( id );
        if ( entity == null ) {
            throw new EntityNotFoundException();
        }
        delete( entity );
    }

    public void detach( T entity ) {
        jpaServiceUtil.getEntityManager().detach( entity );
    }

    public T find( Object primaryKey ) {
        return jpaServiceUtil.getEntityManager().find( entityClass, primaryKey );
    }

    public PaginatedResult<T> findAll( int offset, int count ) {
        return jpaServiceUtil.findAll( entityClass, offset, count );
    }

    public void refresh( T entity ) {
        jpaServiceUtil.getEntityManager().refresh( entity );
    }

    @Transactional
    public void save( T entity ) {
        jpaServiceUtil.getEntityManager().persist( entity );
    }

    @Transactional
    public void update( T entity ) {
        jpaServiceUtil.getEntityManager().merge( entity );
    }
}
