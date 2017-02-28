<div id="classSelectModal" class="modal fade">
	<div class="modal-dialog">
		<div class="modal-content">
			<div class="modal-header">
				<button type="button" class="close" data-dismiss="modal"
					aria-label="Close">
					<span aria-hidden="true">&times;</span>
				</button>
				<h4 class="modal-title">选择班级</h4>
			</div>
			<div class="modal-body newtable">
				<div class="tabAdd">
					<select id="grade" name="termId" style="width: 25%;">
						<option value="">----年级----</option>
						<option value="2014">2014</option>
						<option value="2015">2015</option>
						<option value="2016">2016</option>
						<option value="2017">2017</option>
						<option value="2018">2018</option>
						<option value="2019">2019</option>
						</select>
					    <select id="specialty1" onchange="loadMajorData($('#specialty2'),this.value);loadMajorData($('#specialty3'));">
                            <option value="">--请选择专业--</option>
                        </select>    
                        <select id="specialty2" onchange="loadMajorData($('#specialty3'),this.value);" style="margin-top:5px">
                            <option value="">--请选择专业--</option>
                        </select>          
                        <select id="specialty3" name="specialty" style="margin-top:5px">
                            <option value="">--请选择专业--</option>
                        </select>  
					<select id="degree" name="termId" style="width: 25%;">
						<option value="">----请选择学制----</option>
						<option value="研究生">研究生</option>
						<option value="本科">本科</option>
						<option value="专科">专科</option>
						<option value="中专">中专</option>
						<option value="技校">技校</option>
					</select>
					<select id="classNum" name="termId" style="width: 25%;">
						<option value="">----请选择班级----</option>
						<option value="1">1</option>
						<option value="2">2</option>
						<option value="3">3</option>
						<option value="4">4</option>
						<option value="5">5</option>
						<option value="6">6</option>
					</select>
					<a id="classAddBtn" class="tabAddbtn">增加</a>
				</div>
				<div class="tabCont" id="classlist">

				</div>
			</div>
		</div>
	</div>
</div>
<script>
	$('#classAddBtn').click(function(){
		try{
			if($('#grade').val() == ''){
				throw("年级不能为空！");
			}
			if($('#specialty3').val() == ''){
				throw("专业不能为空！");
			}
			if($('#degree').val() == ''){
				throw("学制不能为空！");
			}
			if($('#classNum').val() == ''){
				throw("班级不能为空！");
			}
			$('.classSpan').each(function(i){
				if($(this).attr('grade') == $('#grade').val() 
				&& $(this).attr('degree') == $('#degree').val() 
				&& $(this).attr('majorId') == $('#specialty3').val()
				&& $(this).attr('className') == $('#specialty3').find("option:selected").text()+$('#degree').val()+$('#classNum').val()+'班'){
					throw("该班级已存在");
				}
			});
			var temp = new Array();
			var item = {};
			item.grade = $('#grade').val();
			item.degree  = $('#degree').val();
			item.majorId = $('#specialty3').val();
			item.className = $('#specialty3').find("option:selected").text()+$('#degree').val()+$('#classNum').val()+'班';
			temp.push(item);
			$('#classlist').append(classDot(temp));
			refreshResult();
		}catch(err){
			if(err){
				alert(err);
			}
		}
	});
	//删除班级
	function removeItem(tag){
		$(tag).parent().remove();
		refreshResult();
	}
	
	//更新主页面
	function refreshResult(){
		var temps = new Array();
		$('.classSpan').each(function(i){
			var item = {};
			item.grade = $(this).attr('grade');
			item.degree  = $(this).attr('degree');
			item.majorId = $(this).attr('majorId');
			item.className = $(this).attr('className');
			temps.push(item);
		});
		$('#classResultList').html(classResultDot(temps));
	}
	
</script>