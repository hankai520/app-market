/*
 * Copyright © 2016 Jiangsu Sparknet Software Co., Ltd. All rights reserved.
 *
 * http://www.sparksoft.com.cn
 */

package ren.hankai.config.tomcat;

import org.apache.catalina.Context;
import org.springframework.boot.context.embedded.tomcat.TomcatContextCustomizer;

/**
 * Tomcat Context 配置
 *
 * @author hankai
 * @version 1.0
 * @since Apr 5, 2016 4:15:40 PM
 */
public class ContextConfig implements TomcatContextCustomizer {

    /*
     * (non-Javadoc)
     * @see
     * org.springframework.boot.context.embedded.tomcat.TomcatContextCustomizer#customize(org.apache
     * .catalina.Context)
     */
    @Override
    public void customize( Context context ) {
    }
}
