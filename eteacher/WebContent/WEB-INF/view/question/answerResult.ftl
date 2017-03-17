<div id="answerResultModal" class="modal fade">
	<div class="modal-dialog">
		<div class="modal-content">
			<div class="modal-header">
                <button type="button" class="close" data-dismiss="modal"
                    aria-label="Close">
                    <span aria-hidden="true">&times;</span>
                </button>
				<h4 class="modal-title">答题结果</h4>
			</div>
			<div class="modal-body newtable">
                <div class="quest" onclick="openDetail();">
                    <span class="qlogo"> 问 </span>
                    <span class="qtitle"> </span>
                </div>
                <div class="quest file">
                    <img />
                </div>
                <div class='quest'>
                    <span class="result"></span>
                </div>
                <div id="test" style="width: 100%;height:250px;"></div>
			</div>
		</div>
	</div>
</div>
<script>
        /*获取答题结果*/
        function getResult() {
            var params = {};
            params.recordId = recordId;
            $httpUtils.post('teacher/questionResult', params, function(ret) {
                if (ret) {
                    if (ret.content) {
                        $('.qtitle').html(ret.content);
                    }
                    if (ret.questionId) {
                        questionId = ret.questionId;
                    }
                    if (ret.options && ret.options.length > 0) {
                        var opts = new Array();
                        for (var i = 0; i < ret.options.length; i++) {
                            if (ret.options[i].flag == 1) {
                                $('.result').html('正确答案' + ret.options[i].optionType + ":" + ret.options[i].optionValue);
                            }
                            var temp = {};
                            temp.name = ret.options[i].optionType + ": " + ret.options[i].person + "人";
                            temp.value = ret.options[i].person;
                            opts.push(temp);
                        }
                        initChart(opts);
                    }
                    if(ret.files && ret.files.length > 0){
                        $('.file').html(fileDot(ret.files));
                        $('.file').show();
                    }else{
                        $('.file').hide();
                    }
                }
            });
        }

        /**
         *初始化饼状图
         */
        function initChart(datas) {
            // 基于准备好的dom，初始化echarts实例
            /** {
             value : 135,
             name : 'D 135人'
             }**/
            var myChart = echarts.init(document.getElementById('test'));
            var option = {
                tooltip : {
                    trigger : 'item',
                    formatter : "{a} <br/>{b} : {c} ({d}%)"
                },
                series : [{
                    name : '访问来源',
                    type : 'pie',
                    radius : '55%',
                    center : ['50%', '60%'],
                    data : datas,
                    itemStyle : {
                        emphasis : {
                            shadowBlur : 10,
                            shadowOffsetX : 0,
                            shadowColor : 'rgba(0, 0, 0, 0.5)'
                        }
                    }
                }]
            };
            myChart.setOption(option);
        }
</script>