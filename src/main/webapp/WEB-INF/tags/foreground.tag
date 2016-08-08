<%@ tag description="Main JSP Frame Template" pageEncoding="UTF-8"%>
<!--//用于内嵌的页面加载自身需要的资源 -->
<%@attribute name="linkResources" fragment="true" %>
<!--//用于内嵌的页面加载自身需要的JS -->
<%@attribute name="linkScripts" fragment="true" %>
<!--//页面的JS引用将会放在页面底部用以加快页面加载，被嵌入的子页面应该在该代码块内处理初始化任务 -->
<%@attribute name="pageDidLoad" fragment="true" %>

<!DOCTYPE html>
<html>
<head>
<meta charset="utf-8">
<meta name="viewport" content="width=device-width, initial-scale=1">
<!-- <link rel="shortcut icon" href="resources/images/favicon.ico" type="image/x-icon" /> -->
<%
    String path = request.getContextPath();
    String basePath = request.getScheme() + "://"
            + request.getServerName() + ":" + request.getServerPort()
            + path + "/";
%>
<base href="<%=basePath%>" />
<title>星网移动应用市场</title>

<link href="css/bootstrap.min.css" rel="stylesheet">
<link href="css/bootstrap-switch.min.css" rel="stylesheet">
<link href="css/styles.css" rel="stylesheet">
<link href="css/custom.css" rel="stylesheet">

<script src="js/jquery-1.11.1.min.js"></script>
<script src="js/jquery.cookie.js"></script>
<script src="js/bootstrap.min.js"></script>
<!--//链接子页面脚本 -->
<jsp:invoke fragment="linkScripts" />

<!--[if lt IE 9]>
<script src="js/html5shiv.js"></script>
<script src="js/respond.min.js"></script>
<![endif]-->

<!--//子页面级资源应该有最高优先级，因此最后引入 -->
<jsp:invoke fragment="linkResources" />
</head>

<body>
    <input type="hidden" id="baseUrl" value="<%=basePath%>" />
    <nav class="navbar navbar-inverse navbar-fixed-top" role="navigation">
        <div class="container-fluid">
            <div class="navbar-header">
                <a class="navbar-brand" href=""><span>星网</span> 移动应用市场</a>
                <ul class="user-menu">
                    <li class="dropdown pull-right">
                        <a href="javascript:;" class="dropdown-toggle" data-toggle="dropdown">
                            <span class="glyphicon glyphicon-user"></span> ${ current_fg_user.mobile } <span class="caret"></span>
                        </a>
                        <ul class="dropdown-menu" role="menu">
                            <li><a href="/market/logout"><span class="glyphicon glyphicon-log-out"></span>退出</a></li>
                        </ul>
                    </li>
                </ul>
            </div>
        </div><!-- /.container-fluid -->
    </nav>
    <div style="padding-top: 20px;">
        <jsp:doBody />
    </div>
    <!--//初始化 -->
    <script src="js/custom/app.js"></script>
    <jsp:invoke fragment="pageDidLoad" />
</body>
</html>
