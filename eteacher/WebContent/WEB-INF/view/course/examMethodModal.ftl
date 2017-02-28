<div id="examMethodModal" class="modal fade">
	<div class="modal-dialog">
		<div class="modal-content">
			<div class="modal-header">
				<button type="button" class="close" data-dismiss="modal"
					aria-label="Close">
					<span aria-hidden="true">&times;</span>
				</button>
				<h4 class="modal-title">考核方式</h4>
			</div>
			<div class="modal-body newtable">
				<div class="tabAdd">
					<input id="examMethoadInput" value="" class="tabinput" type="text" /><a
						class="tabAddbtn" id="examMethoadBtn">增加</a>
				</div>
				<div class="tabCont" id="examMethodList">
				</div>
			</div>
		</div>
	</div>
</div>
<script>
	$("#examMethoadBtn").click(function(){
		if($('#examMethoadInput').val()){
			try{
				$('.examMethodSpan').each(function(i){
				   if($(this).html() == $('#examMethoadInput').val()){
				   		throw('该考核方式已存在！');
				   }
				});
				addDictionary($('#examMethoadInput').val(),3,function(){
					$('#examMethoadInput').val("");
					initExamMethod();
				})
			}catch(err){
				alert(err);
			}
		}
	});
	
	//选择考核方式
	function selectExamMethod(tag){
		$('#examinationMode').attr('pId',$(tag).attr('pId'));
		$('#examinationMode').val($(tag).html());
	}
	
	//删除授课方式
	function delExamMethod(tag,id){
		delDictionary(id,3,function(ret){
			if(id == $('#examinationMode').attr('pId')){
				$('#examinationMode').val('');
				$('#examinationMode').removeAttr('pId');
			}
			$(tag).parent().remove();
		});
	}
</script>