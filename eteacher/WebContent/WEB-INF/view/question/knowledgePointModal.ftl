<div id="knowledgePointModal" class="modal fade">
	<div class="modal-dialog">
		<div class="modal-content">
			<div class="modal-header">
				<button type="button" class="close" data-dismiss="modal"
					aria-label="Close">
					<span aria-hidden="true">&times;</span>
				</button>
				<h4 class="modal-title">知识点</h4>
			</div>
			<div class="modal-body newtable">
				<div class="tabAdd">
					<input id="teachMethoadInput" value="" class="tabinput" type="text" /><a
						class="tabAddbtn" id="teachMethodBtn">增加</a>
				</div>
				<div class="tabCont" id="teachMethodList">
				</div>
			</div>
		</div>
	</div>
</div>
<script>
	$("#teachMethodBtn").click(function(){
		if($('#teachMethoadInput').val()){
			try{
				$('.teachMethodSpan').each(function(i){
				   if($(this).html() == $('#teachMethoadInput').val()){
				   		throw('该知识点已存在！');
				   }
				});
				addKnowledgePoint($('#teachMethoadInput').val(),function(){
					$('#teachMethoadInput').val("");
					initKnowledgePoint();
				})
			}catch(err){
				alert(err);
			}
		}
	});
	//选择授课方式
	function selectTeachMethod(tag){
		$('#knowledgePoint').attr('pId',$(tag).attr('pId'));
		$('#knowledgePoint').val($(tag).html());
	}
	//删除授课方式
	function delTeachMethod(tag,id){
		delknowledgePoint(id,function(ret){
			if(id == $('#teachingMethod').attr('pId')){
				$('#knowledgePoint').val('');
				$('#knowledgePoint').removeAttr('pId');
			}
			$(tag).parent().remove();
		});
	}
</script>