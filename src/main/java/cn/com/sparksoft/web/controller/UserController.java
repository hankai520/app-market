/*
 * Copyright © 2016 Jiangsu Sparknet Software Co., Ltd. All rights reserved.
 *
 * http://www.sparksoft.com.cn
 */

package cn.com.sparksoft.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import cn.com.sparksoft.config.Route;

/**
 * 用户信息控制器
 *
 * @author hankai
 * @version 1.0
 * @since Mar 28, 2016 2:22:51 PM
 */
@Controller
public class UserController {

    @RequestMapping(
        value = Route.BG_LOGIN,
        method = RequestMethod.GET )
    public ModelAndView login() {
        return new ModelAndView( "login" );
    }
}
