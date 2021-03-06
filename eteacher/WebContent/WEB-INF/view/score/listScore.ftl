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
	var scoreTable;
	$(function(){
		scoreTable = $('#scoreTable').bootstrapTable({
		    url: 'getScoreListData?courseId=${courseId?default("")}',
		    striped : true,
		    toolbar : '#toolbar',
		    search : true,
		    columns: [{
		        field: 'stuNo',
		        halign: 'center',
		        title: '学号'
		    }, {
		        field: 'stuName',
		        halign: 'center',
		        title: '姓名'
		    }, 
		    <#list CourseScoreList as courseScore> 
		    {
		        field: 'score_${courseScore.csId}',
		        halign: 'center',
		        title: '${courseScore.scoreName}'
		    }, 
		    </#list>
		    {
		        field: 'finalScore',
		        halign: 'center',
		        title: '综合成绩'
		    },{
		        title: '操作',
		        align: 'center',
		        formatter : function (value, row, index){
		        	return '<i style="cursor: pointer;" title="成绩录入" class="glyphicon glyphicon-edit"></i>';
		        },
		        events : {
		        	'click .glyphicon-edit': function (e, value, row, index) {
		        		record = row;
		        		$('#scoreModal').modal('show');
			        }
		        }
		    } ]
		});
	});
	function importScore(){
		$('#importFile').trigger('click');
	}
	function exportScore(){
		window.location.href = 'exportScore?courseId=${courseId?default("")}';
	}
	function uploadFile(elem){
		var file = elem.value;
		var type = file.substring(file.lastIndexOf('.')+1).toLowerCase();
		if(type != 'xls' && type != 'xlsx'){
			alert('请选择excel格式文件进行导入');
			return;
		}
		$('#importForm').submit();
		elem.value = '';
	}
	function uploadCallback(){
		alert('导入完成');
		scoreTable.bootstrapTable('refresh');
	}
</script>
</head>
<body>
	<div class="maincont">
		<div class="title"><span>${courseName?default("")}成绩</span></div>
		<div class="enter-message">
			<div id="toolbar"> 
			    <button type="button" class="btn btn-default btn-sm" title="导入" onclick="importScore();">
			        <i class="glyphicon glyphicon-floppy-save"></i> 导入
			    </button>
			    <button type="button" class="btn btn-default btn-sm" title="导出" onclick="exportScore();">
			        <i class="glyphicon glyphicon-floppy-open"></i> 导出
			    </button>
			    <button type="button" class="btn btn-default btn-sm" title="返回" onclick="history.back();">
			        <i class="glyphicon glyphicon-share-alt"></i> 返回
			    </button>
			    
			</div>                          
	        <table id="scoreTable"></table>
        </div>
	</div>
	<div style="display: none;">
		<iframe name="importIframe"></iframe>
		<form id="importForm" method="post" action="importScore" enctype="multipart/form-data" target="importIframe">
			<input type="hidden" name="courseId" value="${courseId?default("")}" />
	    	<input id="importFile" name="importFile" type="file" onchange="uploadFile(this)" />
	    </form>
	</div>
	<#include "/footer.ftl" />
	<#include "scoreModal.ftl" />
</body>
</html>