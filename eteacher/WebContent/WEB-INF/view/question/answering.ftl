<div id="answerModal" class="modal fade">
	<div class="modal-dialog">
		<div class="modal-content">
			<div class="modal-header">
				<h4 class="modal-title">答题中..</h4>
			</div>
			<div class="modal-body newtable">
				<div class="topDiv">
					<span id="timer">12:00</span>
				</div>
				<div class="bottomDiv">
					<div class="bottom">
						<div id="stopBtn" class="btn">结束答题</div>
						<span class="hint">注意：结束答题前请勿关闭窗口</span>
					</div>
				</div>
			</div>
		</div>
	</div>
</div>
<script>
    var timer = null;
    var count = 0;
    /**
     *开始计时
     */
    function startTimer() {
        count = count + 1
        timer = setTimeout("startTimer()", 1000);
        var m = parseInt(count / 60);
        var s = count % 60;
        if (m < 10) {
            m = '0' + m;
        }
        if (s < 10) {
            s = '0' + s;
        }
        $('#timer').html(m + ':' + s);
    }

    $('#stopBtn').click(function(){
		var r=confirm("您确认要结束练习吗？");
		if (r==true){
	        if (timer) {
	            clearTimeout(timer);
	            stopAnswer();
	        }
		}
    });

    /**
     *关闭答题
     */
    function stopAnswer() {
        var params = {};
        params.recordId = recordId;
        $httpUtils.post('teacher/stopQuestion', params, function(res) {
        	$('#answerModal').modal('hide');
            alert("答题已结束,去看答题结果吧！");
            getResult();
	        $('#answerResultModal').modal();
        });
    }
</script>