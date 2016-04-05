/*
 * Copyright © 2016 Jiangsu Sparknet Software Co., Ltd. All rights reserved.
 *
 * http://www.sparksoft.com.cn
 */

package cn.com.sparksoft.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import cn.com.sparksoft.config.Route;

/**
 * 默认控制器
 *
 * @author hankai
 * @version 1.0
 * @since Apr 1, 2016 9:56:54 AM
 */
@Controller
public class DefaultController {

    @RequestMapping( Route.BACKGROUND_PREFIX )
    public ModelAndView adminIndex() {
        return new ModelAndView( "apps" );
    }
}
