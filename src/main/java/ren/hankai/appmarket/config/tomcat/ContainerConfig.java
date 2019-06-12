/*
 * Copyright © 2015 Jiangsu Sparknet Software Co., Ltd. All rights reserved
 * http://www.sparksoft.com.cn
 */

package ren.hankai.appmarket.config.tomcat;

import org.apache.catalina.valves.RemoteIpValve;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.embedded.ConfigurableEmbeddedServletContainer;
import org.springframework.boot.context.embedded.EmbeddedServletContainerCustomizer;
import org.springframework.boot.context.embedded.tomcat.TomcatEmbeddedServletContainerFactory;
import org.springframework.boot.web.servlet.ErrorPage;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import ren.hankai.appmarket.config.Route;

/**
 * 自定义servlet容器配置。
 *
 * @author hankai
 * @version 1.0.0
 * @since Jul 14, 2015 10:08:48 AM
 */
@Component
public class ContainerConfig implements EmbeddedServletContainerCustomizer {

  @Autowired
  private ConnectorConfig connectorConfig;

  @Override
  public void customize(final ConfigurableEmbeddedServletContainer container) {
    container.addErrorPages(
        // Http 错误
        new ErrorPage(HttpStatus.BAD_REQUEST, Route.ERROR_PREFIX + "/400"),
        new ErrorPage(HttpStatus.FORBIDDEN, Route.ERROR_PREFIX + "/403"),
        new ErrorPage(HttpStatus.NOT_FOUND, Route.ERROR_PREFIX + "/404"),
        new ErrorPage(HttpStatus.INTERNAL_SERVER_ERROR, Route.ERROR_PREFIX + "/500"),
        // 异常
        new ErrorPage(Exception.class, Route.ERROR_PREFIX),
        new ErrorPage(Error.class, Route.ERROR_PREFIX));
    if (container instanceof TomcatEmbeddedServletContainerFactory) {
      final TomcatEmbeddedServletContainerFactory cf =
          (TomcatEmbeddedServletContainerFactory) container;
      cf.addConnectorCustomizers(connectorConfig);
      final RemoteIpValve riv = new RemoteIpValve();
      riv.setRemoteIpHeader("x-forwarded-for");
      riv.setProxiesHeader("x-forwarded-by");
      riv.setProtocolHeader("x-forwarded-proto");
      cf.addContextValves(riv);
    }
  }
}
