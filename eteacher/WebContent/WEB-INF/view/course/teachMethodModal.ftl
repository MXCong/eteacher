<div id="teachMethodModal" class="modal fade">
	<div class="modal-dialog">
		<div class="modal-content">
			<div class="modal-header">
				<button type="button" class="close" data-dismiss="modal"
					aria-label="Close">
					<span aria-hidden="true">&times;</span>
				</button>
				<h4 class="modal-title">授课方式</h4>
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
				   		throw('该授课方式已存在！');
				   }
				});
				addDictionary($('#teachMethoadInput').val(),2,function(){
					$('#teachMethoadInput').val("");
					initTeachMethod();
				})
			}catch(err){
				alert(err);
			}
		}
	});
	//选择授课方式
	function selectTeachMethod(tag){
		$('#teachingMethod').attr('pId',$(tag).attr('pId'));
		$('#teachingMethod').val($(tag).html());
	}
	//删除授课方式
	function delTeachMethod(tag,id){
		delDictionary(id,2,function(ret){
			if(id == $('#teachingMethod').attr('pId')){
				$('#teachingMethod').val('');
				$('#teachingMethod').removeAttr('pId');
			}
			$(tag).parent().remove();
		});
	}
</script>