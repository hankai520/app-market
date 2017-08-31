<%@ page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags"%>
<t:background>
    <jsp:attribute name="linkResources">
        <link href="css/bootstrap-table.min.css" rel="stylesheet" />
    </jsp:attribute>
    <jsp:attribute name="linkScripts">
        <script type="text/javascript" src="js/bootstrap-table.min.js"><!--//--></script>
        <script type="text/javascript" src="js/bootstrap-table-zh-CN.min.js"><!--//--></script>
    </jsp:attribute>
    <jsp:attribute name="pageDidLoad">
        <script type="text/javascript">
            $('#miUserGroups').addClass('active');
            <!--// 必须用JS来初始化表格，因为通过HTML属性来初始化，本地化JS的执行会滞后，因而失效 -->
            $('#dataTable').bootstrapTable({
                url: 'admin/groups.json',
                sidePagination: 'server',
                showRefresh: true,
                pageSize: 50,
                pageList: [50,100,150],
                showColumns: true,
                search: true,
                pagination: true,
                columns: [{
                    field: 'id',
                    title: 'ID',
                    sortable: false,
                    valign: 'middle'
                }, {
                    field: 'name',
                    title: '用户组',
                    valign: 'middle',
                    sortable: true,
                    formatter: function (value, row, index) {
                      return '<a href="admin/groups/' + row.id + '/edit">' + value + '</a>';
                  }
                }, {
                  title: '用户数',
                  valign: 'middle',
                  sortable: false,
                  formatter: function (value, row, index) {
                    return row.numberOfUsers;
                }
              }]
            });
        </script>
    </jsp:attribute>
    <jsp:body>
        <div class="row">
            <ol class="breadcrumb">
                <li><a href=""><span class="glyphicon glyphicon-home"></span></a></li>
                <li>用户组管理</li>
            </ol>
        </div><!--/.row-->
        <div class="row">
            <div class="col-lg-12">
                <div class="panel panel-default">
                    <div class="panel-heading">
                        <div class="columns btn-group pull-left">
                            <a href="/admin/groups/add">
                                <button class="btn btn-info" type="button" title="添加用户组">
                                    <i class="glyphicon glyphicon-plus"></i>
                                    添加用户组
                                </button>
                            </a>
                        </div>
                    </div>
                    <div class="panel-body">
                        <table id="dataTable"></table>
                    </div>
                </div>
            </div>
        </div><!--/.row-->
    </jsp:body>
</t:background>