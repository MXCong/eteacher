<div id="questionTypeModal" class="modal fade">
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
					<input id="examMethoadInput" value="" class="tabinput" type="text" />
					<a class="tabAddbtn" id="examMethoadBtn">增加</a>
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
				   		throw('该问题分类已存在！');
				   }
				});
				addQuestionType($('#examMethoadInput').val(),function(){
					$('#examMethoadInput').val("");
					initQuestionType();
				})
			}catch(err){
				alert(err);
			}
		}
	});
	
	//选择考核方式
	function selectExamMethod(tag){
		$('#questionType').attr('pId',$(tag).attr('pId'));
		$('#questionType').val($(tag).html());
		var typeId = $('#questionType').attr('pId');
		initKnowledgePoint(typeId);
	}
	
	//删除授课方式
	function delExamMethod(tag,id,questions){
		if(questions > 0){
			if(confirm("确定删除？删除后该类别下的问题将置为'未分类'状态...")){
				delQuestionType(id , questions , function(ret){
					if(id == $('#questionType').attr('pId')){
						$('#questionType').val('');
						$('#questionType').removeAttr('pId');
					}
					$(tag).parent().remove();
				});
			}
		}else{
			delQuestionType(id,function(ret){
				if(id == $('#questionType').attr('pId')){
					$('#questionType').val('');
					$('#questionType').removeAttr('pId');
				}
				$(tag).parent().remove();
			});
		}
	}
</script>