<%@ page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags"%>
<t:frame>
    <jsp:attribute name="linkResources">
        <link href="css/bootstrap-table.min.css" rel="stylesheet" />
    </jsp:attribute>
    <jsp:attribute name="linkScripts">
        <script type="text/javascript" src="js/bootstrap-table.min.js"><!--//--></script>
        <script type="text/javascript" src="js/bootstrap-table-zh-CN.min.js"><!--//--></script>
    </jsp:attribute>
    <jsp:attribute name="pageDidLoad">
        <script type="text/javascript">
            $('#miApps').addClass('active');
            <!--// 必须用JS来初始化表格，因为通过HTML属性来初始化，本地化JS的执行会滞后，因而失效 -->
            $('#appsTable').bootstrapTable({
                url: apps.json',
                sidePagination: 'server',
                showRefresh: true,
                pageList: [10,20,50,100],
                showColumns: true,
                search: true,
                pagination: true,
                columns: [{
                    field: 'id',
                    title: 'ID',
                    sortable: 'true',
                    valign: 'middle'
                }, {
                    field: 'smallImageUrl',
                    title: '图标',
                    valign: 'middle',
                    formatter: function(value, row, index) {
                        if (value && value.length > 0) {
                            return '<img src="' + value + '" class="logo-thumbnail" />';
                        } else {
                            return '-';
                        }
                    }
                }, {
                    field: 'name',
                    title: '名称',
                    valign: 'middle',
                    sortable: 'true'
                }, {
                    field: 'packageUrl',
                    title: '安装包',
                    valign: 'middle',
                    sortable: 'false',
                    formatter: function (value, row, index) {
                        if (value && value.length > 0) {
                            return '<a href="' + value + '">' + value + '</a>';
                        }
                    }
                }, {
                    title: '操作',
                    valign: 'middle',
                    formatter: function (value, row, index) {
                        return '<a href="apps/' + row.id + '/edit"><i class="glyphicon glyphicon-edit"></i></a>';
                    }
                }]
            });
        </script>
    </jsp:attribute>
    <jsp:body>
        <div class="row">
            <ol class="breadcrumb">
                <li><a href=""><span class="glyphicon glyphicon-home"></span></a></li>
                <li>应用管理</li>
            </ol>
        </div><!--/.row-->
        <div class="row">
            <div class="col-lg-12">
                <div class="panel panel-default">
                    <div class="panel-heading">
                        <div class="columns btn-group pull-left">
                            <a href="apps/add">
                                <button class="btn btn-info" type="button" title="添加应用">
                                    <i class="glyphicon glyphicon-plus"></i>
                                    添加应用
                                </button>
                            </a>
                        </div>
                    </div>
                    <div class="panel-body">
                        <table id="appsTable"></table>
                    </div>
                </div>
            </div>
        </div><!--/.row-->
    </jsp:body>
</t:frame>