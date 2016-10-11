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
<script type="text/javascript" src="${context}/js/turingLib/alert.js"></script>
<script type="text/javascript" src="${context}/js/bootstrap/js/bootstrap.min.js"></script>
<script type="text/javascript" src="${context}/js/bootstrap-table/bootstrap-table.min.js"></script>
<script type="text/javascript" src="${context}/js/bootstrap-table/bootstrap-table-zh-CN.min.js"></script>
<script type="text/javascript">
	var courseTable;
	$(function(){
		courseTable = $('#courseTable').bootstrapTable({
		    url: 'getCourseListData',
		    //method : 'post',
		    queryParams : function(params){
		    	params.termId = $('#termSelect').val();
		    	return params;
		    },
		    striped : true,
		    toolbar : '#toolbar',
		    //search : true,
		    idField : 'courseId',
		    columns: [{
		        title: '序号',
		        align: 'center',
		        formatter : function (value, row, index){
		        	return index + 1;
		        }
		    },{
		        field: 'courseName',
		        halign: 'center',
		        title: '课程名称',
		        formatter : function (value, row, index){
		        	//return '<a href="${context}/work/viewListWork?termId='+row.termId+'&courseId='+row.courseId+'">'+value+'</a>';
		        	var url = '${context}/work/viewListWork?termId=' + row.termId +'&courseId='+row.courseId;
		        	return '<a href="#" onclick="parent.jump(\''+url+'\',parent.document.getElementById(\'workMenu\'))">'+value+'</a>';
		        }
		    }, {
		        field: 'specialty',
		        halign: 'center',
		        title: '专业'
		    }, {
		        field: 'courseType',
		        halign: 'center',
		        title: '课程类型'
		    }, {
		        title: '操作',
		        align: 'center',
		        formatter : function (value, row, index){
		        	return '<i style="cursor: pointer;" title="编辑" class="glyphicon glyphicon-edit"></i> &nbsp;'
		        		 + '<i style="cursor: pointer;" title="课表" class="glyphicon glyphicon-list-alt"></i> &nbsp;'
		        		 + '<i style="cursor: pointer;" title="删除" class="glyphicon glyphicon-trash"></i>';
		        },
		        events : {
		        	'click .glyphicon-edit': function (e, value, row, index) {
			            window.location.href = 'viewEditCourse?courseId=' + row.courseId;
			        },
			        'click .glyphicon-list-alt': function (e, value, row, index) {
			            window.location.href = '${context}/courseTable/viewListCourseTable?courseId=' + row.courseId;
			        },
			        'click .glyphicon-trash': function (e, value, row, index) {
			        	if(confirm("确定删除")){
			            	$.post('deleteCourse',{courseId : row.courseId},function(data){
				            	courseTable.bootstrapTable('refresh');
				            });
			            }
			        }
		        }
		    } ]
		});
	});
	function add(){
		window.location.href = 'viewAddCourse?termId=' + $('#termSelect').val();
	}
</script>
</head>
<body>
	<div class="maincont">
		<div class="title"><span>课程列表</span></div>
		<div class="enter-message">
			<div id="toolbar">
			    <button type="button" class="btn btn-default btn-sm" title="新增" onclick="add();">
			        <i class="glyphicon glyphicon-plus"></i> 新增
			    </button>
			</div>
			<table width="100%" border="0" cellpadding="0" cellspacing="0">
            	<tbody>
                	<tr>
                    	<td width="8%" align="left" valign="middle"><label for="xueqi" style="line-height:36px; margin-bottom:0;">学期：</label></td>
                        <td>
                        	<select id="termSelect" class="select-control" style="width:30%" onchange="courseTable.bootstrapTable('refresh');">   
                        		<#list termList as term> 
									<option value=${term.termId} 
										<#if term.termId == termId?default("")>
											selected="selected"
										</#if>>
										${term.termName}
									</option>
								</#list>                          
                            </select>                                
                        </td>                    
                </tbody>
            </table>
			<table id="courseTable"></table>
        </div>
	</div>
	<#include "/footer.ftl" />
</body>
</html>