/*
 * Copyright © 2016 Jiangsu Sparknet Software Co., Ltd. All rights reserved.
 *
 * http://www.sparksoft.com.cn
 */

package cn.com.sparksoft.web.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import java.util.Date;

import javax.validation.Valid;

import cn.com.sparksoft.config.Route;
import cn.com.sparksoft.config.WebConfig;
import cn.com.sparksoft.persist.AppService;
import cn.com.sparksoft.persist.model.App;
import cn.com.sparksoft.persist.util.JpaServiceUtil;
import cn.com.sparksoft.persist.util.PaginatedResult;
import cn.com.sparksoft.web.payload.PaginatedList;

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
    private JpaServiceUtil      jpaUtil;
    @Autowired
    private MessageSource       messageSource;

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
                }
            }
            response = new PaginatedList();
            response.setTotal( result.getCount() );
            response.setRows( result.getObjects() );
        } catch (Exception e) {
            logger.error( "Failed to get game list.", e );
        } catch (Error e) {
            logger.error( "Failed to get game list.", e );
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
        if ( app.getIconFile() == null ) {
            br.rejectValue( "packageFile", "NotNull.app.iconFile" );
        }
        if ( br.hasErrors() ) {
            mav.addObject( "app", app );
        } else {
            try {
                app.setCreateTime( new Date() );
                appService.save( app );
                appService.saveAppPackage( app.getId(), app.getPlatform(), app.getPackageFile() );
                appService.saveAppIcon( app.getId(), app.getPlatform(), app.getIconFile() );
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
            if ( ( app.getPackageFile() != null ) && ( app.getPackageFile().getSize() > 0l ) ) {
                appService.saveAppPackage( appId, app.getPlatform(), app.getPackageFile() );
            }
            if ( ( app.getIconFile() != null ) && ( app.getIconFile().getSize() > 0l ) ) {
                appService.saveAppIcon( appId, app.getPlatform(), app.getIconFile() );
            }
            app.setId( appId );
            if ( !br.hasErrors() ) {
                try {
                    app.setCreateTime( localApp.getCreateTime() );
                    app.setUpdateTime( new Date() );
                    appService.update( app );
                    mav.addObject( WebConfig.WEB_PAGE_MESSAGE,
                        messageSource.getMessage( "operation.success", null, null ) );
                } catch (Exception e) {
                    mav.addObject( WebConfig.WEB_PAGE_ERROR,
                        messageSource.getMessage( "operation.fail", null, null ) );
                }
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
            appService.deleteRelatedFiles( appId, app.getPlatform() );
        }
        return mav;
    }
}
