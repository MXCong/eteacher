
//引用style和js文件
if(!$.formValidator){
	document.write('<scri'+'pt type="text/java'+'script" src="../js/formValidator/formValidator-4.1.3.js"></script>');
	document.write('<scri'+'pt type="text/java'+'script" src="../js/formValidator/formValidatorRegex.js"></script>');
}
$(function() {
	$.extend( {
		add : function(a, b) {
			return a + b;
		},
		// 验证正则
		valid_regExp : function(str, reg) {
			var patten = new RegExp(reg);
			return patten.test(str);
		}
	});
	$.fn.extend( {
		// 表单初始化 Callback为验证通过后的方法
		validatorInit : function(Callback, validator) {
			var url, formid;
			formid = $(this).attr("id");
			url = $(this).attr("action");
			$.formValidator.initConfig( {
				theme : "ArrowSolidBox",
				submitOnce : true,
				formID : formid,
				validatorGroup : formid,
				mode : 'AutoTip',
				onError : function(msg, obj, errorlist) {
					alert(msg);
				},
				onSuccess : function() {
					if (validator == undefined || validator()) {
						var data = $("#" + formid).serialize();
						url = $("#" + formid).attr("action");
					    $("form[id='"+formid+"'] :submit").attr("disabled","disabled");
						$.post(url, data, function(result){
						    Callback(result);
						    $("form[id='"+formid+"'] :submit").removeAttr("disabled");
						});
					}
					return false;
				},
				submitAfterAjaxPrompt : '有数据正在异步验证，请稍等...'
			});
		},
		//正常提交表单（非ajax）
		validatorNormalInit : function(validator) {
			var formid = $(this).attr("id");
			$.formValidator.initConfig( {
				theme : "ArrowSolidBox",
				submitOnce : true,
				formID : formid,
				validatorGroup : formid,
				mode : 'AutoTip',
				onError : function(msg, obj, errorlist) {
					alert(msg);
				},
				onSuccess : function() {
					if (validator == undefined || validator()) {
					    return true;
					}
					else{
						return false;
					}
				},
				submitAfterAjaxPrompt : '有数据正在异步验证，请稍等...'
			});
		},
		// 字符验证a为验证对像名称，b为最少字符，c为最多字符
		validatorString : function(a, b, c) {
			var aa, bb, cc;
			aa = a !=undefined ? a : "内容";
			bb = b !=undefined ? b : 1;
			cc = c !=undefined ? c : 10;
			$(this).formValidator( {
				onShow : "请输入" + aa,
				onFocus : aa + "至少" + bb + "个字符,最多" + cc + "个字符",
				onCorrect : "输入合法"
			}).inputValidator( {
				min : bb,
				max : cc,
				empty : {
					leftEmpty : false,
					rightEmpty : false,
					emptyError : aa + "首尾不能有空格"
				},
				onError : aa + "至少" + bb + "个字符,最多" + cc + "个字符"
			}).functionValidator( {
				fun : checkChar
			});
			function checkChar(val, elem) {
				if (val != '') {
					if ($.valid_regExp(val, "[・－]")) {
						return "不能包含特殊字符（[・－]）";
					}
				}
			}
		},
		// select验证a为对像名称
		validatorSelect : function(a) {
			var aa, bb, cc;
			aa = a !=undefined ? a : "内容";
			$(this).formValidator( {
				onShow : "请输入" + aa,
				onFocus : aa + "为必须选择项",
				onCorrect : "输入合法"
			}).inputValidator( {
				min : 1,
				max : 999,
				empty : {
					leftEmpty : false,
					rightEmpty : false,
					emptyError : aa + "两边不能有空格"
				},
				onError : aa + "为必须选择项"
			});
		},
		// 数字验证a为验证对像名称，b为最少位数，c为最多位数
		validatorNumber : function(a, b, c) {
			var aa, bb, cc;
			aa = a !=undefined ? a : "内容";
			bb = b !=undefined ? b : 1;
			cc = c !=undefined ? c : 10;
			$(this).formValidator( {
				onShow : "请输入" + aa,
				onFocus : aa + "至少" + bb + "个位数,最多" + cc + "个位数",
				onCorrect : "输入合法"
			}).inputValidator( {
				min : bb,
				max : cc,
				empty : {
					leftEmpty : false,
					rightEmpty : false,
					emptyError : aa + "两边不能有空格"
				},
				onError : aa + "不能为空,至少" + bb + "个位数,最多" + cc + "个位数"
			}).regexValidator( {
				regExp : "num1",
				dataType : "enum",
				onError : "正数格式不正确，请输入正数或0"
			});
		},
		// 邮箱验证
		validatorEmail : function() {
			$(this).formValidator(
				{
					onShowFixText : "6~18个字符，包括字母、数字、下划线，以字母开头，字母或数字结尾",
					onShow : "请输入邮箱",
					onFocus : "邮箱6-100个字符,输入正确了才能离开焦点",
					onCorrect : "输入合法",
					defaultValue : "@"
				}).inputValidator( {
					min : 6,
					max : 100,
					onError : "您输入的邮箱长度非法,请确认"
				}).regexValidator(
					{
						regExp : "^([a-zA-Z0-9_\.\-])+\@(([a-zA-Z0-9\-])+\.)+([a-zA-Z0-9]{2,4})+$",
						onError : "您输入的邮箱格式不正确"
					});
		},
		// 如果非空则验证是否为邮箱
		validatorEmail2 : function() {
			$(this).formValidator(
				{
					onShowFixText : "邮箱包括字母、数字、下划线，以字母开头，字母或数字结尾",
					onShow : "请输入邮箱",
					onFocus : "邮箱包括字母、数字、下划线，以字母开头，字母或数字结尾",
					onCorrect : "输入正确"
				}).functionValidator( {
					fun : checkEmail
			});
			function checkEmail(val, elem) {
				if (val != '') {
					if (!$.valid_regExp(val, '^([a-zA-Z0-9_\.\-])+\@(([a-zA-Z0-9\-])+\.)+([a-zA-Z0-9]{2,4})+$')) {
						return '请输入正确邮箱';
					} 
				}
			}
		},
		// 电话
		validatorPhone : function() {
			$(this).formValidator( {
				onShow : "请输入国内电话",
				onFocus : "例如：0577-88888888或省略区号88888888",
				onCorrect : "谢谢您的合作，您的国内电话正确"
			}).regexValidator( {
				regExp : "tel",
				dataType : "enum",
				onError : "国内电话格式不正确"
			});
		},
		// 如果非空则验证是否为电话
		validatorPhone2 : function() {
			$(this).formValidator( {
				onShow : "请输入国内电话",
				onFocus : "例如：0577-88888888或省略区号88888888",
				onCorrect : "谢谢您的合作，您的国内电话正确"
			}).functionValidator( {
				fun : checkPhone
			});
			function checkPhone(val, elem) {
				if (val != '') {
					if (!$.valid_regExp(val, '^(([0\\+]\\d{2,3}-)?(0\\d{2,3})-)?(\\d{7,8})(-(\\d{3,}))?$')) {
						return '请输入正确电话号码';
					}
				}
			}
		},
		// 手机验证
		validatorMPhone : function() {
			$(this).formValidator( {
				onShow : "请输入您的手机号码",
				onFocus : "必须是13或15打头哦",
				onCorrect : "谢谢您的合作，您的手机号码正确"
			}).regexValidator( {
				regExp : "mobile",
				dataType : "enum",
				onError : "手机号码格式不正确"
			});
		},
		// 重复验证 a为验证对像名称,b为请求地址,c为最少字符，d为最多字符,e为正则表达式
		validatorRepeat : function(a, b, c, d,e) {
			var aa, bb, cc, dd,ee;
			aa = a !=undefined ? a : "内容";
			bb = b !=undefined ? b : "";
			cc = c !=undefined ? c : 1;
			dd = d !=undefined ? d : 10;
			ee=e!=undefined?e:"username";
			$(this).formValidator( {
				onShow : "请输入" + aa + "",
				onFocus : aa + "至少" + cc + "个字符,最多" + dd + "个字符",
				onCorrect : "请输入" + aa + ""
			}).inputValidator( {
				min : cc,
				max : dd,
				onError : aa + "至少" + cc + "个字符,最多" + dd + "个字符"
			}).regexValidator( {
				regExp :ee,
				dataType : "enum",
				onError : aa +"格式不正确,正确格式应为："+eval("regexEstr."+ee)
			}).ajaxValidator( {
				dataType : "json",
				async : true,
				type : "POST",
				url : bb,
				success : function(data) {
					if (data)
						return true;
					return "该" + aa + "已存在，请更换" + aa;
				},
				buttons : $("#button"),
				error : function(jqXHR, textStatus, errorThrown) {
					alert("服务器没有返回数据，可能服务器忙，请重试" + errorThrown);
				},
				onError : "该"+ aa + "已存在，请更换" + aa,
				onWait : "正在对" + aa + "进行合法性校验，请稍候..."
			}).defaultPassed();
		},
		// 相同验证 a为验证对象名称，b为所要对比对象的id
		validatorEqual : function(a, bb) {
			var aa;
			aa = a !=undefined ? a : "内容";
			$(this).formValidator( {
				onFocus : "请再次输入" + aa,
				onCorrect : aa + "一致"
			}).inputValidator( {
				empty : {
					leftEmpty : false,
					rightEmpty : false,
					emptyError : "重复" + aa + "两边不能有空格"
				},
				onError : "重复" + aa + "不能为空,请确认"
			}).compareValidator( {
				desID : bb,
				operateor : "=",
				onError : "2次" + aa + "不一致,请确认"
			});
		},
		// 数字
		validatorSZ : function(a) {
			var aa;
			aa = a != undefined ? a : "内容";
			$(this).formValidator( {
				onFocus : "请输入数字",
				onCorrect : "谢谢您的合作，您的数字正确"
			}).regexValidator( {
				regExp : "num",
				dataType : "enum",
				onError : aa+"输入的数字格式不正确"
			});
		},
		// 数字（可以为空）
		validatorSZ2 : function(a) {
			var aa;
			aa = a != undefined ? a : "内容";
			$(this).formValidator( {
				onFocus : "请输入数字",
				onCorrect : "谢谢您的合作，您的数字正确"
		}).functionValidator( {
				fun : function(val,elem){
				if(val!=''){
					var result=val.match("^([+-]?)\\d*\\.?\\d+$");
					if(result==null){
						return aa+'请输入数字'
					}
				}
				return true;
			}
			});
		},
		// 整数
		validatorZS : function(a) {
			var aa,dd;
			aa = a != undefined ? a : "内容";
			$(this).formValidator( {
				onFocus : "请输入整数",
				onCorrect : "谢谢您的合作，您的整数正确"
			}).regexValidator( {
				regExp : "intege",
				dataType : "enum",
				onError : aa+"输入的整数格式不正确"
			});
		},
		// 正数
		validatorZS1 : function(a) {
			var aa,dd;
			aa = a != undefined ? a : "内容";
			$(this).formValidator( {
				onFocus : "请输入正数",
				onCorrect : "谢谢您的合作，您的正数正确"
			}).regexValidator( {
				regExp : "num1",
				dataType : "enum",
				onError : aa+"输入的正数格式不正确"
			});
		},
		// 整数验证a为验证对像名称，b为最小值，c为最大值
		validatorZS2 : function(a,b,c) {
			var aa,dd;
			aa = a != undefined ? a : "内容";
			$(this).formValidator( {
				onFocus : "请输入整数,最小值为"+b+",最大值为"+c,
				onCorrect : "谢谢您的合作，您的整数正确"
			}).functionValidator( {
				fun : function(val,elem){
					if(b!=null && val<b){
						return aa+'不可以小于'+b;
					}
					if(c!=null && val>c){
						return aa+'不可以大于'+c;
					}
					return true;
				}
			}).regexValidator( {
				regExp : "num1",
				dataType : "enum",
				onError : aa+"输入的整数格式不正确"
			});
		},
		// 正整数
		validatorZZS : function(a) {
			var aa,dd;
			aa = a != undefined ? a : "内容";
			$(this).formValidator( {
				onFocus : "请输入正整数",
				onCorrect : "谢谢您的合作，您的正整数正确"
			}).regexValidator( {
				regExp : "intege1",
				dataType : "enum",
				onError : aa+"输入的正整数格式不正确"
			});
		},
		// 正整数允许为空
		validatorZZS2 : function(a) {
			var aa;
			aa = a != undefined ? a : "内容";
			$(this).formValidator( {
				onFocus : "请输入正整数",
				onCorrect : "谢谢您的合作，您的输入正确"
			}).functionValidator( {
				fun : function(val,elem){
				if(val!=''){
					var result=val.match("^[1-9]\\d*$");
					if(result==null){
						return aa+'请输入正整数'
					}
				}
				return true;
			}
			});
		},
		// 负数
		validatorFZ : function(a) {
			var aa;
			aa = a != undefined ? a : "内容";
			$(this).formValidator( {
				onFocus : "请输入负数",
				onCorrect : "谢谢您的合作，您的负数正确"
			}).regexValidator( {
				regExp : "num2",
				dataType : "enum",
				onError : aa+"输入的负数格式不正确"
			});
		},
		// 负整数
		validatorFZS : function(a) {
			var aa;
			aa = a != undefined ? a : "内容";
			$(this).formValidator( {
				onFocus : "请输入负整数",
				onCorrect : "谢谢您的合作，您的负整数正确"
			}).regexValidator( {
				regExp : "intege2",
				dataType : "enum",
				onError : aa+"输入的负整数格式不正确"
			});
		},
		// 正浮点数
		validatorZFDS : function(a) {
			var aa;
			aa = a != undefined ? a : "内容";
			$(this).formValidator( {
				onFocus : "请输入正浮点数",
				onCorrect : "谢谢您的合作，您的正浮点数正确"
			}).regexValidator( {
				regExp : "decmal1",
				dataType : "enum",
				onError : aa+"输入的正浮点数格式不正确"
			});
		},
		// 负浮点数
		validatorFFDS : function(a) {
			var aa;
			aa = a != undefined ? a : "内容";
			$(this).formValidator( {
				onFocus : "请输入负浮点数",
				onCorrect : "谢谢您的合作，您的负浮点数正确"
			}).regexValidator( {
				regExp : "decmal2",
				dataType : "enum",
				onError : aa+"输入的负浮点数格式不正确"
			});
		},
		// 非负浮点数
		validatorFFFDS : function(a) {
			var aa;
			aa = a != undefined ? a : "内容";
			$(this).formValidator( {
				onFocus : "请输入非负浮点数",
				onCorrect : "谢谢您的合作，您的非负浮点数正确"
			}).regexValidator( {
				regExp : "decmal4",
				dataType : "enum",
				onError : aa+"输入的非负浮点数格式不正确"
			});
		},
		// 非正浮点数
		validatorFZFDS : function(a) {
			var aa;
			aa = a != undefined ? a : "内容";
			$(this).formValidator( {
				onFocus : "请输入非正浮点数",
				onCorrect : "谢谢您的合作，您的非正浮点数正确"
			}).regexValidator( {
				regExp : "decmal5",
				dataType : "enum",
				onError : aa+"输入的非正浮点数格式不正确"
			});
		},
		// 邮编
		validatorYB : function() {
			$(this).formValidator( {
				onShow : "请输入邮编",
				onFocus : "邮编为6位数字组成",
				onCorrect : "谢谢您的合作，您的邮编正确"
			}).regexValidator( {
				regExp : "zipcode",
				dataType : "enum",
				onError : "邮编格式不正确"
			});
		},
		//非空时验证邮编
		validatorYB2 : function() {
			$(this).formValidator(
				{
					onShow : "请输入邮编",
					onFocus : "邮编为6位数字组成",
					onCorrect : "谢谢您的合作，您的邮编正确"
				}).functionValidator( {
					fun : checkYB
			});
			function checkYB(val, elem) {
				if (val != '') {
					if (!$.valid_regExp(val, '^[0-9]{6}$')) {
						return '邮编格式不正确';
					} 
				}
			}
		},
		// QQ
		validatorQQ : function() {
			$(this).formValidator( {
				onFocus : "请输入QQ号码",
				onCorrect : "谢谢您的合作，您的QQ号码正确"
			}).regexValidator( {
				regExp : "qq",
				dataType : "enum",
				onError : "QQ号码格式不正确"
			});
		},
		// 身份证
		validatorSFZ : function() {
			$(this).formValidator( {
				onFocus : "请输入身份证",
				onCorrect : "谢谢您的合作，您的身份证正确"
			}).regexValidator( {
				regExp : "idcard",
				dataType : "enum",
				onError : "身份证格式不正确"
			});
		},
		// 字母
		validatorZM : function(a) {
			var aa;
			aa = a != undefined ? a : "内容";
			$(this).formValidator( {
				onFocus : "请输入字母",
				onCorrect : "谢谢您的合作，您的字母正确"
			}).regexValidator( {
				regExp : "letter",
				dataType : "enum",
				onError : aa+"输入的字母格式不正确"
			});
		},
		// 大写字母
		validatorDXZM : function(a) {
			var aa;
			aa = a != undefined ? a : "内容";
			$(this).formValidator( {
				onFocus : "请输入大写字母",
				onCorrect : "谢谢您的合作，您的大写字母正确"
			}).regexValidator( {
				regExp : "letter_u",
				dataType : "enum",
				param : "g",
				onError : aa+"输入的大写字母格式不正确"
			});
		},
		// 小写字母
		validatorXXZM : function(a) {
			var aa;
			aa = a != undefined ? a : "内容";
			$(this).formValidator( {
				onFocus : "请输入小写字母",
				onCorrect : "谢谢您的合作，您的小写字母正确"
			}).egexValidator( {
				regExp : "letter_l",
				dataType : "enum",
				param : "g",
				onError : aa+"输入的小写字母格式不正确"
			});
		},
		// 英文字母或数字
		validatorZMSZ : function(a, b, c) {
			var aa, bb, cc;
			aa = a !=undefined ? a : "内容";
			bb = b !=undefined ? b : 1;
			cc = c !=undefined ? c : 10;
			$(this).formValidator( {
				onShow : "请输入" + aa,
				onFocus : aa + "至少" + bb + "个位数,最多" + cc + "个位数",
				onCorrect : "输入合法"
			}).inputValidator( {
				min : bb,
				max : cc,
				empty : {
					leftEmpty : false,
					rightEmpty : false,
					emptyError : aa + "两边不能有空格"
				},
				onError : aa + "不能为空,至少" + bb + "个位数,最多" + cc + "个位数"
			}).regexValidator( {
				regExp : "^[A-Za-z0-9]+$",
				onError : aa+"格式不正确，请输入英文字母或数字"
			});
		},
		// 邮箱或手机
		validatorEmailOrPhone : function() {
			$(this).formValidator(
				{
					onShow : "请输入手机或邮箱",
					onFocus : "请输入手机或邮箱",
					onCorrect : "输入正确"
				}).functionValidator( {
					fun : function(val, elem) {
						if (!$.valid_regExp(val, '^[1][3578][0-9]{9}$') && !$.valid_regExp(val, '^([a-zA-Z0-9_\.\-])+\@(([a-zA-Z0-9\-])+\.)+([a-zA-Z0-9]{2,4})+$')) {
							return '请输入正确手机或邮箱';
						} 
					}
			});
		},
		// 手机
		validatorMobile : function() {
			$(this).formValidator(
				{
					onShow : "请输入手机号",
					onFocus : "请输入手机号",
					onCorrect : "输入正确"
				}).functionValidator( {
					fun : function(val, elem) {
						if (!$.valid_regExp(val, '^[1][3578][0-9]{9}$')) {
							return '请输入正确手机号';
						} 
					}
			});
		},
		validatorPwd6Number : function() {
			$(this).formValidator(
				{
					onShow : "请输入密码，要求6为数字。",
					onFocus : "请输入密码，要求6为数字。",
					onCorrect : "输入正确"
				}).functionValidator( {
					fun : function(val, elem) {
						if (!$.valid_regExp(val, '^\\d{6}$')) {
							return '密码要求为6为数字';
						} 
					}
			});
		},
		// 非空验证
		validatorEmpty : function(a) {
			var aa;
			aa = a !=undefined ? a : "内容";
			$(this).formValidator( {
				onShow : "请输入" + aa,
				onFocus : "请输入" + aa + "，" + aa + "不能为空"
			}).inputValidator( {
				min : 1,
				empty : {
					leftEmpty : false,
					rightEmpty : false,
					emptyError : aa + "首尾不能有空格"
				},
				onError : aa + "不能为空"
			});
		},
		/**
		 * 自定议验证
		 * a为验证表单名称
		 * func为自定义验证方法名默认参数为val,elem 
		 */ 
		validatorFunc : function(a,func) {
			$(this).formValidator( {
				onShow : "请输入" + a,
				onFocus : "输入格式错误",
				onCorrect : "输入合法"
			}).functionValidator({fun:func});
		}
	});
});