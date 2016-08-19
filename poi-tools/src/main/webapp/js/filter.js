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