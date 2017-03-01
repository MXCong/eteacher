<div id="courseScorekModal" class="modal fade">
	<div class="modal-dialog">
		<div class="modal-content">
			<div class="modal-header">
				<button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>
				<h4 class="modal-title">成绩组成</h4>
			</div>
			<form  method="post" action="addNormalScore">
				<div class="modal-body">
					<table width="100%">
						<thead>
							<tr>
								<td align="center">类型</td>
								<td align="center">比例</td>
								<td align="center">计分方式</td>
								<td align="center">操作</td>
							</tr>
						</thead>
						<tbody id="scoreDiv">
							
						</tbody>
					</table>      
				</div>
				<div class="modal-footer">
					<button type="button" class="btn btn-default" data-dismiss="modal">关 闭</button>
					<button type="button" class="btn btn-default" onclick="saveScore();">保 存</button>
				</div>
			</form> 
		</div>
	</div>
</div>
<script>
	var courseScoreArr = [{scoreName:'平时'},{scoreName:'期中'},{scoreName:'期末'}];
	var typeArr = [{id:1,type:'定值'},{id:2,type:'均值'}];
	var pointArr = [];
	//initScore();
	
	//打开时初始化数据
	$('#courseScorekModal').on('show.bs.modal', function (e) {
		$.each(courseScoreArr,function(i,n){
			addScore(n.scoreName,n.scorePercent ,n.status);
		});
	})
	//关闭后清空表单
	$('#courseScorekModal').on('hidden.bs.modal', function (e) {
		$('#scoreDiv').html('');
	})
	function addScore(sname,spercent,sstatus){
		var scoreName = sname?sname:'';
		var scorePercent = spercent?spercent:'';
		var status = sstatus?sstatus:'';
		var html =  '<tr>'+
		'	<td ><div class="btn-group" id="toolbar" style="float:left;">'+
		'		<input value="'+scoreName+'" class="mess-control scoreName" style="width:110px; margin-top:5px;" />'+
		'	</div></td>';

		html+=	
		'	<td>'+
		'		<input value="'+scorePercent+'" name="" class="mess-control scorePercent" data-percent="30" data-csid="" style="width:100px; margin-top:5px" />'+
		'	</td>'+
		'	<td>'+
		'		<select class="mess-control status" style="width:110px; margin-top:5px">';
		$.each(typeArr,function(i,n){
			html+='<option value='+n.id;
			if(n.id && status){
				if(n.id == status){
					html+=' selected = "selected"';
				}	
			}
			html+='>'+n.type+'</option>';
		});					
		html+=	 	'		</select>'+
		'	</td>'+
		'	<td>'+
		'	<div class="btn-group" id="toolbar" style="float:left;">'+
		'		<button type="button" title="新增" class="btn btn-default" style="height:30px;" onclick="addScore();">'+
		'			<i class="glyphicon glyphicon-plus"></i>'+
		'	</button>'+
		'	<button type="button" title="新增" class="btn btn-default" style="height:30px;" onclick="delScore(this);">'+
		'			<i class="glyphicon glyphicon-remove"></i>'+
		'	</button>'+
		'	</div>'+
		'	</td>'+
		'</tr>';
		$('#scoreDiv').append(html);
	}
	function delScore(btn){
		if($('#scoreDiv').children().length > 1){
			$(btn).parent().parent().parent().remove();
		}
		else{
			$(btn).parent().parent().children(':input').val('');
		}
	}
	function saveScore(){
		var boo = true;
		$('.scoreName,.scorePercent').each(function(i){
			if(!$(this).val()){
				boo = false;
				alert("类型和比例不可为空！");
				return false;
			}
		});
		var totalPercent = 0;
		if(boo){
			$('.scorePercent').each(function(i){
				totalPercent += Number($(this).val());
			});
			if(totalPercent!=100){
				alert("成绩比例之和必须为100");
				boo = false;
			}
		}
		if(boo)	{
			courseScoreArr = [];
			var record = null;
			$('#scoreDiv').children().each(function(i){
				record = {};
				record.scoreName = $(this).find('.scoreName').val();
				record.scorePercent = $(this).find('.scorePercent').val();
				record.status = $(this).find('.status').val();
				courseScoreArr.push(record);
			});
			console.log(courseScoreArr);
			initScore();
			$('#courseScorekModal').modal('hide');
		}	
		return false;
		
	}  
	function initScore(){
		var html = '';
		$.each(courseScoreArr,function(i,n){
			var value = n.scorePercent?n.scorePercent:'';
			html += '<span>' + n.scoreName +'&nbsp;&nbsp;'+ value+' %&nbsp;&nbsp;&nbsp;&nbsp</span>';
		});
		$('#courseScore').html(html);
	}
	
</script> 
