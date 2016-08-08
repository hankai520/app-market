/*
 * Copyright © 2016 Jiangsu Sparknet Software Co., Ltd. All rights reserved.
 *
 * http://www.sparksoft.com.cn
 */

package ren.hankai.web.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.util.FileCopyUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import java.io.File;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import ren.hankai.config.Route;
import ren.hankai.config.WebConfig;
import ren.hankai.persist.AppService;
import ren.hankai.persist.model.App;
import ren.hankai.persist.util.JpaServiceUtil;
import ren.hankai.persist.util.PaginatedResult;
import ren.hankai.util.MobileAppInfo;
import ren.hankai.web.payload.PaginatedList;

/**
 * 应用信息控制器
 *
 * @author hankai
 * @version 1.0
 * @since Mar 16, 2016 9:13:38 AM
 */
@Controller
public class AppController {

    private static final Logger logger = LoggerFactory.getLogger( AppController.class );
    @Autowired
    private AppService          appService;
    @Autowired
    private MessageSource       messageSource;
    @Autowired
    private JpaServiceUtil      jpaServiceUtil;

    @RequestMapping( Route.BG_APPS )
    public ModelAndView index() {
        return new ModelAndView( "apps" );
    }

    @RequestMapping(
        value = Route.BG_AJAX_APPS,
        produces = { "application/json; charset=utf-8" } )
    @ResponseBody
    public PaginatedList getAppList(
                    @RequestParam(
                        value = "search",
                        required = false ) String search,
                    @RequestParam(
                        value = "order",
                        required = false ) String order,
                    @RequestParam(
                        value = "sort",
                        required = false ) String sort,
                    @RequestParam( "limit" ) int limit,
                    @RequestParam( "offset" ) int offset ) {
        PaginatedList response = null;
        try {
            boolean asc = "asc".equalsIgnoreCase( order );
            PaginatedResult<App> result = appService.getAllApps( search, sort, asc, offset, limit );
            if ( ( result != null ) && ( result.getObjects() != null ) ) {
                for ( App app : result.getObjects() ) {
                    String str = messageSource.getMessage( app.getStatus().i18nKey(), null, null );
                    app.setStatusDesc( str );
                    str = messageSource.getMessage( app.getPlatform().i18nKey(), null, null );
                    app.setPlatformDesc( str );
                    app.setBundle( null );
                    app.setIcon( null );
                }
            }
            response = new PaginatedList();
            response.setTotal( result.getCount() );
            response.setRows( result.getObjects() );
        } catch (Exception e) {
            logger.error( "Failed to get app list.", e );
        } catch (Error e) {
            logger.error( "Failed to get app list.", e );
        }
        return response;
    }

    @RequestMapping(
        value = Route.BG_APP_ADD,
        method = RequestMethod.GET )
    public ModelAndView showAddAppForm() {
        ModelAndView mav = new ModelAndView( "add_app" );
        mav.addObject( "app", new App() );
        return mav;
    }

    @RequestMapping(
        value = Route.BG_APP_ADD,
        method = RequestMethod.POST )
    public ModelAndView addApp(
                    @ModelAttribute( "app" ) @Valid App app,
                    BindingResult br ) {
        ModelAndView mav = new ModelAndView( "add_app" );
        if ( app.getPackageFile() == null ) {
            br.rejectValue( "packageFile", "NotNull.app.packageFile" );
        }
        if ( br.hasErrors() ) {
            mav.addObject( "app", app );
        } else {
            try {
                MobileAppInfo mai = appService.saveAppPackage( app.getPlatform(),
                    app.getPackageFile() );
                app.setBundle( FileCopyUtils.copyToByteArray( new File( mai.getBundlePath() ) ) );
                app.setIcon( mai.getIcon() );
                app.setBundleIdentifier( mai.getBundleId() );
                app.setVersion( mai.getVersion() );
                app.setCreateTime( new Date() );
                appService.save( app );
                mav.addObject( WebConfig.WEB_PAGE_MESSAGE,
                    messageSource.getMessage( "operation.success", null, null ) );
                mav.addObject( "app", new App() );
            } catch (Exception e) {
                mav.addObject( "app", app );
                mav.addObject( WebConfig.WEB_PAGE_ERROR,
                    messageSource.getMessage( "operation.fail", null, null ) );
            }
        }
        return mav;
    }

    @RequestMapping(
        value = Route.BG_APP_EDIT,
        method = RequestMethod.GET )
    public ModelAndView showEditAppForm( @PathVariable( "appId" ) Integer appId ) {
        ModelAndView mav = new ModelAndView( "edit_app" );
        App app = appService.find( appId );
        if ( app != null ) {
            mav.addObject( "app", app );
        } else {
            mav.setViewName( "redirect:/404.html" );
        }
        return mav;
    }

    @RequestMapping(
        value = Route.BG_APP_EDIT,
        method = RequestMethod.POST )
    public ModelAndView editApp(
                    @PathVariable( "appId" ) Integer appId,
                    @ModelAttribute( "app" ) @Valid App app, BindingResult br ) {
        ModelAndView mav = new ModelAndView( "edit_app" );
        App localApp = appService.find( appId );
        if ( localApp == null ) {
            mav.setViewName( "redirect:/404.html" );
        } else {
            try {
                app.setId( appId );
                App dup = jpaServiceUtil.findUniqueBy( App.class, "sku", app.getSku() );
                if ( ( dup != null ) && !dup.getId().equals( appId ) ) {
                    br.rejectValue( "sku", "Duplicate.app.sku" );
                }
                if ( !br.hasErrors() ) {
                    if ( ( app.getPackageFile() != null )
                        && ( app.getPackageFile().getSize() > 0l ) ) {
                        MobileAppInfo mai = appService.saveAppPackage( app.getPlatform(),
                            app.getPackageFile() );
                        localApp.setBundle(
                            FileCopyUtils.copyToByteArray( new File( mai.getBundlePath() ) ) );
                        localApp.setIcon( mai.getIcon() );
                        localApp.setBundleIdentifier( mai.getBundleId() );
                        localApp.setVersion( mai.getVersion() );
                    }
                    localApp.setName( app.getName() );
                    localApp.setSku( app.getSku() );
                    localApp.setPlatform( app.getPlatform() );
                    localApp.setMetaData( app.getMetaData() );
                    localApp.setStatus( app.getStatus() );
                    localApp.setUpdateTime( new Date() );
                    appService.update( localApp );
                    mav.addObject( WebConfig.WEB_PAGE_MESSAGE,
                        messageSource.getMessage( "operation.success", null, null ) );
                }
            } catch (Exception e) {
                mav.addObject( WebConfig.WEB_PAGE_ERROR,
                    messageSource.getMessage( "operation.fail", null, null ) );
            }
            mav.addObject( "app", app );
        }
        return mav;
    }

    @RequestMapping(
        value = Route.BG_APP_DELETE,
        method = RequestMethod.GET )
    public ModelAndView deleteApp( @PathVariable( "appId" ) Integer appId ) {
        ModelAndView mav = new ModelAndView( "redirect:" + Route.BG_APPS );
        App app = appService.find( appId );
        if ( app == null ) {
            mav.setViewName( "redirect:/404.html" );
        } else {
            appService.deleteById( appId );
        }
        return mav;
    }

    @RequestMapping( Route.FG_APPS )
    public ModelAndView foregroundIndex( HttpServletRequest request ) {
        ModelAndView mav = new ModelAndView( "fg_apps" );
        String path = request.getContextPath();
        String basePath = request.getScheme() + "://"
            + request.getServerName() + ":" + request.getServerPort()
            + path + "/";
        mav.addObject( "basePath", basePath );
        List<App> apps = appService.getAvailableApps();
        mav.addObject( "apps", apps );
        return mav;
    }
}
