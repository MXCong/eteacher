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
	 		url : 'http://192.168.1.105:8080/eteacher/remote/' + url,
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
	 		url : 'http://192.168.1.105:8080/eteacher/remote/' + url,
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