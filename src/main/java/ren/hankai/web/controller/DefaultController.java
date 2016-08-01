/*
 * Copyright © 2016 Jiangsu Sparknet Software Co., Ltd. All rights reserved.
 *
 * http://www.sparksoft.com.cn
 */

package ren.hankai.web.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

import ren.hankai.config.Route;
import ren.hankai.persist.AppService;
import ren.hankai.persist.model.App;

/**
 * 默认控制器
 *
 * @author hankai
 * @version 1.0
 * @since Apr 1, 2016 9:56:54 AM
 */
@Controller
public class DefaultController {

    @Autowired
    private AppService appService;

    @RequestMapping( Route.BACKGROUND_PREFIX )
    public ModelAndView adminIndex() {
        return new ModelAndView( "admin" );
    }

    @RequestMapping(
        value = { "/", Route.FG_APPS } )
    public ModelAndView foregroundIndex() {
        ModelAndView mav = new ModelAndView( "fg_apps" );
        List<App> apps = appService.getAvailableApps();
        mav.addObject( "apps", apps );
        return mav;
    }
}
