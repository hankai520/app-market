<%@ page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<t:frame>
    <jsp:attribute name="linkResources">
        <link href="css/bootstrap-switch.min.css" rel="stylesheet" />
    </jsp:attribute>
    <jsp:attribute name="linkScripts">
        <script type="text/javascript" src="js/jquery.ui.widget.js"><!--//--></script>
        <script type="text/javascript" src="js/jquery.iframe-transport.js"><!--//--></script>
    </jsp:attribute>
    <jsp:attribute name="pageDidLoad">
        <script type="text/javascript">
            $('#miApps').addClass('active');
        </script>
    </jsp:attribute>
    <jsp:body>
        <div class="row">
            <ol class="breadcrumb">
                <li>
                    <a href=""><span class="glyphicon glyphicon-home"></span></a>
                </li>
                <li><a href="admin/apps">应用管理</a></li>
                <li>添加应用</li>
            </ol>
        </div><!--/.row-->
        <div class="row">
            <div class="col-lg-12">
                <form:form method="post" action="admin/apps/add" modelAttribute="app">
                    <div class="panel panel-default">
                        <div class="panel-heading">
                            <p>添加应用</p>
                        </div>
                        <div class="panel-body">
                            <c:if test="${ not empty pageMessage }">
                                <div class="alert bg-success alert-dismissible" role="alert">
                                    <button type="button" class="close" data-dismiss="alert" aria-label="Close"><span aria-hidden="true">×</span></button>
                                    <span class="glyphicon glyphicon-ok-sign"></span>
                                    ${ pageMessage }
                                </div>
                            </c:if>
                            <c:if test="${ not empty pageError }">
                                <div class="alert bg-danger alert-dismissible" role="alert">
                                    <button type="button" class="close" data-dismiss="alert" aria-label="Close"><span aria-hidden="true">×</span></button>
                                    <span class="glyphicon glyphicon-exclamation-sign"></span>
                                    ${ pageError }
                                </div>
                            </c:if>
                            <div class="col-md-6">
                                <div class="form-group">
                                    <label>应用名称 <span class="fg-red">*</span></label>
                                    <form:input path="name" cssClass="form-control" maxlength="20" placeholder="最大长度20个汉字" />
                                    <form:errors cssClass="field-error" path="name" />
                                </div>
                                <div class="form-group">
                                    <label>应用包ID <span class="fg-red">*</span></label>
                                    <form:input path="bundleIdentifier" cssClass="form-control" maxlength="50" placeholder="最大长度50个字符，如：cn.com.sparksoft.zjsc" />
                                    <form:errors cssClass="field-error" path="bundleIdentifier" />
                                </div>
                                <div class="form-group">
                                    <label>小图标 URL <span class="fg-red">*</span></label>
                                    <form:input path="smallImageUrl" cssClass="form-control" maxlength="200" placeholder="最大长度200个字符，如：http://apps.sparksoft.com.cn/small.png" />
                                    <form:errors cssClass="field-error" path="smallImageUrl" />
                                </div>
                                <div class="form-group">
                                    <label>大图标 URL <span class="fg-red">*</span></label>
                                    <form:input path="largeImageUrl" cssClass="form-control" maxlength="200" placeholder="最大长度200个字符，如：http://apps.sparksoft.com.cn/large.png" />
                                    <form:errors cssClass="field-error" path="largeImageUrl" />
                                </div>
                                <div class="form-group">
                                    <label>安装包 URL <span class="fg-red">*</span></label>
                                    <form:input path="packageUrl" cssClass="form-control" maxlength="200" placeholder="最大长度200个字符，android为apk文件，ios为ipa文件" />
                                    <form:errors cssClass="field-error" path="packageUrl" />
                                </div>
                                <div class="form-group">
                                    <label>版本号 <span class="fg-red">*</span></label>
                                    <form:input path="version" cssClass="form-control" maxlength="200" placeholder="最大长度200个字符，如：1.2.3" />
                                    <form:errors cssClass="field-error" path="version" />
                                </div>
                                <div class="form-group">
                                    <label>运行平台 <span class="fg-red">*</span></label>
                                    <form:select path="platform" cssClass="form-control">
                                        <form:option value="iOS"><fmt:message key="app.platform.iOS" /></form:option>
                                        <form:option value="Android"><fmt:message key="app.platform.Android" /></form:option>
                                    </form:select>
                                    <form:errors cssClass="field-error" path="platform" />
                                </div>
                                <div class="form-group">
                                    <label>状态 <span class="fg-red">*</span></label>
                                    <form:select path="status" cssClass="form-control">
                                        <form:option value="Developing"><fmt:message key="app.status.Developing" /></form:option>
                                        <form:option value="ReadyToSale"><fmt:message key="app.status.ReadyToSale" /></form:option>
                                        <form:option value="Removed"><fmt:message key="app.status.Removed" /></form:option>
                                    </form:select>
                                    <form:errors cssClass="field-error" path="status" />
                                </div>
                            </div>
                        </div>
                    </div>
                    <button type="submit" class="btn btn-info">提交</button>
                    <a href="admin/apps" class="btn btn-link">取消</a>
                </form:form>
            </div>
        </div><!--/.row-->
    </jsp:body>
</t:frame>