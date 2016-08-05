/*
 * Copyright © 2016 Jiangsu Sparknet Software Co., Ltd. All rights reserved.
 *
 * http://www.sparksoft.com.cn
 */

package ren.hankai.web.service;

import org.apache.velocity.app.VelocityEngine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.velocity.VelocityEngineUtils;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import ren.hankai.config.Route;
import ren.hankai.persist.AppService;
import ren.hankai.persist.model.App;
import ren.hankai.persist.model.AppPlatform;
import ren.hankai.persist.util.JpaServiceUtil;
import ren.hankai.util.MobileAppInfo;
import ren.hankai.web.payload.ApiCode;
import ren.hankai.web.payload.ApiResponse;
import ren.hankai.web.payload.BusinessError;

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
    @Autowired
    private JpaServiceUtil      jpaUtil;

    @RequestMapping(
        value = { Route.API_APP_IOS_PACKAGE, Route.API_APP_ANDROID_PACKAGE },
        produces = { MediaType.APPLICATION_OCTET_STREAM_VALUE } )
    public ResponseEntity<ByteArrayResource>
                    getAppPackage( @PathVariable( "appId" ) Integer appId ) {
        App app = appService.find( appId );
        if ( app != null ) {
            return new ResponseEntity<ByteArrayResource>( new ByteArrayResource( app.getBundle() ),
                HttpStatus.OK );
        } else {
            return new ResponseEntity<>( HttpStatus.NOT_FOUND );
        }
    }

    @RequestMapping(
        value = { Route.API_APP_ICON },
        produces = { MediaType.IMAGE_JPEG_VALUE } )
    public ResponseEntity<ByteArrayResource> getAppIcon( @PathVariable( "appId" ) Integer appId ) {
        App app = appService.find( appId );
        if ( app != null ) {
            return new ResponseEntity<ByteArrayResource>( new ByteArrayResource( app.getIcon() ),
                HttpStatus.OK );
        } else {
            return new ResponseEntity<>( HttpStatus.NOT_FOUND );
        }
    }

    @RequestMapping(
        value = Route.API_IOS_MANIFEST,
        produces = { MediaType.TEXT_XML_VALUE } )
    public ResponseEntity<String> generateIosManifest(
                    @PathVariable( "appId" ) Integer appId,
                    HttpServletRequest request ) {
        App app = appService.find( appId );
        if ( app != null ) {
            String baseUrl = request.getScheme() + "://"
                + request.getServerName() + ":" + request.getServerPort()
                + request.getContextPath();
            String packageUrl = baseUrl
                + Route.API_APP_IOS_PACKAGE.replaceAll( "\\{appId\\}", appId + "" );
            Map<String, Object> model = new HashMap<>();
            model.put( "packageUrl", packageUrl );
            String iconUrl = baseUrl + Route.API_APP_ICON.replaceAll( "\\{appId\\}", appId + "" );
            model.put( "smallImageUrl", iconUrl );
            model.put( "largeImageUrl", iconUrl );
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

    @RequestMapping(
        value = Route.API_APP_METADATA,
        produces = { "application/json; charset=utf-8" } )
    @ResponseBody
    public ApiResponse getAppMetaData( @PathVariable( "sku" ) String sku ) {
        ApiResponse response = new ApiResponse();
        try {
            App app = jpaUtil.findUniqueBy( App.class, "sku", sku );
            if ( app != null ) {
                response.getBody().setData( app );
                response.getBody().setSuccess( true );
            } else {
                response.getBody().setError( BusinessError.AppNotFound );
            }
            response.setCode( ApiCode.Success );
        } catch (Exception e) {
            logger.error( Route.API_APP_METADATA, e );
        } catch (Error e) {
            logger.error( Route.API_APP_METADATA, e );
        }
        return response;
    }

    @RequestMapping(
        value = Route.API_UPDATE_APP )
    public ResponseEntity<String> updateApp( @PathVariable( "sku" ) String sku,
                    @RequestPart( "package" ) MultipartFile file ) {
        try {
            App app = jpaUtil.findUniqueBy( App.class, "sku", sku );
            if ( app != null ) {
                if ( ( file != null ) && ( file.getSize() > 0 )
                    && file.getOriginalFilename().matches( "(?i)^.*(\\.ipa|\\.apk)$" ) ) {
                    String[] parts = file.getOriginalFilename().toLowerCase().split( "\\." );
                    String ext = parts[parts.length - 1];
                    AppPlatform platform = ext.equals( "ipa" ) ? AppPlatform.iOS
                        : AppPlatform.Android;
                    MobileAppInfo mai = appService.saveAppPackage( platform, file );
                    if ( mai.getBundleId().equals( app.getBundleIdentifier() ) ) {
                        app.setBundle(
                            FileCopyUtils.copyToByteArray( new File( mai.getBundlePath() ) ) );
                        app.setIcon( mai.getIcon() );
                        app.setVersion( mai.getVersion() );
                        app.setUpdateTime( new Date() );
                        appService.update( app );
                        return new ResponseEntity<>( HttpStatus.OK );
                    } else {
                        return new ResponseEntity<>( HttpStatus.BAD_REQUEST );
                    }
                } else {
                    return new ResponseEntity<>( HttpStatus.BAD_REQUEST );
                }
            } else {
                return new ResponseEntity<>( HttpStatus.NOT_FOUND );
            }
        } catch (Exception e) {
            return new ResponseEntity<>( HttpStatus.INTERNAL_SERVER_ERROR );
        } catch (Error e) {
            return new ResponseEntity<>( HttpStatus.INTERNAL_SERVER_ERROR );
        }
    }
}
