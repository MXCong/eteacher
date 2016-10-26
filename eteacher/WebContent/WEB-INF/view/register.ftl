<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<meta name="keywords" content="">
<meta name="description" content="">
<title>教学系统教师pc端</title>
<link href="${context}/css/base.css" rel="stylesheet" type="text/css" />
<script type="text/javascript">
	var rootPath = '${context}';
</script>
<script type="text/javascript" src="${context}/js/jquery.min.js"></script>
<script type="text/javascript" src="${context}/js/turingLib/alert.js"></script>
<script type="text/javascript" src="${context}/js/turingLib/validator.js"></script>
<script type="text/javascript">
	var countdown=60; 
	function getVerifyCode(btn){
		if($.formValidator.isOneValid('account')){
			$.post('getVerifyCode',{mobile:$('#account').val()},function(data){
				
			},'json');
			countdown=60; 
			updateBtn(btn);
		}
		else{
			alert('该手机号已注册！');
			$('#account').focus();
		}
	}
	
	function updateBtn(btn){
		if (countdown == 0) { 
			btn.removeAttribute("disabled");    
			btn.value="获取验证码";
		} else { 
			btn.setAttribute("disabled", true); 
			btn.value="重新发送(" + countdown + ")"; 
			countdown--; 
			setTimeout(function() { 
				updateBtn(btn) 
			},1000) 
		} 
	}
	$(function(){
		$('#registerForm').validatorInit(callback,validator);
		function callback(data){
			if(data == 'verifyCode_error'){
				JAlert('验证码错误');
			}
			else{
				JAlert('注册成功','',function(){
					window.location.href = 'login';
				});
			}
		}
		function validator(){
			if($('#AgreeTerms').is(':checked')){
				return true;
			}
			else{
				alert('您还未接受用户协议');
				return false;
			}
			
		}
		$('#account').formValidator({
			onFocus : '请输入手机号',
			onCorrect : '手机号输入格式正确'
		}).regexValidator( {
			regExp : '^[1][3578][0-9]{9}$',
			onError : '请输入正确手机号'
		}).ajaxValidator({
			url : 'existAccount',
			async : true,
			type : 'post',
			dataType : 'text',
			onError: "手机号已被注册",
			success : function(data) {
				if(data == 'success'){
					return true;
				}
				return data;
			}
		});
		$('#password').validatorPwd6Number();
		$('#pwdConfirm').validatorEqual('密码', 'password');
		$('#verifyCode').validatorEmpty('验证码');
	});
</script>
</head>

<body>
<div class="register-box">
	<div class="panel">
    	<div class="panel-body">
        	<div class="reheader">
            	<img src="${context}/images/relogo.png" width="240" height="125" alt="" title="" />
            	<p>用户注册</p>
            </div>
            <form id="registerForm" method="post" action="saveRegister" class="register">
            	<div class="register-body">
            		<div class="form-group">
                    	<label>账号：</label><br/>
                        <input id="account" onkeyup="value=value.replace(/[^\d{1,}\d{1,}|\d{1,}]/g,'')" maxlength="11" name="account" type="text" class="form-control" placeholder="手机号" />
                    </div>
                    
                    <div class="form-group">
                    	<label>设置密码：</label>
                        <input id="password" maxlength="2" name="password" type="password" class="form-control" placeholder="设置密码" onkeyup="value=value.replace(/[\W]/g,'') " onbeforepaste="clipboardData.setData('text',clipboardData.getData('text').replace(/[^\d]/g,''))"/>
                    </div>
                    <div class="form-group">
                    	<label>确认密码：</label>
                        <input id="pwdConfirm" maxlength="20" name="pwdConfirm" type="password" class="form-control" placeholder="确认密码" onkeyup="value=value.replace(/[\W]/g,'') " onbeforepaste="clipboardData.setData('text',clipboardData.getData('text').replace(/[^\d]/g,''))"/>
                    </div>
                    <div class="form-group">
                    	<label>验证码：</label>
                        <div class="yzm">
                        	<input name="verifyCode" maxlength="6" type="text" class="form-control srinput" placeholder="验证码" id="verifyCode" onkeyup="value=value.replace(/[\W]/g,'') " onbeforepaste="clipboardData.setData('text',clipboardData.getData('text').replace(/[^\d]/g,''))" />
                            <input type="button" class="form-control yzminput" value="获取验证码" id="verifyCodeBtn" onclick="getVerifyCode(this)"/>
                        </div>
                    </div>
                    <div class="checkbox-custom">
                    	<div class="agreeterm">
                        	<input id="AgreeTerms" name="agreeterms" type="checkbox" />
							<label for="AgreeTerms">同意 <a href="#">用户注册协议</a></label>
                        </div>
                        <div class="register-btn">
                        	<button type="submit" class="btn-primary">注册</button>
                        </div>
                    </div>
                    
                    <div class="textad">
                    	<p>已有账号？ <a href="login">登录</a></p>
                    </div>
                </div>
            </form>
        </div>
        
    </div>
    <div class="register-bottom">
    	    版权所有 河北图灵 
    </div>
</div>

</body>
</html>
