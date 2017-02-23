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
			url : '../remote/' + url,
			type : 'POST',
			dataType : 'json',
			data : params,
			complete : function(XMLHttpRequest, textStatus) {
				switch (XMLHttpRequest.status) {
				/*
				 * case 200: if (fnSuc) { fnSuc(XMLHttpRequest.data); } break;
				 */
				case 900:
					// 请求超时
					if (fnErr) {
						fnErr(XMLHttpRequest.msg);
					}
					break;
				case 406:
					// 非法请求
					if (fnErr) {
						fnErr(XMLHttpRequest.msg);
					}
					break;
				case 401:
					// 用户不存在
					goLogin();
					if (fnErr) {
						fnErr(XMLHttpRequest.msg);
					}
					break;
				case 400:
					// 请求失败
					if (fnErr) {
						fnErr(XMLHttpRequest.msg);
					}
					break;
				case 201:
					// token过期
					goLogin();
					if (fnErr) {
						fnErr(XMLHttpRequest.msg);
					}
					break;
				}
			},
			error : function(XMLHttpRequest, textStatus, errorThrown) {
				// TODO
			},
			success : function(data) {
				fnSuc(data.data);
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
			url : '../remote/' + url,
			type : 'POST',
			dataType : 'json',
			data : params,
			complete : function(XMLHttpRequest, textStatus) {
				var status = XMLHttpRequest.status;
				switch (status) {
				/*
				 * case 200: if (fnSuc) { // 转换Json数据为javascript对象 eval("var
				 * objResults =" + XMLHttpRequest.responseText);
				 * alert("--:"+objResults.Results); var displaytext = ""; for
				 * (var i=0; i < objResults.Results.computer.length; i++) {
				 * displaytext += objResults.Results.computer[i].Manufacturer + " " +
				 * objResults.Results.computer[i].Model + ": $" +
				 * objResults.Results.computer[i].Price + "<br>"; }
				 * fnSuc(displaytext); } break;
				 */
				case 900:
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
				case 401:
					// 用户不存在
					goLogin();
					if (fnErr) {
						fnErr(XMLHttpRequest.msg);
					}
					break;
				case 400:
					// 请求失败
					if (fnErr) {
						fnErr(XMLHttpRequest.msg);
					}
					break;
				case 201:
					// token过期
					goLogin();
					if (fnErr) {
						fnErr(XMLHttpRequest.msg);
					}
					break;
				}
			},
			error : function(XMLHttpRequest, textStatus, errorThrown) {
				// TODO
			},
			success : function(data) {
				fnSuc(data);
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
	window.$httpUtils = h;
})(window);