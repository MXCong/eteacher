(function(window) {
	var h = {};
	/**
	 * 自带默认参数的POST网络请求
	 * 
	 * @author lifei
	 * @param {Object}
	 *            url 访问的接口名称
	 * @param {Object}
	 *            params 需要传递的参数JSON对象
	 * @param {Object}
	 *            callback 成功返回函数 例：function(data){}; data值为返回值，类型为JSON
	 */
	 h.post = function(url, params, fnSuc) {
	 	if (!params) {
	 		params = {};
	 	}
	 	var myDate = new Date();
	 	params.appKey = '20161001_ITEACHER';
	 	params.userId = localStorage.getItem("userId");
	 	var token = localStorage.getItem("token");
	 	params.timeStamp = myDate.getTime();
	 	params.signature = hex_md5(token + params.timeStamp);
	 	$.ajax({
	 		url : 'http://60.205.153.22:8080/eteacher/remote/' + url,
	 		//url : 'http://192.168.1.107:8080/eteacher/remote/' + url,
	 		type : 'POST',
	 		dataType : 'json',
	 		data : params,
	 		error : function(XMLHttpRequest, textStatus, errorThrown) {
	 			
	 		},
	 		success : function(data) {
	 			switch (data.result) {
	 				case '200':
	 				if (fnSuc) {
	 					fnSuc(data.data);
	 				}
	 				break;
	 				case '900':
					// 请求超时
					alert(data.msg);
					if (fnErr) {
						fnErr(XMLHttpRequest.msg);
					}
					break;
					case '406':
					// 非法请求
					alert(data.msg);
					if (fnErr) {
						fnErr(XMLHttpRequest.msg);
					}
					break;
					case '401':
					// 用户不存在
					alert(data.msg);
					goLogin();
					if (fnErr) {
						fnErr(XMLHttpRequest.msg);
					}
					break;
					case '400':
					alert(data.msg);
					// 请求失败
					if (fnErr) {
						fnErr(XMLHttpRequest.msg);
					}
					break;
					case '201':
					alert(data.msg);
					// token过期
					goLogin();
					if (fnErr) {
						fnErr(XMLHttpRequest.msg);
					}
					break;
				}
			}
		});
	 }
	/**
	 * 不带默认参数的POST网络请求
	 * 
	 * @author lifei
	 * @param {Object}
	 *            url 访问的接口名称
	 * @param {Object}
	 *            params 需要传递的参数JSON对象
	 * @param {Object}
	 *            callback 成功返回函数 例：function(data){}; data值为返回值，类型为JSON
	 */
	 h.postNormal = function(url, params, fnSuc) {
	 	$.ajax({
	 		url : 'http://60.205.153.22:8080/eteacher/remote/' + url,
	 		//url : 'http://192.168.1.107:8080/eteacher/remote/' + url,
	 		type : 'POST',
	 		dataType : 'json',
	 		data : params,
	 		error : function(XMLHttpRequest, textStatus, errorThrown) {
				// TODO
			},
			success : function(data) {
				switch (data.result) {
					case '200':
					if (fnSuc) {
						fnSuc(data.data);
					}
					break;

					case '900':
					// 请求超时
					alert(data.msg);
					if (fnErr) {
						fnErr(XMLHttpRequest.msg);
					}
					break;
					case '406':
					// 非法请求
					alert(data.msg);
					if (fnErr) {
						fnErr(XMLHttpRequest.msg);
					}
					break;
					case '401':
					// 用户不存在
					goLogin();
					alert(data.msg);
					if (fnErr) {
						fnErr(XMLHttpRequest.msg);
					}
					break;
					case '400':
					// 请求失败
					alert(data.msg);
					if (fnErr) {
						fnErr(XMLHttpRequest.msg);
					}
					break;
					case '201':
					// token过期
					alert(data.msg);	
					goLogin();
					if (fnErr) {
						fnErr(XMLHttpRequest.msg);
					}
					break;
				}
			}

		});
	 }
	 
	 /**
	*跳转到登录页
	**/
	h.goLogin = function(){
		//alert("222");
		localStorage.clear();
            //获取当前网址，如： http://localhost:8083/uimcardprj/share/meun.jsp
            var curWwwPath = window.document.location.href;
            //获取主机地址之后的目录，如： uimcardprj/share/meun.jsp
            var pathName = window.document.location.pathname;
            var pos = curWwwPath.indexOf(pathName);
            //获取主机地址，如： http://localhost:8083
            var localhostPaht = curWwwPath.substring(0, pos);
            //获取带"/"的项目名，如：/uimcardprj
            var projectName = pathName.substring(0, pathName.substr(1).indexOf('/') + 1);
            window.location.href = localhostPaht+projectName+'/view/login.html';
        }
	/**
	*检查当前是否为登录状态
	**/
	h.checkParams = function(){
		if (!localStorage.getItem("userId") || !localStorage.getItem("token")) {
			h.goLogin();
		}
	}
	 
	 	//上传ajax
		 h.postUp = function(url, params, fnSuc) {
		 	if (!params) {
		 		params = {};
		 	}
		 	var myDate = new Date();
		 	params.appKey = '20161001_ITEACHER';
		 	params.userId = localStorage.getItem("userId");
//		 	var token = localStorage.getItem("token");
		 	params.timeStamp = myDate.getTime();
//		 	params.signature = hex_md5(token + params.timeStamp);
		 	$.ajaxFileUpload({ 
	            url:'http://localhost:8080/eteacher/remote/' + url, 
	            type: 'post',
	            secureuri:false,  
	            fileElementId:params.fileIds,                        //文件选择框的id属性 
	            dataType:'text',
	            data:params,
	            success: function(data, status){ 
//	            	alert('data:'+JSON.stringify(data));
	            	var str=JSON.stringify(data);
	            	var json = eval('(' + str + ')'); 
//	            	alert(json);
//	            	alert($(json).text());
	            	var zz=$(json).text();
	                var obj = eval('(' + zz + ')');
//	            	alert(obj.result);
	            	switch (obj.result) {
					case '200':
					if (fnSuc) {
						fnSuc(data.data);
					}
					break;

					case '900':
					// 请求超时
					if (fnErr) {
						fnErr(XMLHttpRequest.msg);
					}
					break;
					case '406':
					// 非法请求
					if (fnErr) {
						fnErr(XMLHttpRequest.msg);
					}
					break;
					case '401':
					// 用户不存在
					goLogin();
					if (fnErr) {
						fnErr(XMLHttpRequest.msg);
					}
					break;
					case '400':
					// 请求失败
					if (fnErr) {
						fnErr(XMLHttpRequest.msg);
					}
					break;
					case '201':
					// token过期
					goLogin();
					if (fnErr) {
						fnErr(XMLHttpRequest.msg);
					}
					break;
				}
	              },error: function (data, status, e){ 
	            	  
	            }  
	        });
		 }
	 
	 
	 
	// 生成数字串，模拟移动端的设备码
	$.IMEI = function randomString(len) {
		len = len || 15;
		var $chars = '0123456789';
		var maxPos = $chars.length;
		var pwd = '';
		for (i = 0; i < len; i++) {
			pwd += $chars.charAt(Math.floor(Math.random() * maxPos));
		}
		return pwd;
	}

	h.getUrlParam = function(name) {
		var reg = new RegExp("(^|&)" + name + "=([^&]*)(&|$)");
		var r = window.location.search.substr(1).match(reg);
		if (r != null)
			return unescape(r[2]);
		return null;
	}

	window.$httpUtils = h;
})(window);