<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<meta name="keywords" content="">
<meta name="description" content="">
<title>教学系统教师pc端</title>
<link href="${context}/css/base.css" rel="stylesheet" type="text/css">
<link href="${context}/js/bootstrap/css/bootstrap.css" rel="stylesheet" type="text/css">
<script type="text/javascript" src="${context}/js/jquery.min.js"></script>
<script type="text/javascript" src="${context}/js/bootstrap/js/bootstrap.min.js"></script>
<script type="text/javascript" src="${context}/js/json2form.js"></script>
<script type="text/javascript" src="${context}/js/turingLib/alert.js"></script>
<script type="text/javascript" src="${context}/js/turingLib/validator.js"></script>
<script type="text/javascript" src="${context}/js/turingLib/modal.js"></script>
<script type="text/javascript">
	var editFlag = '${editFlag?default("")}';
	//页面效果：点击增加按钮，新增一行输入框；点击删除按钮后，删除该按钮对应的一行数据；
	function addRecord(type, value){
		var title = type=='email'?'邮箱':((type=='phone')?'联系电话':'IM');
		var value = value?value:'';
		var recordDiv = '';
		if(title == 'IM'){         
            recordDiv += '<div class="message-wrap">';
            recordDiv += '<div class="mess-input">';
            recordDiv += '<select style="width:40%">';
            recordDiv += '<option>--请选择IM类型--</option>';
            recordDiv += '</select>';
            recordDiv += ('<input name="'+type+'Text" type="text" class="mess-control" placeholder="请添加您的'+title+'" style=" width:58.7%;" value="'+value+'" />');
         	recordDiv += '</div>';
         	recordDiv += '<div class="mess-img">';
            recordDiv += '<a href="javascript:void(0);" class="delet-message" onclick="delRecord(this)"></a>';
            recordDiv += '</div>';
            recordDiv += '</div>';
            $('#'+type+'Div').append(recordDiv);	
		}else{
			recordDiv += '<div class="message-wrap">';
			recordDiv += '<div class="mess-input">';
	        recordDiv += ('<input name="'+type+'Text" type="text" class="mess-control" placeholder="请添加您的'+title+'" style=" width:100%;" value="'+value+'" />');
	        recordDiv += '</div>';
	        recordDiv += '<div class="mess-img">';
	        recordDiv += '<a href="javascript:void(0);" class="delet-message" onclick="delRecord(this)"></a>';
	        recordDiv += '</div>';
	        recordDiv += '</div> ';
			$('#'+type+'Div').append(recordDiv);
		}
	}
	
	function delRecord(btn){
		$(btn).parent().parent().remove();
	}
	//页面加载时执行，将查询到的用户信息进行展示。
	$(function(){
		$('#userInfoForm').json2form({url:'getCurrentTeacherJson'});
		$('#userInfoForm').validatorInit(callback);
		function callback(data){
			JAlert('保存成功');
		}
		//获取专业信息
		loadSchoolData($('#specialty1'), 1 , "fistLoad");
	});
	
	
	//学校的级联查询
	function loadSchoolData(select,type,id){
		//alert("select:"+select+"   type:"+type+"   id:"+id);
		select.html('<option value="${school[2].id!' '}">${school[2].province!'--请选择所在城市--'}</option>');
		if(type){
			$.post('${context}/user/getSchoolSelectData',{type:type,id:id},function(data){
				$.each(data,function(i,r){
					select.append('<option value="'+r.id+'">'+r.value+'</option>');
				});
				if(id){
					select.val(id);
				}
			},'json');
		}
	}
	//用户自定义菜单项的弹窗操作
	function openModal(dtype,pid){
		$.modal.open({
			title:'选择',
			url:'../dictionary/viewDictionaryModal?type='+dtype+'&pid='+pid,
		});
	}
	
</script>
</head>

<body>
	<div class="maincont">
	<div class="title"><span>用户信息</span></div>
	<div class="enter-message">
    	<form id="userInfoForm" method="post" action="updateUserInfo">
    		<div class="message-group">
        		<div class="message-left">教工号：</div>
                <div class="message-right">
                	<input id="teacherNo" name="teacherNo" type="text" class="mess-control" placeholder="请输入您的教工号" />
                    
                </div>                   	
        	</div>
            <div class="message-group">
        		<div class="message-left">姓名：</div>
                <div class="message-right">
                	<input id="name" name="name" type="text" class="mess-control" placeholder="请输入您的姓名" />
                    
                </div>                   	
        	</div>
            <div class="message-group">
        		<div class="message-left">性别：</div>
                <div class="message-right">
                	<#if teacher.sex == '0'>
                	<input name="sex" type="radio" value="0" checked  style=" margin-top:10px;"/> 男
                	<input name="sex" type="radio" value="1" style="margin:12px 0 0 20px;" /> 女
                	<#else>
                	<input name="sex" type="radio" value="0" style="margin:12px 0 0 20px;" /> 男
                	<input name="sex" type="radio" value="1" checked style="margin:12px 0 0 20px;" /> 女
                	</#if>
                </div>                   	
        	</div>
            <div class="message-group">
        		<div class="message-left">职称：</div>
                <div class="message-right" >       
                    <input id="titleSel" onclick="openModal(4,'titleSel');" type="text" class="mess-control" placeholder="请选择职称" value="${(title.value)!''}">
                </div>                 	
        	</div>
            <div class="message-group">
        		<div class="message-left">职务：</div>
                <div class="message-right" >       
                    <input id="postSel" onclick="openModal(5,'postSel');" type="text" class="mess-control" placeholder="请选择职称" value="${(post.value)!''}">
                </div>                   	
        	</div>
            <!--
            <div class="message-group">
        		<div class="message-left">学校：</div>
                <div class="message-right">
                	<input id="school" name="school" type="text" class="mess-control" placeholder="请输入您的学校" />                    
                </div>                   	
        	</div>
        	-->
        	<!--begin-->
        	<div class="message-group">
                    <div class="message-left">学校：</div>
                    <div class="message-right">
                        <select id="specialty1" onchange="loadSchoolData($('#specialty2'), 1 ,this.value);loadSchoolData($('#specialty3'));">
                            <option value="${school[2].id!''}">${school[2].province!'--请选择所在城市--'}</option>
                        </select>    
                        <select id="specialty2" onchange="loadSchoolData($('#specialty3'), 2 ,this.value);" style="margin-top:5px">
                            <option value="${school[1].id!''}">${school[1].city!'--请选择所在城市--'}</option>
                        </select>      
                        <select id="specialty3" name="specialty" style="margin-top:5px" style=" width:100%;">
                            <option value="${school[0].id!''}">${school[0].school!'--请选择学校--'}</option>
                        </select>                 
                    </div>                   	
                </div>
        	<!--end-->
            <div class="message-group">
        		<div class="message-left">院系：</div>
                <div class="message-right">
                	<input id="department" name="department" type="text" class="mess-control" placeholder="请输入您的院系" />                    
                </div>                   	
        	</div>                                  
            <div class="message-group">
        		<div class="message-left">电子邮箱：</div>
        		<input id="email" name="email" type="hidden" />
                <div class="message-right" id="emailDiv">
                	<div class="message-wrap">
                        <div class="mess-input">
                            <input id="emailText" name="emailText" type="text" class="mess-control" placeholder="请添加您的邮箱" style=" width:100%;" />   
                        </div>
                        <div class="mess-img">
                            <a href="javascript:void(0);" class="add-message" onclick="addRecord('email')"></a>
                        </div>
                    </div>        
                </div>                                   	
        	</div>
        	    	
            <div class="message-group">
        		<div class="message-left">联系电话：</div>
        		<input id="phone" name="phone" type="hidden" />
                <div class="message-right" id="phoneDiv">
                	<div class="message-wrap">
                        <div class="mess-input">
                            <input id="phoneName" name="phoneName" type="text" class="mess-control" placeholder="请输入您电话的类型" id="" style=" width:30%;" />   
                            <input id="phoneValue" name="phoneValue" type="text" class="mess-control" placeholder="请输入您的电话号码" id="" style=" width:69.3%;" />   
                        </div>
                        <div class="mess-img">
                            <a href="javascript:void(0);" class="add-message" onclick="addRecord('phone')"></a>
                        </div>
                    </div>               
                </div>
                                   	
        	</div>
                     	
        	<div class="message-group">
        		<div class="message-left">I M：</div>
        		<input id="IM" name="IM" type="hidden" />
                <div class="message-right" id="IMDiv">
                	<div class="message-wrap">
                        <div class="mess-input">
                        	<!--
 							<select style="width:40%">
 								<option>--请选择IM类型--</option>
 								<option>Q Q</option>
 								<option>微信</option>
 								<option>GitHub</option>
 							</select>                       
                            -->
                            <input id="IMName" name="IMName" type="text" class="mess-control" placeholder="请输入您的IM类型" id="" style=" width:30%;" />   
                            <input id="IMValue" name="IMValue" type="text" class="mess-control" placeholder="请输入您的IM号码" id="" style=" width:69.3%;" />   
                        </div>
                        <div class="mess-img">
                            <a href="javascript:void(0);" class="add-message" onclick="addRecord('IM')"></a>
                        </div>
                    </div>               
                </div>
        	</div>
            <div class="message-group">
        		<div class="message-left">教师简介：</div>
                <div class="message-right">
                	
                    <textarea id="introduction" name="introduction" placeholder="教师简介（不超过200字）"></textarea>                    
                </div>                   	
        	</div>  
            <div class="btnsub" style="width:140px">
                <button type="submit" class="btn-submit">保存</button>
            </div>
        </form> 
    </div>
    </div>
	<#include "/footer.ftl" />
</body>
</html>
