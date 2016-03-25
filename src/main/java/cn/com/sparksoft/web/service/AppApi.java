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
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.velocity.VelocityEngineUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.HashMap;
import java.util.Map;

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
        value = Route.API_IOS_MANIFEST,
        produces = { MediaType.APPLICATION_XML_VALUE } )
    public ResponseEntity<String> generateIosManifest( @RequestParam( "app_id" ) Integer appId) {
        App app = appService.find( appId );
        if ( app != null ) {
            Map<String, Object> model = new HashMap<>();
            model.put( "packageUrl", app.getPackageUrl() );
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
