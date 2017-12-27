<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<t:foreground>
    <jsp:attribute name="linkResources">
        
    </jsp:attribute>
    <jsp:attribute name="linkScripts">
        
    </jsp:attribute>
    <jsp:body>
        <div class="row">
            <div class="col-md-2"></div>
            <div class="col-md-8">
                <c:choose>
                    <c:when test="${ not empty apps }">
                        <c:forEach items="${ apps }" var="app">
                            <div class="row text-center">
                                <div class="col-lg-3 col-md-2 col-xs-1"></div>
                                <div class="col-lg-6 col-md-8 col-xs-10">
                                    <div class="panel panel-blue panel-widget ">
                                        <div class="row text-left">
                                            <div class="col-xs-4 col-md-3 col-lg-4">
                                                <img class="img-responsive app-icon" src="/api/app/${ app.id }/icon" />
                                            </div>
                                            <div class="col-xs-8 col-md-9 col-lg-8">
                                                <c:set var="packageUrl" value="#" />
                                                <c:choose>
                                                    <c:when test="${ app.platform.name() eq 'iOS' }">
                                                        <c:set var="packageUrl" 
                                                        value="itms-services://?action=download-manifest&url=${ basePath }/api/app/${ app.id }/ios_manifest.plist" />
                                                    </c:when>
                                                    <c:when test="${ app.platform.name() eq 'Android' }">
                                                        <c:set var="packageUrl" value="${ basePath }/api/app/${ app.id }/package.apk" />
                                                    </c:when>
                                                </c:choose>
                                                <h2><a href="${ packageUrl }">${ app.name }</a></h2>
                                                <h4>&nbsp;V ${ app.version } ( <fmt:message key="${ app.platform.i18nKey() }" /> )</h4>
                                            </div>
                                        </div>
                                    </div>
                                </div>
                                <div class="col-lg-3 col-md-2 col-xs-1"></div>
                                </div>
                        </c:forEach>
                    </c:when>
                    <c:otherwise>
                        <p class="text-center">应用即将上线，敬请期待！</p>
                    </c:otherwise>
                </c:choose>
            </div>
            <div class="col-md-2"></div>
        </div>
    </jsp:body>
</t:foreground>
