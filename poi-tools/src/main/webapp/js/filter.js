/**
 * Created by 钱斌 on 2016/8/17.
 */
taskModule.filter("codeFilter", function () {
    return function (input,object) {
        var out =new Array();
        var codes = input.split("|");
        for(var code in codes){
            var name = object[codes[code]];
            out.push(name);
        }
        return input.length !=0 ? out.join("|") : "未选择类别";
    }
});
/**
 * TOP N 查询类别 格式化
 * @param  {[json]} obj
 * @param  {[Object]} object
 * @param  {[String]} type  区分html title属性
 * @return {[String]}  内容
 */
taskModule.filter("format", ['$sce', '$filter',function ($sce,$filter) {
　　return function (obj,object,type) {

        // 范围:全国/全市/all,日期:工作日,小时:9~11/14~16 
		var rank=JSON.parse(obj.rank),
		province=rank.province==="all"?"全国":rank.province,
		city=rank.city==="all"?"全市":rank.city,
		district=rank.district==="all"?"全区":rank.district,
		dateType=rank.day_type==="w"?"工作日":rank.day_type==="e"?"周六日":"全部",
		str_type=$filter('codeFilter')(obj.poi_types,object),
		hour_range=",小时:",
		type=type==="title"?"\n":"<br />";
		for(var i=0,len=rank.hour_range.length;i<len;i++){
			var hour=rank.hour_range[i];
			hour_range+=hour.start_hour+"~"+hour.end_hour+"/";
		}
		var str_rank=type+"范围:"+province+"/"+city+"/"+district+",日期:"+dateType+hour_range,
        str_all=str_type+str_rank;
        return $sce.trustAsHtml(str_all);
　　};
}]);