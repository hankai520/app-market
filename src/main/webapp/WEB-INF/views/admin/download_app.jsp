<%@ page language="java" contentType="text/html; charset=UTF-8"
  pageEncoding="UTF-8"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
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
<title>星网移动应用市场 | App下载</title>

<style>
html, body {
    width: 100%;
    height: 100%;
    margin: 0;
    padding: 0;
}

#weixin {
    width: 100%;
    height: 100%;
    display: none;
}

#weixin img {
    width: 100%;
    height: 100%;
}
</style>

<!--[if lt IE 9]>
<script src="js/html5shiv.js"></script>
<script src="js/respond.min.js"></script>
<![endif]-->

</head>

<body>
  <div id="weixin">
    <img src="image/link_img.png" />
  </div>
</body>
<script type="text/javascript">
 window.onload = function() {
    /*如果是微信浏览器*/
    if(isWeiXin()) {
        var wx=document.getElementById("weixin");
        wx.style.display="block";
    } else{
        window.location.href='${downloadUrl}';
    }
 }

 function isWeiXin() {
    var ua = window.navigator.userAgent.toLowerCase();
    if(ua.match(/MicroMessenger/i) == 'micromessenger') {
        return true;
    } else {
        return false;
    }
 }
</script>
</html>
