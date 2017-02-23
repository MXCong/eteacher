(function(window) {
	var p = {};
	//localStorage方法封装 本地存储
	/**
	 * 配置文件中保存数据
	 * @param {Object} k 名称
	 * @param {Object} v 内容
	 */
	p.savePref = function(k, v) {
		if (!k) {
			alert("key不能为空！");
			return;
		}
		if (!v) {
			alert("value不能为空！");
			return;
		}
		localStorage.setItem(k, v);
	}
	/**
	 * 获取配置文件中的数据
	 * @param {Object} k 名称
	 */
	p.getPref = function(k) {
		if (!k) {
			alert("key不能为空！");
			return;
		}
		
		return localStorage.getItem(k);
	}

	/**
	 * 删除配置文件中的对应的数据
	 * @param {Object} k 名称
	 */
	p.remValue = function(k) {
		if (!k) {
			alert("key不能为空！");
			return;
		}
		localStorage.removeItem(k);
	}
//	-----------------------------------------------------------
	/**
	 * sessionStorage方法封装 临时存储，关闭标签页后清除数据。用于页面间参数传递
	 * 
	 */
	// 存数据
	p.transfer = function(k, v) {
		sessionStorage.setItem(k, v);
	}
	// 取数据
	p.receive = function(k) {
		return sessionStorage.getItem(k);
	}
	
	window.$PrefsUtils = p;
})(window);
