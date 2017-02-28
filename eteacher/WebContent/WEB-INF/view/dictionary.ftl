<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<link href="../css/base.css" rel="stylesheet" type="text/css">
<script type="text/javascript" src="../js/jquery.min.js"></script>
<head>
<script type="text/javascript">
	/**
	function selectData(id,value,pid){
		$('#'+pid,parent.document).val(value);
		$('#'+pid,parent.document).attr("name",id);
		parent.$.modal.close();
	}
	
	//删除数据
	function delData(dtype,did){
		$.post('../dictionary/viewDictionaryDel',{type:dtype,id:did},function(data){
			if(data){
				window.location.reload();
			}else{
				alert("发生错误，删除数据失败");
			} 
		});
		}
		
		//增加数据
		function addDate(dtype){
			var dvalue = $('#inputeData').val();
			$.post('../dictionary/viewDictionaryAdd',{type:dtype,value:dvalue},function(data){
			if(data){
				window.location.reload();
			}else{
				alert("发生错误，增加数据失败");
			}
		});
		}
		**/
</script>
</head>
<body style="background:#fff;">

	<div class="newtable">
		<div class="tabAdd">
        	<input id="inputeData" value="" class="tabinput" type="text" /><a class="tabAddbtn" onclick="addDate(${type})">增加</a>
        </div>
    	<div class="tabCont">
    	<!-- <#list titleList as dictionarys> 
			<div class="onetab">
				<span id="titleSel" onclick="selectData('${dictionarys['id']}','${dictionarys['content']}','${pid}')">${dictionarys['content']}</span>
				<a class="delet-img" onclick="delData(${type},'${dictionarys['id']}')" href="#" title="删除"></a>
			</div>
		</#list>
		-->
        </div>
  	</div>
</body>
</html>