/*
 * Copyright © 2015 Jiangsu Sparknet Software Co., Ltd. All rights reserved
 *
 * http://www.sparksoft.com.cn
 */

package cn.com.sparksoft.persist.util;

import org.eclipse.persistence.logging.AbstractSessionLog;
import org.eclipse.persistence.logging.SessionLog;
import org.eclipse.persistence.logging.SessionLogEntry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * This is a wrapper class for SLF4J to output the eclipselink los.
 * Log levels of eclipselink are listed below:
 * <ul>
 * <li>org.eclipse.persistence.logging.default
 * <li>org.eclipse.persistence.logging.sql
 * <li>org.eclipse.persistence.logging.transaction
 * <li>org.eclipse.persistence.logging.event
 * <li>org.eclipse.persistence.logging.connection
 * <li>org.eclipse.persistence.logging.query
 * <li>org.eclipse.persistence.logging.cache
 * <li>org.eclipse.persistence.logging.propagation
 * <li>org.eclipse.persistence.logging.sequencing
 * <li>org.eclipse.persistence.logging.ejb
 * <li>org.eclipse.persistence.logging.ejb_or_metadata
 * <li>org.eclipse.persistence.logging.weaver
 * <li>org.eclipse.persistence.logging.properties
 * <li>org.eclipse.persistence.logging.server
 * </ul>
 * Log level mappings are listed below:
 * <ul>
 * <li>ALL,FINER,FINEST -> TRACE
 * <li>FINE -> DEBUG
 * <li>CONFIG,INFO -> INFO
 * <li>WARNING -> WARN
 * <li>SEVERE -> ERROR
 * </ul>
 * 
 * @author hankai
 * @version 1.0.0
 * @since Apr 22, 2015 2:38:04 PM
 */
public class Slf4jSessionLogger extends AbstractSessionLog {

    /**
     * SLF4J log levels.
     */
    enum LogLevel {
        TRACE,
        DEBUG,
        INFO,
        WARN,
        ERROR,
        OFF
    }

    public static final String        ECLIPSELINK_NAMESPACE         =
                                                                      "org.eclipse.persistence.logging";
    public static final String        DEFAULT_CATEGORY              = "default";
    public static final String        DEFAULT_ECLIPSELINK_NAMESPACE =
                                                                      ECLIPSELINK_NAMESPACE
                                                                          + "."
                                                                          + DEFAULT_CATEGORY;
    private Map<Integer, LogLevel>    mapLevels;
    private final Map<String, Logger> categoryLoggers               = new HashMap<String, Logger>();

    /**
     * Override default initializer to initialize loggers and log level mappings.
     */
    public Slf4jSessionLogger() {
        super();
        createCategoryLoggers();
        initMapLevels();
    }

    /**
     * INTERNAL: Add Logger to the categoryLoggers.
     */
    private void addLogger( String loggerCategory, String loggerNameSpace ) {
        categoryLoggers.put( loggerCategory, LoggerFactory.getLogger( loggerNameSpace ) );
    }

    /**
     * Initialize loggers eagerly.
     */
    private void createCategoryLoggers() {
        for ( String category : SessionLog.loggerCatagories ) {
            addLogger( category, ECLIPSELINK_NAMESPACE + "." + category );
        }
        // Logger default para cuando no hay categoría.
        addLogger( DEFAULT_CATEGORY, DEFAULT_ECLIPSELINK_NAMESPACE );
    }

    /**
     * INTERNAL: Return the Logger for the given category.
     */
    private Logger getLogger( String category ) {
        if ( StringUtils.isEmpty( category ) || !categoryLoggers.containsKey( category ) ) {
            category = DEFAULT_CATEGORY;
        }
        return categoryLoggers.get( category );
    }

    /**
     * Return the corresponding Slf4j Level for a given EclipseLink level.
     */
    private LogLevel getLogLevel( Integer level ) {
        LogLevel logLevel = mapLevels.get( level );
        if ( logLevel == null ) {
            logLevel = LogLevel.OFF;
        }
        return logLevel;
    }

    /**
     * Initialize the mappings between SLF4J and Eclipselink.
     */
    private void initMapLevels() {
        mapLevels = new HashMap<Integer, LogLevel>();
        mapLevels.put( SessionLog.ALL, LogLevel.TRACE );
        mapLevels.put( SessionLog.FINEST, LogLevel.TRACE );
        mapLevels.put( SessionLog.FINER, LogLevel.DEBUG );
        mapLevels.put( SessionLog.FINE, LogLevel.INFO );
        mapLevels.put( SessionLog.CONFIG, LogLevel.INFO );
        mapLevels.put( SessionLog.INFO, LogLevel.INFO );
        mapLevels.put( SessionLog.WARNING, LogLevel.WARN );
        mapLevels.put( SessionLog.SEVERE, LogLevel.ERROR );
    }

    @Override
    public void log( SessionLogEntry entry ) {
        if ( !shouldLog( entry.getLevel(), entry.getNameSpace() ) ) {
            return;
        }
        Logger logger = getLogger( entry.getNameSpace() );
        LogLevel logLevel = getLogLevel( entry.getLevel() );
        StringBuilder message = new StringBuilder();
        message.append( getSupplementDetailString( entry ) );
        message.append( formatMessage( entry ) );
        switch ( logLevel ) {
            case TRACE:
                logger.trace( message.toString() );
                break;
            case DEBUG:
                logger.debug( message.toString() );
                break;
            case INFO:
                logger.info( message.toString() );
                break;
            case WARN:
                logger.warn( message.toString() );
                break;
            case ERROR:
                logger.error( message.toString() );
                break;
            default:
                break;
        }
    }

    /**
     * Return true if SQL logging should log visible bind parameters. If the shouldDisplayData is
     * not
     * set, return false.
     */
    @Override
    public boolean shouldDisplayData() {
        if ( shouldDisplayData != null ) {
            return shouldDisplayData.booleanValue();
        } else {
            return false;
        }
    }

    @Override
    public boolean shouldLog( int level ) {
        return shouldLog( level, "default" );
    }

    @Override
    public boolean shouldLog( int level, String category ) {
        Logger logger = getLogger( category );
        boolean resp = false;
        LogLevel logLevel = getLogLevel( level );
        switch ( logLevel ) {
            case TRACE:
                resp = logger.isTraceEnabled();
                break;
            case DEBUG:
                resp = logger.isDebugEnabled();
                break;
            case INFO:
                resp = logger.isInfoEnabled();
                break;
            case WARN:
                resp = logger.isWarnEnabled();
                break;
            case ERROR:
                resp = logger.isErrorEnabled();
                break;
            default:
                break;
        }
        return resp;
    }
}
