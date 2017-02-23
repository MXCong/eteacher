/**
 * 弹出框alert、confirm的封装
 */

//引用style和js文件
if(!$.alerts){
	document.write('<link href="../js/jqueryAlerts/jquery.alerts.css" rel="stylesheet" type="text/css" />');
	document.write('<scri'+'pt type="text/java'+'script" src="../js/jqueryAlerts/jquery.alerts.js"></script>');
}
//alert
JAlert = function(message, title, callback) {
	if(title){
		$.alerts.alert("<br/><b>"+message+"</b><br/>", title, callback);
	}
	else{
		$.alerts.alert("<br/><b>"+message+"</b><br/>", "提示", callback);
	}
}
//confirm
JConfirm = function(message, title, callback) {
	$.alerts.confirm("<br/><b>"+message+"</b><br/>", title, callback);
}
//prompt
JPrompt = function(message, value, title, callback) {
	$.alerts.prompt("<br/><b>"+message+"</b><br/>", value, title, callback);
}
