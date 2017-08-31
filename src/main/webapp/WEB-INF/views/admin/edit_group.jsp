<%@ page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<t:background>
    <jsp:attribute name="linkResources">
    </jsp:attribute>
    <jsp:attribute name="linkScripts">
        <script type="text/javascript" src="js/jquery.ui.widget.js"><!--//--></script>
        <script type="text/javascript" src="js/jquery.iframe-transport.js"><!--//--></script>
    </jsp:attribute>
    <jsp:attribute name="pageDidLoad">
        <script type="text/javascript">
            $('#miUserGroups').addClass('active');
            
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
                <li><a href="/admin/groups">用户组管理</a></li>
                <li>编辑用户组</li>
            </ol>
        </div><!--/.row-->
        <div class="row">
            <div class="col-lg-12">
                <form:form method="post" action="/admin/groups/${ group.id }/edit" modelAttribute="group">
                    <div class="panel panel-default">
                        <div class="panel-heading">
                            <p>编辑用户组</p>
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
                            <div class="col-md-9">
                                <div class="form-group">
                                    <label>用户组名</label>
                                    <form:input path="name" cssClass="form-control" maxlength="40"/>
                                    <form:errors cssClass="field-error" path="name" />
                                </div>
                                <div class="form-group">
                                    <div class="checkbox">
                                        <label>
                                            <form:checkbox path="enabled"/> 是否启用?
                                        </label>
                                    </div>
                                    <form:errors cssClass="field-error" path="enabled" />
                                </div>
                            </div>
                        </div>
                    </div>
                    <button type="submit" class="btn btn-info">提交</button>
                    <a href="/admin/groups" class="btn btn-link">取消</a>
                    <c:if test="${ group.users.size() == 0 }">
                      <button type="button" class="btn btn-danger pull-right" onclick="showWarnings();">删除</button>
                    </c:if>
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
                        删除用户组之前请先确认该组中没有用户，否则将无法删除。确定要删除此用户组吗？
                    </div>
                    <div class="modal-footer">
                        <button type="button" class="btn btn-default" data-dismiss="modal">取消</button>
                        <a href="/admin/groups/${ group.id }/delete" class="btn btn-danger">删除</a>
                    </div>
                </div>
            </div>
        </div>
    </jsp:body>
</t:background>