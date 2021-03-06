<%@ page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<t:background>
    <jsp:attribute name="linkResources">
        <link href="css/bootstrap-switch.min.css" rel="stylesheet" />
    </jsp:attribute>
    <jsp:attribute name="linkScripts">
        <script type="text/javascript" src="js/jquery.ui.widget.js"><!--//--></script>
        <script type="text/javascript" src="js/jquery.iframe-transport.js"><!--//--></script>
        <script type="text/javascript" src="js/jquery.fileupload.js"><!--//--></script>
        <script type="text/javascript" src="js/jquery.fileupload-process.js"><!--//--></script>
        <script type="text/javascript" src="js/jquery.fileupload-validate.js"><!--//--></script>        
        <script type="text/javascript" src="js/bootstrap-switch.min.js"><!--//--></script>
    </jsp:attribute>
    <jsp:attribute name="pageDidLoad">
        <script type="text/javascript">
            $('#miApps').addClass('active');
            
            function showWarnings() {
                $('#confirmModal').modal('show');
            }
        </script>
    </jsp:attribute>
    <jsp:body>
        <div class="row">
            <ol class="breadcrumb">
                <li>
                    <a href=""><span class="glyphicon glyphicon-home"></span></a>
                </li>
                <li><a href="admin/apps">应用管理</a></li>
                <li>编辑应用</li>
            </ol>
        </div><!--/.row-->
        <div class="row">
            <div class="col-lg-12">
                <form:form method="post" action="admin/apps/${ app.id }/edit" enctype="multipart/form-data" modelAttribute="app">
                    <div class="panel panel-default">
                        <div class="panel-heading">
                            <p>编辑应用</p>
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
                                    <label>SKU <span class="fg-red">*</span></label>
                                    <form:input path="sku" cssClass="form-control" maxlength="20" placeholder="最大长度20个字符" />
                                    <form:errors cssClass="field-error" path="sku" />
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
                                    <label>应用包ID <span class="fg-red">*</span></label>
                                    <form:input path="bundleIdentifier" cssClass="form-control" readonly="true" />
                                </div>
                                <div class="form-group">
                                    <label>版本号 <span class="fg-red">*</span></label>
                                    <form:input path="version" cssClass="form-control" readonly="true" />
                                </div>
                                <div class="form-group">
                                    <label>安装包 </label>
                                    <form:input path="packageFile" type="file" cssClass="form-control"/>
                                    <form:errors cssClass="field-error" path="packageFile" />
                                </div>
                                <div class="form-group">
                                    <label>配置信息 </label>
                                    <form:textarea path="metaData" rows="8" cssClass="form-control" maxlength="800" placeholder="APP 配置信息，如 XML/JSON 格式的标记信息" />
                                    <form:errors cssClass="field-error" path="metaData" />
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
                                <div class="form-group">
                                    <label>启用更新检查 <span class="fg-red">*</span></label>
                                    <div class="checkbox">
                                        <label>
                                            <form:checkbox path="enableUpdateCheck"/> 是否启用?
                                        </label>
                                    </div>
                                    <form:errors cssClass="field-error" path="enableUpdateCheck" />
                                </div>
                            </div>
                        </div>
                    </div>
                    <button type="submit" class="btn btn-info">提交</button>
                    <a href="admin/apps" class="btn btn-link">取消</a>
                    <button type="button" class="btn btn-danger pull-right" onclick="showWarnings();">删除</button>
                </form:form>
            </div>
        </div><!--/.row-->
        <div class="modal fade" id="confirmModal" tabindex="-1" role="dialog" aria-labelledby="titleLabel" aria-hidden="true">
            <div class="modal-dialog">
                <div class="modal-content">
                    <div class="modal-header">
                        <h4 class="modal-title" id="titleLabel">
                           <span class="glyphicon glyphicon-exclamation-sign" aria-hidden="true"></span> 确认删除
                        </h4>
                    </div>
                    <div class="modal-body">
                        确定要删除此应用吗？
                    </div>
                    <div class="modal-footer">
                        <button type="button" class="btn btn-default" data-dismiss="modal">取消</button>
                        <a href="admin/apps/${ app.id }/delete" class="btn btn-danger">删除</a>
                    </div>
                </div>
            </div>
        </div>
    </jsp:body>
</t:background>