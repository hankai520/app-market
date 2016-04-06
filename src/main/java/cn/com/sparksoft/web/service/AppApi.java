/*
 * Copyright © 2016 Jiangsu Sparknet Software Co., Ltd. All rights reserved.
 *
 * http://www.sparksoft.com.cn
 */

package cn.com.sparksoft.web.service;

import org.apache.velocity.app.VelocityEngine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.velocity.VelocityEngineUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import cn.com.sparksoft.config.Route;
import cn.com.sparksoft.persist.AppService;
import cn.com.sparksoft.persist.model.App;

/**
 * 应用信息 API
 *
 * @author hankai
 * @version 1.0
 * @since Mar 15, 2016 2:17:24 PM
 */
@Controller
public class AppApi {

    private static final Logger logger = LoggerFactory.getLogger( AppApi.class );
    @Autowired
    private AppService          appService;
    @Autowired
    private VelocityEngine      engine;

    @RequestMapping(
        value = { Route.API_APP_IOS_PACKAGE, Route.API_APP_ANDROID_PACKAGE },
        produces = { MediaType.APPLICATION_OCTET_STREAM_VALUE } )
    public ResponseEntity<FileSystemResource> appPackage( @PathVariable( "appId" ) Integer appId ) {
        App app = appService.find( appId );
        File file = null;
        if ( app != null ) {
            String path = appService.getAppPackagePath( appId, app.getPlatform() );
            File f = new File( path );
            if ( f.exists() ) {
                file = f;
            }
        }
        if ( file != null ) {
            return new ResponseEntity<FileSystemResource>( new FileSystemResource( file ),
                HttpStatus.OK );
        } else {
            return new ResponseEntity<>( HttpStatus.NOT_FOUND );
        }
    }

    @RequestMapping(
        value = Route.API_IOS_MANIFEST,
        produces = { MediaType.APPLICATION_XML_VALUE } )
    public ResponseEntity<String> generateIosManifest(
                    @PathVariable( "appId" ) Integer appId,
                    HttpServletRequest request ) {
        App app = appService.find( appId );
        if ( app != null ) {
            String packageUrl = request.getScheme() + "://"
                + request.getServerName() + ":" + request.getServerPort()
                + request.getContextPath();
            packageUrl += Route.API_APP_IOS_PACKAGE.replaceAll( "\\{appId\\}", appId + "" );
            Map<String, Object> model = new HashMap<>();
            model.put( "packageUrl", packageUrl );
            model.put( "smallImageUrl", app.getSmallImageUrl() );
            model.put( "largeImageUrl", app.getLargeImageUrl() );
            model.put( "bundleIdentifier", app.getBundleIdentifier() );
            model.put( "bundleVersion", app.getVersion() );
            model.put( "appName", app.getName() );
            String manifest = VelocityEngineUtils.mergeTemplateIntoString( engine,
                "ios_manifest.xml", "UTF-8", model );
            return new ResponseEntity<String>( manifest, HttpStatus.OK );
        } else {
            return new ResponseEntity<String>( HttpStatus.NOT_FOUND );
        }
    }
}
