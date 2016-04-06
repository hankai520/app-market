<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
<meta name="viewport" content="width=device-width, initial-scale=1">
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<%
    String path = request.getContextPath();
    String basePath = request.getScheme() + "://"
            + request.getServerName() + ":" + request.getServerPort()
            + path + "/";
%>
<base href="<%=basePath%>" />
<title>星网移动应用市场</title>

<link href="css/bootstrap.min.css" rel="stylesheet">
<link href="css/datepicker3.css" rel="stylesheet">
<link href="css/styles.css" rel="stylesheet">

<!--[if lt IE 9]>
<script src="js/html5shiv.js"></script>
<script src="js/respond.min.js"></script>
<![endif]-->

</head>

<body>
    <h2 class="text-center">星网移动应用市场</h2>
    <div class="row">
        <div class="col-md-2"></div>
        <div class="col-md-8">
            <div class="row fluid text-center">
                <c:choose>
                    <c:when test="${ not empty apps }">
                        <c:forEach items="${ apps }" var="app">
                            <div class="col-md-3"></div>
                            <div class="col-md-6">
                                <div class="panel panel-blue panel-widget ">
                                    <div class="row text-left">
                                        <div class="col-sm-4 col-lg-4">
                                            <img class="img-responsive" src="${ app.smallImageUrl }" />
                                        </div>
                                        <div class="col-sm-8 col-lg-8 ">
                                            <c:set var="packageUrl" value="#" />
                                            <c:choose>
                                                <c:when test="${ app.platform.name() eq 'iOS' }">
                                                    <c:set var="ctx" value="<%=basePath%>" />
                                                    <c:set var="packageUrl" 
                                                    value="itms-services://?action=download-manifest&url=${ ctx }api/app/${ app.id }/ios_manifest" />
                                                </c:when>
                                                <c:when test="${ app.platform.name() eq 'Android' }">
                                                    <c:set var="packageUrl" value="${ app.packageUrl }" />
                                                </c:when>
                                            </c:choose>
                                            <h2><a href="${ packageUrl }">${ app.name }</a></h2>
                                            <h4>&nbsp;V ${ app.version } ( <fmt:message key="${ app.platform.i18nKey() }" /> )</h4>
                                        </div>
                                    </div>
                                </div>
                            </div>
                            <div class="col-md-3"></div>
                        </c:forEach>
                    </c:when>
                    <c:otherwise>
                        应用即将上线，敬请期待！
                    </c:otherwise>
                </c:choose>
            </div>
        </div>
        <div class="col-md-2"></div>
    </div>
    <script src="js/jquery-1.11.1.min.js"></script>
    <script src="js/bootstrap.min.js"></script>
</body>

</html>
