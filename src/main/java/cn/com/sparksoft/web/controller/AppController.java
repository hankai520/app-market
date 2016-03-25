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

    @RequestMapping( "/apps" )
    public ModelAndView index() {
        // return new ModelAndView( "apps" );
        return new ModelAndView( "redirect:/404.html" );
    }

    @RequestMapping(
        value = "/apps/add",
        method = RequestMethod.GET )
    public ModelAndView showAddAppForm() {
        ModelAndView mav = new ModelAndView( "addApp" );
        mav.addObject( "app", new App() );
        return mav;
    }

    @RequestMapping(
        value = "/apps/add",
        method = RequestMethod.POST )
    public ModelAndView addApp( @ModelAttribute( "app" ) @Valid App app,
                    BindingResult br ) {
        ModelAndView mav = new ModelAndView( "addApp" );
        if ( br.hasErrors() ) {
            mav.addObject( "app", app );
        } else {
            try {
                appService.save( app );
                // if ( attachmentService.saveAttachment( game.getLogo() ) ) {
                // mav.addObject( WebConfig.WEB_PAGE_MESSAGE,
                // messageSource.getMessage( "operation.success", null, null ) );
                // mav.addObject( "game", new Game() );
                // } else {
                // logger.error(
                // String.format( "Failed to save cached attachment %s.", game.getLogo() ) );
                // mav.addObject( "game", game );
                // mav.addObject( WebConfig.WEB_PAGE_ERROR,
                // messageSource.getMessage( "operation.fail", null, null ) );
                // }
            } catch (Exception e) {
                mav.addObject( "app", app );
                mav.addObject( WebConfig.WEB_PAGE_ERROR,
                    messageSource.getMessage( "operation.fail", null, null ) );
            }
        }
        return mav;
    }

    @RequestMapping(
        value = "/apps/{appId}/edit",
        method = RequestMethod.GET )
    public ModelAndView showEditAppForm( @PathVariable( "appId" ) Integer appId ) {
        ModelAndView mav = new ModelAndView( "editApp" );
        App app = appService.find( appId );
        if ( app != null ) {
            mav.addObject( "app", app );
        } else {
            mav.setViewName( "redirect:/404.html" );
        }
        return mav;
    }

    @RequestMapping(
        value = "/apps/{appId}/edit",
        method = RequestMethod.POST )
    public ModelAndView editApp( @PathVariable( "appId" ) Integer appId,
                    @ModelAttribute( "app" ) @Valid App app, BindingResult br ) {
        ModelAndView mav = new ModelAndView( "editApp" );
        App existApp = jpaUtil.findUniqueBy( App.class, "name", app.getName() );
        if ( ( existApp != null ) && !existApp.getId().equals( appId ) ) {
            br.rejectValue( "name", "Duplicate.app.name" );
        }
        app.setId( appId );
        if ( !br.hasErrors() ) {
            try {
                app.setUpdateTime( new Date() );
                appService.update( app );
            } catch (Exception e) {
                mav.addObject( WebConfig.WEB_PAGE_ERROR,
                    messageSource.getMessage( "operation.fail", null, null ) );
            }
        }
        mav.addObject( "app", app );
        return mav;
    }

    @RequestMapping(
        value = "/apps.json",
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
            PaginatedResult<App> result = appService.findAll( 0, -1 );
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

    @RequestMapping( "/apps/{appId}/delete" )
    public ModelAndView deleteGame( @PathVariable( "appId" ) Integer appId ) {
        ModelAndView mav = new ModelAndView( "redirect:/apps" );
        App app = appService.find( appId );
        if ( app == null ) {
            mav.setViewName( "redirect:/404.html" );
        } else {
            appService.deleteById( appId );
        }
        return mav;
    }
}
