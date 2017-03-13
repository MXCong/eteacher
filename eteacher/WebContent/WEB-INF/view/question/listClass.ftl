<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<meta name="keywords" content="">
<meta name="description" content="">
<title>教学系统教师pc端</title>
<link href="${context}/css/base.css" rel="stylesheet" type="text/css">
<link href="${context}/js/bootstrap/css/bootstrap.css" rel="stylesheet" type="text/css">
<link href="${context}/js/bootstrap-table/bootstrap-table.css" rel="stylesheet" type="text/css">
<script type="text/javascript" src="${context}/js/jquery.min.js"></script>
<script type="text/javascript" src="${context}/js/bootstrap/js/bootstrap.min.js"></script>
<script type="text/javascript" src="${context}/js/bootstrap-table/bootstrap-table.min.js"></script>
<script type="text/javascript" src="${context}/js/bootstrap-table/bootstrap-table-zh-CN.min.js"></script>
<script type="text/javascript">
	$(function(){
		var classTable = $('#classTable').bootstrapTable({
		    url: 'getClassListData',
		    striped : true,
		    toolbar : '#toolbar',
		    columns: [{
		        title: '序号',
		        align: 'center',
		        formatter : function (value, row, index){
		        	return index + 1;
		        }
		    },{
		        field: 'className',
		        halign: 'center',
		        title: '班级名称'
		    }, {
		        field: 'grade',
		        halign: 'center',
		        title: '年级'
		    }, {
		        field: 'majorName',
		        halign: 'center',
		        title: '专业'
		    }, {
		        title: '操作',
		        field: 'classId',
		        align: 'center',
		        formatter : function (value, row, index){
		        	return '<i style="cursor: pointer;" title="编辑" class="glyphicon glyphicon-edit"></i> &nbsp;'
		        		 + '<i style="cursor: pointer;" title="删除" class="glyphicon glyphicon-trash"></i>';
		        },
		        events : {
		        	'click .glyphicon-edit': function (e, value, row, index) {
			            window.location.href = 'viewEditClass?classId=' + value;
			        },
			        'click .glyphicon-trash': function (e, value, row, index) {
			        	if(confirm("确定删除")){
			            	$.post('deleteClass',{classId : value},function(data){
			            		if(data!='success'){
			            			alert(data);
			            		}
				            	classTable.bootstrapTable('refresh');
				            });
			            }
			        }
		        }
		    } ]
		});
	});
</script>
</head>
<body>
	<div class="maincont">
		<div class="title"><span>班级列表</span></div>
		<div class="enter-message">
			<div id="toolbar" class="btn-group">
			    <button type="button" class="btn btn-default  btn-sm" title="新增" onclick="window.location.href='viewAddClass'">
			        <i class="glyphicon glyphicon-plus"></i> 新增
			    </button>
			</div>
			<table id="classTable"></table>
        </div>
	</div>
	<#include "/footer.ftl" />
</body>
</html>