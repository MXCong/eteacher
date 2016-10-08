<div id="courseScorekModal" class="modal fade">
  <div class="modal-dialog">
    <div class="modal-content">
      <div class="modal-header">
        <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>
        <h4 class="modal-title">课堂提问</h4>
      </div>
      <form id="courseScoreForm" method="post" action="addNormalScore">
      <input type="hidden" id="courseId" name="courseId" />
      <input type="hidden" id="stuId" name="stuId" />
      <div class="modal-body">
            <div class="message-group">
                <div class="message-left">分数：</div>
                <div class="message-right">
                    <input id="scoreNumber" name="scoreNumber" style="width:60%" type="text" class="mess-control" value="" />
                </div>                   	
            </div>
      </div>
      <div class="modal-footer">
        <button type="button" class="btn btn-default" data-dismiss="modal">关 闭</button>
        <button type="submit" class="btn btn-default">保 存</button>
      </div>
      </form> 
    </div><!-- /.modal-content -->
  </div><!-- /.modal-dialog -->
</div><!-- /.modal -->
<script type="text/javascript" src="${context}/js/turingLib/validator.js"></script>
<script>
	//打开时初始化数据
	$('#courseScorekModal').on('show.bs.modal', function (e) {
		$('#courseId').val(courseId);
		$('#courseScoreForm .input_error').removeClass('input_error');
	})
	//关闭后清空表单
	$('#courseScorekModal').on('hidden.bs.modal', function (e) {
	  	document.getElementById("courseScoreForm").reset();
	})
	$(function(){
		$('#courseScoreForm').validatorInit(function(){
			$('#courseScorekModal').modal('hide');
		});
		$('#scoreNumber').validatorSZ('分数');
	});
</script>
