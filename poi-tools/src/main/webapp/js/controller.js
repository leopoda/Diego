/**
 * Created by 钱斌 on 2016/8/12.
 */
var taskModule = angular.module('taskModule', []);

/**
 * 导航栏切换
 * @type {*[]}
 */
taskModule.controller('TabsCtrl', function ($scope, $location, $rootScope) {
    $rootScope.queryurl = "http://54.222.253.178:8080/poi-tools/jobs";
    $rootScope.submiturl = "http://54.222.253.178:8080/poi-tools/submit"
    $rootScope.downloadurl = "http://54.222.253.178:8080/poi-tools/jobresult/";
    $rootScope.deleteurl = "http://54.222.253.178:8080/poi-tools/jobdelete/";
    $scope.tabs = [{
        title: '社区街道',
        sref: 'area'
    }, {
        title: '周边 POI',
        sref: 'poi'
    }, {
        title: '最近小区',
        sref: 'cell'
    }];
    $scope.currentTab = $location.path().substr(1);
    $scope.search_content = $location.search()["query"];
    $scope.isActiveTab = function (tab) {
        return tab == $scope.currentTab;
    }
    $scope.queryTab = function (search_content) {
        $location.search({
            query: search_content
        });
        window.location.reload();
    };

});

/**
 * 行政区域查询
 */
taskModule.controller('AreaCtrl', function ($scope, $rootScope, $http, $interval, $state, $stateParams, $location) {
    $scope.selPage = 1;
    $scope.search_content = $location.search()["query"] == undefined ? "" : $location.search()["query"];
    $scope.getAreas = function (selPage) {
        var params = {
            offset: 10,
            page: selPage,
            for: "geo",
            search: $scope.search_content
        };
        $http.get($rootScope.queryurl, {
            params: params
        }).success(function (response) {
            //数据源
            $scope.items = response.jobs;
            $scope.pages = response.page_total; //分页总数
            $scope.selPage = response.page_num;
            $scope.count = response.count;
            //不能小于1大于最大
            if (selPage < 1 || selPage > $scope.pages) return;
            //最多显示分页数5
            var newpageList = [];
            if (selPage > 2) {
                for (var i = (selPage - 3); i < ((selPage + 2) > $scope.pages ? $scope.pages : (selPage + 2)); i++) {
                    newpageList.push(i + 1);
                }
            } else {
                $scope.newPages = $scope.pages > 5 ? 5 : $scope.pages;
                $scope.pageList = [];
                //分页要repeat的数组
                for (var i = 0; i < $scope.newPages; i++) {
                    newpageList.push(i + 1);
                }
            }
            $scope.pageList = newpageList;
            $scope.selPage = selPage;
            $scope.isActivePage(selPage);
        });
    };
    //打印当前选中页索引
    $scope.selectPage = function (selPage) {
        $scope.getAreas(selPage);
    };
    //设置当前选中页样式
    $scope.isActivePage = function (page) {
        return $scope.selPage == page;
    };
    //设置当前选中页样式
    $scope.isActivePage = function (page) {
        return $scope.selPage == page;
    };
    //上一页
    $scope.Previous = function () {
        if ($scope.selPage == 1) {
            return;
        }
        $scope.selectPage($scope.selPage - 1);
    }
    //下一页
    $scope.Next = function () {
        if ($scope.selPage == $scope.pages) {
            return;
        }
        $scope.selectPage($scope.selPage + 1);
    };
    $scope.delete = function (jobid) {
        $http({
            method: 'DELETE',
            url: $rootScope.deleteurl+jobid
        }).success(function(data,status,config,headers){
            window.location.reload();
        });
    }
    $scope.selectPage(1);
});

/**
 * poi查询
 */
taskModule.controller('PoiCtrl', function ($scope, $rootScope, $http, $interval, $state, $stateParams, $location) {
    $scope.selPage = 1;
    $scope.search_content = $location.search()["query"] == undefined ? "" : $location.search()["query"];
    $scope.getPois = function (selPage) {
        var params = {
            offset: 10,
            page: selPage,
            for: "poi",
            search: $scope.search_content
        };
        $http.get($rootScope.queryurl, {
            params: params
        }).success(function (response) {
            //alert($rootScope.poicode);
            //数据源
            $scope.items = response.jobs;
            $scope.pages = response.page_total; //分页总数
            $scope.selPage = response.page_num;
            $scope.count = response.count;
            //不能小于1大于最大
            if (selPage < 1 || selPage > $scope.pages) return;
            //最多显示分页数5
            var newpageList = [];
            if (selPage > 2) {
                for (var i = (selPage - 3); i < ((selPage + 2) > $scope.pages ? $scope.pages : (selPage + 2)); i++) {
                    newpageList.push(i + 1);
                }
            } else {
                $scope.newPages = $scope.pages > 5 ? 5 : $scope.pages;
                $scope.pageList = [];
                //分页要repeat的数组
                for (var i = 0; i < $scope.newPages; i++) {
                    newpageList.push(i + 1);
                }
            }
            $scope.pageList = newpageList;
            $scope.selPage = selPage;
            $scope.isActivePage(selPage);
        });
    };
    //打印当前选中页索引
    $scope.selectPage = function (selPage) {
        $scope.getPois(selPage);
    };
    //设置当前选中页样式
    $scope.isActivePage = function (page) {
        return $scope.selPage == page;
    };
    //设置当前选中页样式
    $scope.isActivePage = function (page) {
        return $scope.selPage == page;
    };
    //上一页
    $scope.Previous = function () {
        if ($scope.selPage == 1) {
            return;
        }
        $scope.selectPage($scope.selPage - 1);
    }
    //下一页
    $scope.Next = function () {
        if ($scope.selPage == $scope.pages) {
            return;
        }
        $scope.selectPage($scope.selPage + 1);
    };
    $scope.delete = function (jobid) {
        $http({
            method: 'DELETE',
            url: $rootScope.deleteurl+jobid
        }).success(function(data,status,config,headers){
            window.location.reload();
        });
    }
    $scope.selectPage(1);
});

/**
 * 行政区域查询
 */
taskModule.controller('CellCtrl', function ($scope, $rootScope, $http, $interval, $state, $stateParams, $location) {
    $scope.selPage = 1;
    $scope.search_content = $location.search()["query"] == undefined ? "" : $location.search()["query"];
    $scope.getAreas = function (selPage) {
        var params = {
            offset: 10,
            page: selPage,
            for: "cell",
            search: $scope.search_content
        };
        $http.get($rootScope.queryurl, {
            params: params
        }).success(function (response) {
            //数据源
            $scope.items = response.jobs;
            $scope.pages = response.page_total; //分页总数
            $scope.selPage = response.page_num;
            $scope.count = response.count;
            //不能小于1大于最大
            if (selPage < 1 || selPage > $scope.pages) return;
            //最多显示分页数5
            var newpageList = [];
            if (selPage > 2) {
                for (var i = (selPage - 3); i < ((selPage + 2) > $scope.pages ? $scope.pages : (selPage + 2)); i++) {
                    newpageList.push(i + 1);
                }
            } else {
                $scope.newPages = $scope.pages > 5 ? 5 : $scope.pages;
                $scope.pageList = [];
                //分页要repeat的数组
                for (var i = 0; i < $scope.newPages; i++) {
                    newpageList.push(i + 1);
                }
            }
            $scope.pageList = newpageList;
            $scope.selPage = selPage;
            $scope.isActivePage(selPage);
        });
    };
    //打印当前选中页索引
    $scope.selectPage = function (selPage) {
        $scope.getAreas(selPage);
    };
    //设置当前选中页样式
    $scope.isActivePage = function (page) {
        return $scope.selPage == page;
    };
    //设置当前选中页样式
    $scope.isActivePage = function (page) {
        return $scope.selPage == page;
    };
    //上一页
    $scope.Previous = function () {
        if ($scope.selPage == 1) {
            return;
        }
        $scope.selectPage($scope.selPage - 1);
    }
    //下一页
    $scope.Next = function () {
        if ($scope.selPage == $scope.pages) {
            return;
        }
        $scope.selectPage($scope.selPage + 1);
    };
    $scope.delete = function (jobid) {
        $http({
            method: 'DELETE',
            url: $rootScope.deleteurl+jobid
        }).success(function(data,status,config,headers){
            window.location.reload();
        });
    }
    $scope.selectPage(1);
});

taskModule.controller('DataCtrl', function ($scope, $rootScope, $location, $http) {
    $http.get("resource/gd.json")
        .success(function (data) {
            $rootScope.poicode = data;
        });
});

taskModule.controller('FormCtrl', function ($scope, $rootScope, $http, $window, $state, $location) {
    $scope.taskInfo = {import: "gp", cood: "gps", for: "geo"};
    function getTree() {
        var data = [{
            "text": "汽车服务",
            "selectable": false,
            "nodes": [{
                "text": "汽车服务相关",
                "selectable": false,
                "nodes": [{"text": "汽车服务相关", "code": "010000"}]
            }, {
                "text": "加油站",
                "selectable": false,
                "nodes": [{"text": "加油站", "code": "010100"}, {"text": "中国石化", "code": "010101"}, {
                    "text": "中国石油",
                    "code": "010102"
                }, {"text": "壳牌", "code": "010103"}, {"text": "美孚", "code": "010104"}, {
                    "text": "加德士",
                    "code": "010105"
                }, {"text": "东方", "code": "010107"}, {"text": "中石油碧辟", "code": "010108"}, {
                    "text": "中石化碧辟",
                    "code": "010109"
                }, {"text": "道达尔", "code": "010110"}, {"text": "埃索", "code": "010111"}, {
                    "text": "中化道达尔",
                    "code": "010112"
                }]
            }, {"text": "其它能源站", "selectable": false, "nodes": [{"text": "其它能源站", "code": "010200"}]}, {
                "text": "加气站",
                "selectable": false,
                "nodes": [{"text": "加气站", "code": "010300"}]
            }, {
                "text": "汽车养护/装饰",
                "selectable": false,
                "nodes": [{"text": "汽车养护", "code": "010400"}, {"text": "加水站", "code": "010401"}]
            }, {"text": "洗车场", "selectable": false, "nodes": [{"text": "洗车场", "code": "010500"}]}, {
                "text": "汽车俱乐部",
                "selectable": false,
                "nodes": [{"text": "汽车俱乐部", "code": "010600"}]
            }, {"text": "汽车救援", "selectable": false, "nodes": [{"text": "汽车救援", "code": "010700"}]}, {
                "text": "汽车配件销售",
                "selectable": false,
                "nodes": [{"text": "汽车配件销售", "code": "010800"}]
            }, {
                "text": "汽车租赁",
                "selectable": false,
                "nodes": [{"text": "汽车租赁", "code": "010900"}, {"text": "汽车租赁还车", "code": "010901"}]
            }, {"text": "二手车交易", "selectable": false, "nodes": [{"text": "二手车交易", "code": "011000"}]}, {
                "text": "充电站",
                "selectable": false,
                "nodes": [{"text": "充电站", "code": "011100"}]
            }]
        }, {
            "text": "汽车销售",
            "selectable": false,
            "nodes": [{
                "text": "汽车销售",
                "selectable": false,
                "nodes": [{"text": "汽车销售", "code": "020000"}]
            }, {
                "text": "大众特约销售",
                "selectable": false,
                "nodes": [{"text": "大众销售", "code": "020100"}, {"text": "上海大众销售", "code": "020101"}, {
                    "text": "一汽-大众销售",
                    "code": "020102"
                }, {"text": "斯柯达销售", "code": "020103"}, {"text": "进口大众销售", "code": "020104"}, {
                    "text": "宾利销售",
                    "code": "020105"
                }, {"text": "兰博基尼销售", "code": "020106"}]
            }, {
                "text": "本田特约销售",
                "selectable": false,
                "nodes": [{"text": "本田销售", "code": "020200"}, {"text": "广汽本田销售", "code": "020201"}, {
                    "text": "东风本田销售",
                    "code": "020202"
                }, {"text": "本田讴歌销售", "code": "020203"}]
            }, {
                "text": "奥迪特约销售",
                "selectable": false,
                "nodes": [{"text": "奥迪销售", "code": "020300"}, {"text": "一汽-大众奥迪销售", "code": "020301"}]
            }, {
                "text": "通用特约销售",
                "selectable": false,
                "nodes": [{"text": "通用销售", "code": "020400"}, {"text": "凯迪拉克销售", "code": "020401"}, {
                    "text": "别克销售",
                    "code": "020402"
                }, {"text": "雪佛兰销售", "code": "020403"}, {"text": "欧宝销售", "code": "020404"}, {
                    "text": "萨博销售",
                    "code": "020405"
                }, {"text": "沃克斯豪尔销售", "code": "020406"}, {"text": "土星销售", "code": "020407"}, {
                    "text": "大宇销售",
                    "code": "020408"
                }]
            }, {
                "text": "宝马特约销售",
                "selectable": false,
                "nodes": [{"text": "宝马销售", "code": "020600"}, {"text": "宝马MINI销售", "code": "020601"}, {
                    "text": "劳斯莱斯销售",
                    "code": "020602"
                }]
            }, {
                "text": "日产特约销售",
                "selectable": false,
                "nodes": [{"text": "日产销售", "code": "020700"}, {"text": "东风日产销售", "code": "020701"}, {
                    "text": "郑州日产销售",
                    "code": "020702"
                }, {"text": "英菲尼迪销售", "code": "020703"}]
            }, {
                "text": "雷诺特约销售",
                "selectable": false,
                "nodes": [{"text": "雷诺销售", "code": "020800"}]
            }, {
                "text": "梅赛德斯-奔驰特约销售",
                "selectable": false,
                "nodes": [{"text": "梅赛德斯-奔驰销售", "code": "020900"}, {"text": "迈巴赫销售", "code": "020904"}, {
                    "text": "精灵销售",
                    "code": "020905"
                }]
            }, {
                "text": "丰田特约销售",
                "selectable": false,
                "nodes": [{"text": "丰田销售", "code": "021000"}, {"text": "一汽丰田销售", "code": "021001"}, {
                    "text": "广汽丰田销售",
                    "code": "021002"
                }, {"text": "雷克萨斯销售", "code": "021003"}, {"text": "大发销售", "code": "021004"}]
            }, {
                "text": "斯巴鲁特约销售",
                "selectable": false,
                "nodes": [{"text": "斯巴鲁销售", "code": "021100"}]
            }, {
                "text": "标致雪铁龙特约销售",
                "selectable": false,
                "nodes": [{"text": "雪铁龙销售", "code": "021200"}, {"text": "东风雪铁龙销售", "code": "021201"}, {
                    "text": "东风标致销售",
                    "code": "021202"
                }, {"text": "DS销售", "code": "021203"}]
            }, {
                "text": "三菱特约销售",
                "selectable": false,
                "nodes": [{"text": "三菱销售", "code": "021300"}, {"text": "广汽三菱销售", "code": "021301"}]
            }, {
                "text": "菲亚特约销售",
                "selectable": false,
                "nodes": [{"text": "菲亚特销售", "code": "021400"}, {"text": "阿尔法-罗密欧销售", "code": "021401"}]
            }, {
                "text": "法拉利特约销售",
                "selectable": false,
                "nodes": [{"text": "法拉利销售", "code": "021500"}, {"text": "玛莎拉蒂销售", "code": "021501"}]
            }, {
                "text": "现代特约销售",
                "selectable": false,
                "nodes": [{"text": "现代销售", "code": "021600"}, {"text": "进口现代销售", "code": "021601"}, {
                    "text": "北京现代销售",
                    "code": "021602"
                }]
            }, {
                "text": "起亚特约销售",
                "selectable": false,
                "nodes": [{"text": "起亚销售", "code": "021700"}, {"text": "进口起亚销售", "code": "021701"}, {
                    "text": "东风悦达起亚销售",
                    "code": "021702"
                }]
            }, {
                "text": "福特特约销售",
                "selectable": false,
                "nodes": [{"text": "福特销售", "code": "021800"}, {"text": "马自达销售", "code": "021802"}, {
                    "text": "林肯销售",
                    "code": "021803"
                }, {"text": "水星销售", "code": "021804"}]
            }, {
                "text": "捷豹特约销售",
                "selectable": false,
                "nodes": [{"text": "捷豹销售", "code": "021900"}]
            }, {
                "text": "路虎特约销售",
                "selectable": false,
                "nodes": [{"text": "路虎销售", "code": "022000"}]
            }, {
                "text": "保时捷特约销售",
                "selectable": false,
                "nodes": [{"text": "保时捷销售", "code": "022100"}]
            }, {
                "text": "东风特约销售",
                "selectable": false,
                "nodes": [{"text": "东风销售", "code": "022200"}]
            }, {
                "text": "吉利特约销售",
                "selectable": false,
                "nodes": [{"text": "吉利销售", "code": "022300"}, {"text": "沃尔沃销售", "code": "022301"}]
            }, {
                "text": "奇瑞特约销售",
                "selectable": false,
                "nodes": [{"text": "奇瑞销售", "code": "022400"}]
            }, {
                "text": "克莱斯勒特约销售",
                "selectable": false,
                "nodes": [{"text": "克莱斯勒销售", "code": "022500"}, {"text": "吉普销售", "code": "022501"}, {
                    "text": "道奇销售",
                    "code": "022502"
                }]
            }, {"text": "荣威销售", "selectable": false, "nodes": [{"text": "荣威销售", "code": "022600"}]}, {
                "text": "名爵销售",
                "selectable": false,
                "nodes": [{"text": "名爵销售", "code": "022700"}]
            }, {"text": "江淮销售", "selectable": false, "nodes": [{"text": "江淮销售", "code": "022800"}]}, {
                "text": "红旗销售",
                "selectable": false,
                "nodes": [{"text": "红旗销售", "code": "022900"}]
            }, {
                "text": "长安汽车销售",
                "selectable": false,
                "nodes": [{"text": "长安汽车销售", "code": "023000"}]
            }, {
                "text": "海马汽车销售",
                "selectable": false,
                "nodes": [{"text": "海马汽车销售", "code": "023100"}]
            }, {
                "text": "北京汽车销售",
                "selectable": false,
                "nodes": [{"text": "北京汽车销售", "code": "023200"}]
            }, {
                "text": "长城汽车销售",
                "selectable": false,
                "nodes": [{"text": "长城汽车销售", "code": "023300"}]
            }, {"text": "纳智捷销售", "selectable": false, "nodes": [{"text": "纳智捷销售", "code": "023400"}]}, {
                "text": "货车销售",
                "selectable": false,
                "nodes": [{"text": "货车销售", "code": "025000"}]
            }, {
                "text": "东风货车销售",
                "selectable": false,
                "nodes": [{"text": "东风货车销售", "code": "025100"}]
            }, {
                "text": "中国重汽销售",
                "selectable": false,
                "nodes": [{"text": "中国重汽销售", "code": "025200"}]
            }, {
                "text": "一汽解放销售",
                "selectable": false,
                "nodes": [{"text": "一汽解放销售", "code": "025300"}]
            }, {
                "text": "福田卡车销售",
                "selectable": false,
                "nodes": [{"text": "福田卡车销售", "code": "025400"}]
            }, {
                "text": "陕西重汽销售",
                "selectable": false,
                "nodes": [{"text": "陕西重汽销售", "code": "025500"}]
            }, {
                "text": "北奔重汽销售",
                "selectable": false,
                "nodes": [{"text": "北奔重汽销售", "code": "025600"}]
            }, {
                "text": "江淮货车销售",
                "selectable": false,
                "nodes": [{"text": "江淮货车销售", "code": "025700"}]
            }, {
                "text": "华菱星马销售",
                "selectable": false,
                "nodes": [{"text": "华菱星马销售", "code": "025800"}]
            }, {
                "text": "成都大运汽车销售",
                "selectable": false,
                "nodes": [{"text": "成都大运汽车销售", "code": "025900"}]
            }, {
                "text": "梅赛德斯-奔驰卡车销售",
                "selectable": false,
                "nodes": [{"text": "梅赛德斯-奔驰卡车销售", "code": "026000"}]
            }, {
                "text": "德国曼恩销售",
                "selectable": false,
                "nodes": [{"text": "德国曼恩销售", "code": "026100"}]
            }, {
                "text": "斯堪尼亚销售",
                "selectable": false,
                "nodes": [{"text": "斯堪尼亚销售", "code": "026200"}]
            }, {
                "text": "沃尔沃卡车销售",
                "selectable": false,
                "nodes": [{"text": "沃尔沃卡车销售", "code": "026300"}]
            }, {"text": "观致销售", "selectable": false, "nodes": [{"text": "观致销售", "code": "029900"}]}]
        }, {
            "text": "汽车维修",
            "selectable": false,
            "nodes": [{
                "text": "汽车维修",
                "selectable": false,
                "nodes": [{"text": "汽车维修", "code": "030000"}]
            }, {
                "text": "汽车综合维修",
                "selectable": false,
                "nodes": [{"text": "汽车综合维修", "code": "030100"}]
            }, {
                "text": "大众特约维修",
                "selectable": false,
                "nodes": [{"text": "大众维修", "code": "030200"}, {"text": "上海大众维修", "code": "030201"}, {
                    "text": "一汽-大众维修",
                    "code": "030202"
                }, {"text": "斯柯达维修", "code": "030203"}, {"text": "进口大众维修", "code": "030204"}, {
                    "text": "宾利维修",
                    "code": "030205"
                }, {"text": "兰博基尼维修", "code": "030206"}]
            }, {
                "text": "本田特约维修",
                "selectable": false,
                "nodes": [{"text": "本田维修", "code": "030300"}, {"text": "广汽本田维修", "code": "030301"}, {
                    "text": "东风本田维修",
                    "code": "030302"
                }, {"text": "本田讴歌维修", "code": "030303"}]
            }, {
                "text": "奥迪特约维修",
                "selectable": false,
                "nodes": [{"text": "奥迪维修", "code": "030400"}, {"text": "一汽-大众奥迪维修", "code": "030401"}]
            }, {
                "text": "通用特约维修",
                "selectable": false,
                "nodes": [{"text": "通用维修", "code": "030500"}, {"text": "凯迪拉克维修", "code": "030501"}, {
                    "text": "别克维修",
                    "code": "030502"
                }, {"text": "雪佛兰维修", "code": "030503"}, {"text": "欧宝维修", "code": "030504"}, {
                    "text": "萨博维修",
                    "code": "030505"
                }, {"text": "沃克斯豪尔维修", "code": "030506"}, {"text": "土星维修", "code": "030507"}, {
                    "text": "大宇维修",
                    "code": "030508"
                }]
            }, {
                "text": "宝马特约维修",
                "selectable": false,
                "nodes": [{"text": "宝马维修", "code": "030700"}, {"text": "宝马MINI维修", "code": "030701"}, {
                    "text": "劳斯莱斯维修",
                    "code": "030702"
                }]
            }, {
                "text": "日产特约维修",
                "selectable": false,
                "nodes": [{"text": "日产维修", "code": "030800"}, {"text": "英菲尼迪维修", "code": "030801"}, {
                    "text": "东风日产维修",
                    "code": "030802"
                }, {"text": "郑州日产维修", "code": "030803"}]
            }, {
                "text": "雷诺特约维修",
                "selectable": false,
                "nodes": [{"text": "雷诺维修", "code": "030900"}]
            }, {
                "text": "梅赛德斯-奔驰特约维修",
                "selectable": false,
                "nodes": [{"text": "梅赛德斯-奔驰维修", "code": "031000"}, {"text": "迈巴赫维修", "code": "031004"}, {
                    "text": "精灵维修",
                    "code": "031005"
                }]
            }, {
                "text": "丰田特约维修",
                "selectable": false,
                "nodes": [{"text": "丰田维修", "code": "031100"}, {"text": "一汽丰田维修", "code": "031101"}, {
                    "text": "广汽丰田维修",
                    "code": "031102"
                }, {"text": "雷克萨斯维修", "code": "031103"}, {"text": "大发维修", "code": "031104"}]
            }, {
                "text": "斯巴鲁特约维修",
                "selectable": false,
                "nodes": [{"text": "斯巴鲁维修", "code": "031200"}]
            }, {
                "text": "标致雪铁龙特约维修",
                "selectable": false,
                "nodes": [{"text": "雪铁龙维修", "code": "031300"}, {"text": "东风标致维修", "code": "031301"}, {
                    "text": "东风雪铁龙维修",
                    "code": "031302"
                }, {"text": "DS维修", "code": "031303"}]
            }, {
                "text": "三菱特约维修",
                "selectable": false,
                "nodes": [{"text": "三菱维修", "code": "031400"}, {"text": "广汽三菱维修", "code": "031401"}]
            }, {
                "text": "菲亚特特约维修",
                "selectable": false,
                "nodes": [{"text": "菲亚特维修", "code": "031500"}, {"text": "阿尔法-罗密欧维修", "code": "031501"}]
            }, {
                "text": "法拉利特约维修",
                "selectable": false,
                "nodes": [{"text": "法拉利维修", "code": "031600"}, {"text": "玛莎拉蒂维修", "code": "031601"}]
            }, {
                "text": "现代特约维修",
                "selectable": false,
                "nodes": [{"text": "现代维修", "code": "031700"}, {"text": "进口现代维修", "code": "031701"}, {
                    "text": "北京现代维修",
                    "code": "031702"
                }]
            }, {
                "text": "起亚特约维修",
                "selectable": false,
                "nodes": [{"text": "起亚维修", "code": "031800"}, {"text": "进口起亚维修", "code": "031801"}, {
                    "text": "东风悦达起亚维修",
                    "code": "031802"
                }]
            }, {
                "text": "福特特约维修",
                "selectable": false,
                "nodes": [{"text": "福特维修", "code": "031900"}, {"text": "马自达维修", "code": "031902"}, {
                    "text": "林肯维修",
                    "code": "031903"
                }, {"text": "水星维修", "code": "031904"}]
            }, {
                "text": "捷豹特约维修",
                "selectable": false,
                "nodes": [{"text": "捷豹维修", "code": "032000"}]
            }, {
                "text": "路虎特约维修",
                "selectable": false,
                "nodes": [{"text": "路虎维修", "code": "032100"}]
            }, {
                "text": "保时捷特约维修",
                "selectable": false,
                "nodes": [{"text": "保时捷维修", "code": "032200"}]
            }, {
                "text": "东风特约维修",
                "selectable": false,
                "nodes": [{"text": "东风维修", "code": "032300"}]
            }, {
                "text": "吉利特约维修",
                "selectable": false,
                "nodes": [{"text": "吉利维修", "code": "032400"}, {"text": "沃尔沃维修", "code": "032401"}]
            }, {
                "text": "奇瑞特约维修",
                "selectable": false,
                "nodes": [{"text": "奇瑞维修", "code": "032500"}]
            }, {
                "text": "克莱斯勒特约维修",
                "selectable": false,
                "nodes": [{"text": "克莱斯勒维修", "code": "032600"}, {"text": "吉普维修", "code": "032601"}, {
                    "text": "道奇维修",
                    "code": "032602"
                }]
            }, {"text": "荣威维修", "selectable": false, "nodes": [{"text": "荣威维修", "code": "032700"}]}, {
                "text": "名爵维修",
                "selectable": false,
                "nodes": [{"text": "名爵维修", "code": "032800"}]
            }, {"text": "江淮维修", "selectable": false, "nodes": [{"text": "江淮维修", "code": "032900"}]}, {
                "text": "红旗维修",
                "selectable": false,
                "nodes": [{"text": "红旗维修", "code": "033000"}]
            }, {
                "text": "长安汽车维修",
                "selectable": false,
                "nodes": [{"text": "长安汽车维修", "code": "033100"}]
            }, {
                "text": "海马汽车维修",
                "selectable": false,
                "nodes": [{"text": "海马汽车维修", "code": "033200"}]
            }, {
                "text": "北京汽车维修",
                "selectable": false,
                "nodes": [{"text": "北京汽车维修", "code": "033300"}]
            }, {
                "text": "长城汽车维修",
                "selectable": false,
                "nodes": [{"text": "长城汽车维修", "code": "033400"}]
            }, {"text": "纳智捷维修", "selectable": false, "nodes": [{"text": "纳智捷维修", "code": "033500"}]}, {
                "text": "货车维修",
                "selectable": false,
                "nodes": [{"text": "货车维修", "code": "035000"}]
            }, {
                "text": "东风货车维修",
                "selectable": false,
                "nodes": [{"text": "东风货车维修", "code": "035100"}]
            }, {
                "text": "中国重汽维修",
                "selectable": false,
                "nodes": [{"text": "中国重汽维修", "code": "035200"}]
            }, {
                "text": "一汽解放维修",
                "selectable": false,
                "nodes": [{"text": "一汽解放维修", "code": "035300"}]
            }, {
                "text": "福田卡车维修",
                "selectable": false,
                "nodes": [{"text": "福田卡车维修", "code": "035400"}]
            }, {
                "text": "陕西重汽维修",
                "selectable": false,
                "nodes": [{"text": "陕西重汽维修", "code": "035500"}]
            }, {
                "text": "北奔重汽维修",
                "selectable": false,
                "nodes": [{"text": "北奔重汽维修", "code": "035600"}]
            }, {
                "text": "江淮货车维修",
                "selectable": false,
                "nodes": [{"text": "江淮货车维修", "code": "035700"}]
            }, {
                "text": "华菱星马维修",
                "selectable": false,
                "nodes": [{"text": "华菱星马维修", "code": "035800"}]
            }, {
                "text": "成都大运汽车维修",
                "selectable": false,
                "nodes": [{"text": "成都大运汽车维修", "code": "035900"}]
            }, {
                "text": "梅赛德斯-奔驰卡车维修",
                "selectable": false,
                "nodes": [{"text": "梅赛德斯-奔驰卡车维修", "code": "036000"}]
            }, {
                "text": "德国曼恩维修",
                "selectable": false,
                "nodes": [{"text": "德国曼恩维修", "code": "036100"}]
            }, {
                "text": "斯堪尼亚维修",
                "selectable": false,
                "nodes": [{"text": "斯堪尼亚维修", "code": "036200"}]
            }, {
                "text": "沃尔沃卡车维修",
                "selectable": false,
                "nodes": [{"text": "沃尔沃卡车维修", "code": "036300"}]
            }, {"text": "观致维修", "selectable": false, "nodes": [{"text": "观致维修", "code": "039900"}]}]
        }, {
            "text": "摩托车服务",
            "selectable": false,
            "nodes": [{
                "text": "摩托车服务相关",
                "selectable": false,
                "nodes": [{"text": "摩托车服务相关", "code": "040000"}]
            }, {
                "text": "摩托车销售",
                "selectable": false,
                "nodes": [{"text": "摩托车销售", "code": "040100"}, {"text": "宝马摩托车销售", "code": "040101"}]
            }, {
                "text": "摩托车维修",
                "selectable": false,
                "nodes": [{"text": "摩托车维修", "code": "040200"}, {"text": "宝马摩托车维修", "code": "040201"}]
            }]
        }, {
            "text": "餐饮服务",
            "selectable": false,
            "nodes": [{
                "text": "餐饮相关场所",
                "selectable": false,
                "nodes": [{"text": "餐饮相关", "code": "050000"}]
            }, {
                "text": "中餐厅",
                "selectable": false,
                "nodes": [{"text": "中餐厅", "code": "050100"}, {"text": "综合酒楼", "code": "050101"}, {
                    "text": "四川菜(川菜)",
                    "code": "050102"
                }, {"text": "广东菜(粤菜)", "code": "050103"}, {"text": "山东菜(鲁菜)", "code": "050104"}, {
                    "text": "江苏菜",
                    "code": "050105"
                }, {"text": "浙江菜", "code": "050106"}, {"text": "上海菜", "code": "050107"}, {
                    "text": "湖南菜(湘菜)",
                    "code": "050108"
                }, {"text": "安徽菜(徽菜)", "code": "050109"}, {"text": "福建菜", "code": "050110"}, {
                    "text": "北京菜",
                    "code": "050111"
                }, {"text": "湖北菜(鄂菜)", "code": "050112"}, {"text": "东北菜", "code": "050113"}, {
                    "text": "云贵菜",
                    "code": "050114"
                }, {"text": "西北菜", "code": "050115"}, {"text": "老字号", "code": "050116"}, {
                    "text": "火锅店",
                    "code": "050117"
                }, {"text": "特色/地方风味餐厅", "code": "050118"}, {"text": "海鲜酒楼", "code": "050119"}, {
                    "text": "中式素菜馆",
                    "code": "050120"
                }, {"text": "清真菜馆", "code": "050121"}, {"text": "台湾菜", "code": "050122"}, {
                    "text": "潮州菜",
                    "code": "050123"
                }]
            }, {
                "text": "外国餐厅",
                "selectable": false,
                "nodes": [{"text": "外国餐厅", "code": "050200"}, {"text": "西餐厅(综合风味)", "code": "050201"}, {
                    "text": "日本料理",
                    "code": "050202"
                }, {"text": "韩国料理", "code": "050203"}, {"text": "法式菜品餐厅", "code": "050204"}, {
                    "text": "意式菜品餐厅",
                    "code": "050205"
                }, {"text": "泰国/越南菜品餐厅", "code": "050206"}, {"text": "地中海风格菜品", "code": "050207"}, {
                    "text": "美式风味",
                    "code": "050208"
                }, {"text": "印度风味", "code": "050209"}, {"text": "英国式菜品餐厅", "code": "050210"}, {
                    "text": "牛扒店(扒房)",
                    "code": "050211"
                }, {"text": "俄国菜", "code": "050212"}, {"text": "葡国菜", "code": "050213"}, {
                    "text": "德国菜",
                    "code": "050214"
                }, {"text": "巴西菜", "code": "050215"}, {"text": "墨西哥菜", "code": "050216"}, {
                    "text": "其它亚洲菜",
                    "code": "050217"
                }]
            }, {
                "text": "快餐厅",
                "selectable": false,
                "nodes": [{"text": "快餐厅", "code": "050300"}, {"text": "肯德基", "code": "050301"}, {
                    "text": "麦当劳",
                    "code": "050302"
                }, {"text": "必胜客", "code": "050303"}, {"text": "永和豆浆", "code": "050304"}, {
                    "text": "茶餐厅",
                    "code": "050305"
                }, {"text": "大家乐", "code": "050306"}, {"text": "大快活", "code": "050307"}, {
                    "text": "美心",
                    "code": "050308"
                }, {"text": "吉野家", "code": "050309"}, {"text": "仙跡岩", "code": "050310"}, {
                    "text": "呷哺呷哺",
                    "code": "050311"
                }]
            }, {"text": "休闲餐饮场所", "selectable": false, "nodes": [{"text": "休闲餐饮场所", "code": "050400"}]}, {
                "text": "咖啡厅",
                "selectable": false,
                "nodes": [{"text": "咖啡厅", "code": "050500"}, {"text": "星巴克咖啡", "code": "050501"}, {
                    "text": "上岛咖啡",
                    "code": "050502"
                }, {"text": "Pacific Coffee Company", "code": "050503"}, {"text": "巴黎咖啡店", "code": "050504"}]
            }, {"text": "茶艺馆", "selectable": false, "nodes": [{"text": "茶艺馆", "code": "050600"}]}, {
                "text": "冷饮店",
                "selectable": false,
                "nodes": [{"text": "冷饮店", "code": "050700"}]
            }, {"text": "糕饼店", "selectable": false, "nodes": [{"text": "糕饼店", "code": "050800"}]}, {
                "text": "甜品店",
                "selectable": false,
                "nodes": [{"text": "甜品店", "code": "050900"}]
            }]
        }, {
            "text": "购物服务",
            "selectable": false,
            "nodes": [{
                "text": "购物相关场所",
                "selectable": false,
                "nodes": [{"text": "购物相关场所", "code": "060000"}]
            }, {
                "text": "商场",
                "selectable": false,
                "nodes": [{"text": "商场", "code": "060100"}, {"text": "购物中心", "code": "060101"}, {
                    "text": "普通商场",
                    "code": "060102"
                }, {"text": "免税品店", "code": "060103"}]
            }, {
                "text": "便民商店/便利店",
                "selectable": false,
                "nodes": [{"text": "便民商店/便利店", "code": "060200"}, {
                    "text": "7-ELEVEn便利店",
                    "code": "060201"
                }, {"text": "OK便利店", "code": "060202"}]
            }, {
                "text": "家电电子卖场",
                "selectable": false,
                "nodes": [{"text": "家电电子卖场", "code": "060300"}, {"text": "综合家电商场", "code": "060301"}, {
                    "text": "国美",
                    "code": "060302"
                }, {"text": "大中", "code": "060303"}, {"text": "苏宁", "code": "060304"}, {
                    "text": "手机销售",
                    "code": "060305"
                }, {"text": "数码电子", "code": "060306"}, {"text": "丰泽", "code": "060307"}, {
                    "text": "苏宁镭射",
                    "code": "060308"
                }]
            }, {
                "text": "超级市场",
                "selectable": false,
                "nodes": [{"text": "超市", "code": "060400"}, {"text": "家乐福", "code": "060401"}, {
                    "text": "沃尔玛",
                    "code": "060402"
                }, {"text": "华润", "code": "060403"}, {"text": "北京华联", "code": "060404"}, {
                    "text": "上海华联",
                    "code": "060405"
                }, {"text": "麦德龙", "code": "060406"}, {"text": "乐天玛特", "code": "060407"}, {
                    "text": "华堂",
                    "code": "060408"
                }, {"text": "卜蜂莲花", "code": "060409"}, {"text": "屈臣氏", "code": "060411"}, {
                    "text": "惠康超市",
                    "code": "060413"
                }, {"text": "百佳超市", "code": "060414"}, {"text": "万宁超市", "code": "060415"}]
            }, {
                "text": "花鸟鱼虫市场",
                "selectable": false,
                "nodes": [{"text": "花鸟鱼虫市场", "code": "060500"}, {"text": "花卉市场", "code": "060501"}, {
                    "text": "宠物市场",
                    "code": "060502"
                }]
            }, {
                "text": "家居建材市场",
                "selectable": false,
                "nodes": [{"text": "家居建材市场", "code": "060600"}, {"text": "家具建材综合市场", "code": "060601"}, {
                    "text": "家具城",
                    "code": "060602"
                }, {"text": "建材五金市场", "code": "060603"}, {"text": "厨卫市场", "code": "060604"}, {
                    "text": "布艺市场",
                    "code": "060605"
                }, {"text": "灯具瓷器市场", "code": "060606"}]
            }, {
                "text": "综合市场",
                "selectable": false,
                "nodes": [{"text": "综合市场", "code": "060700"}, {"text": "小商品市场", "code": "060701"}, {
                    "text": "旧货市场",
                    "code": "060702"
                }, {"text": "农副产品市场", "code": "060703"}, {"text": "果品市场", "code": "060704"}, {
                    "text": "蔬菜市场",
                    "code": "060705"
                }, {"text": "水产海鲜市场", "code": "060706"}]
            }, {"text": "文化用品店", "selectable": false, "nodes": [{"text": "文化用品店", "code": "060800"}]}, {
                "text": "体育用品店",
                "selectable": false,
                "nodes": [{"text": "体育用品店", "code": "060900"}, {"text": "李宁专卖店", "code": "060901"}, {
                    "text": "耐克专卖店",
                    "code": "060902"
                }, {"text": "阿迪达斯专卖店", "code": "060903"}, {"text": "锐步专卖店", "code": "060904"}, {
                    "text": "彪马专卖店",
                    "code": "060905"
                }, {"text": "高尔夫用品店", "code": "060906"}, {"text": "户外用品", "code": "060907"}]
            }, {
                "text": "特色商业街",
                "selectable": false,
                "nodes": [{"text": "特色商业街", "code": "061000"}, {"text": "步行街", "code": "061001"}]
            }, {
                "text": "服装鞋帽皮具店",
                "selectable": false,
                "nodes": [{"text": "服装鞋帽皮具店", "code": "061100"}, {"text": "品牌服装店", "code": "061101"}, {
                    "text": "品牌鞋店",
                    "code": "061102"
                }, {"text": "品牌皮具店", "code": "061103"}, {"text": "品牌箱包店", "code": "061104"}]
            }, {
                "text": "专卖店",
                "selectable": false,
                "nodes": [{"text": "专营店", "code": "061200"}, {"text": "古玩字画店", "code": "061201"}, {
                    "text": "珠宝首饰工艺品",
                    "code": "061202"
                }, {"text": "钟表店", "code": "061203"}, {"text": "眼镜店", "code": "061204"}, {
                    "text": "书店",
                    "code": "061205"
                }, {"text": "音像店", "code": "061206"}, {"text": "儿童用品店", "code": "061207"}, {
                    "text": "自行车专卖店",
                    "code": "061208"
                }, {"text": "礼品饰品店", "code": "061209"}, {"text": "烟酒专卖店", "code": "061210"}, {
                    "text": "宠物用品店",
                    "code": "061211"
                }, {"text": "摄影器材店", "code": "061212"}, {"text": "宝马生活方式", "code": "061213"}, {
                    "text": "土特产专卖店",
                    "code": "061214"
                }]
            }, {
                "text": "特殊买卖场所",
                "selectable": false,
                "nodes": [{"text": "特殊买卖场所", "code": "061300"}, {"text": "拍卖行", "code": "061301"}, {
                    "text": "典当行",
                    "code": "061302"
                }]
            }, {
                "text": "个人用品/化妆品店",
                "selectable": false,
                "nodes": [{"text": "其它个人用品店", "code": "061400"}, {"text": "莎莎", "code": "061401"}]
            }]
        }, {
            "text": "生活服务",
            "selectable": false,
            "nodes": [{
                "text": "生活服务场所",
                "selectable": false,
                "nodes": [{"text": "生活服务场所", "code": "070000"}]
            }, {"text": "旅行社", "selectable": false, "nodes": [{"text": "旅行社", "code": "070100"}]}, {
                "text": "信息咨询中心",
                "selectable": false,
                "nodes": [{"text": "信息咨询中心", "code": "070200"}, {"text": "服务中心", "code": "070201"}, {
                    "text": "旅馆问讯",
                    "code": "070202"
                }, {"text": "行李查询/行李问询", "code": "070203"}]
            }, {
                "text": "售票处",
                "selectable": false,
                "nodes": [{"text": "售票处", "code": "070300"}, {"text": "飞机票代售点", "code": "070301"}, {
                    "text": "火车票代售点",
                    "code": "070302"
                }, {"text": "长途汽车票代售点", "code": "070303"}, {"text": "船票代售点", "code": "070304"}, {
                    "text": "公交卡/月票代售点",
                    "code": "070305"
                }, {"text": "公园景点售票处", "code": "070306"}]
            }, {
                "text": "邮局",
                "selectable": false,
                "nodes": [{"text": "邮局", "code": "070400"}, {"text": "邮政速递", "code": "070401"}]
            }, {
                "text": "物流速递",
                "selectable": false,
                "nodes": [{"text": "物流速递", "code": "070500"}, {"text": "物流仓储场地", "code": "070501"}]
            }, {
                "text": "电讯营业厅",
                "selectable": false,
                "nodes": [{"text": "电讯营业厅", "code": "070600"}, {
                    "text": "中国电信营业厅",
                    "code": "070601"
                }, {"text": "中国移动营业厅", "code": "070603"}, {"text": "中国联通营业厅", "code": "070604"}, {
                    "text": "中国铁通营业厅",
                    "code": "070605"
                }, {"text": "中国卫通营业厅", "code": "070606"}, {"text": "和记电讯", "code": "070607"}, {
                    "text": "数码通电讯",
                    "code": "070608"
                }, {"text": "电讯盈科", "code": "070609"}, {"text": "中国移动香港", "code": "070610"}]
            }, {
                "text": "事务所",
                "selectable": false,
                "nodes": [{"text": "事务所", "code": "070700"}, {"text": "律师事务所", "code": "070701"}, {
                    "text": "会计师事务所",
                    "code": "070702"
                }, {"text": "评估事务所", "code": "070703"}, {"text": "审计事务所", "code": "070704"}, {
                    "text": "认证事务所",
                    "code": "070705"
                }, {"text": "专利事务所", "code": "070706"}]
            }, {"text": "人才市场", "selectable": false, "nodes": [{"text": "人才市场", "code": "070800"}]}, {
                "text": "自来水营业厅",
                "selectable": false,
                "nodes": [{"text": "自来水营业厅", "code": "070900"}]
            }, {"text": "电力营业厅", "selectable": false, "nodes": [{"text": "电力营业厅", "code": "071000"}]}, {
                "text": "美容美发店",
                "selectable": false,
                "nodes": [{"text": "美容美发店", "code": "071100"}]
            }, {"text": "维修站点", "selectable": false, "nodes": [{"text": "维修站点", "code": "071200"}]}, {
                "text": "摄影冲印店",
                "selectable": false,
                "nodes": [{"text": "摄影冲印", "code": "071300"}]
            }, {"text": "洗浴推拿场所", "selectable": false, "nodes": [{"text": "洗浴推拿场所", "code": "071400"}]}, {
                "text": "洗衣店",
                "selectable": false,
                "nodes": [{"text": "洗衣店", "code": "071500"}]
            }, {"text": "中介机构", "selectable": false, "nodes": [{"text": "中介机构", "code": "071600"}]}, {
                "text": "搬家公司",
                "selectable": false,
                "nodes": [{"text": "搬家公司", "code": "071700"}]
            }, {
                "text": "彩票彩券销售点",
                "selectable": false,
                "nodes": [{"text": "彩票彩券销售点", "code": "071800"}, {"text": "马会投注站", "code": "071801"}]
            }, {
                "text": "丧葬设施",
                "selectable": false,
                "nodes": [{"text": "丧葬设施", "code": "071900"}, {"text": "陵园", "code": "071901"}, {
                    "text": "公墓",
                    "code": "071902"
                }, {"text": "殡仪馆", "code": "071903"}]
            }, {
                "text": "婴儿服务场所",
                "selectable": false,
                "nodes": [{"text": "婴儿服务场所", "code": "072000"}, {"text": "婴儿游泳馆", "code": "072001"}]
            }]
        }, {
            "text": "体育休闲服务",
            "selectable": false,
            "nodes": [{
                "text": "体育休闲服务场所",
                "selectable": false,
                "nodes": [{"text": "体育休闲服务场所", "code": "080000"}]
            }, {
                "text": "运动场馆",
                "selectable": false,
                "nodes": [{"text": "运动场所", "code": "080100"}, {"text": "综合体育馆", "code": "080101"}, {
                    "text": "保龄球馆",
                    "code": "080102"
                }, {"text": "网球场", "code": "080103"}, {"text": "篮球场馆", "code": "080104"}, {
                    "text": "足球场",
                    "code": "080105"
                }, {"text": "滑雪场", "code": "080106"}, {"text": "溜冰场", "code": "080107"}, {
                    "text": "户外健身场所",
                    "code": "080108"
                }, {"text": "海滨浴场", "code": "080109"}, {"text": "游泳馆", "code": "080110"}, {
                    "text": "健身中心",
                    "code": "080111"
                }, {"text": "乒乓球馆", "code": "080112"}, {"text": "台球厅", "code": "080113"}, {
                    "text": "壁球场",
                    "code": "080114"
                }, {"text": "马术俱乐部", "code": "080115"}, {"text": "赛马场", "code": "080116"}, {
                    "text": "橄榄球场",
                    "code": "080117"
                }, {"text": "羽毛球场", "code": "080118"}, {"text": "跆拳道场馆", "code": "080119"}]
            }, {
                "text": "高尔夫相关",
                "selectable": false,
                "nodes": [{"text": "高尔夫相关", "code": "080200"}, {"text": "高尔夫球场", "code": "080201"}, {
                    "text": "高尔夫练习场",
                    "code": "080202"
                }]
            }, {
                "text": "娱乐场所",
                "selectable": false,
                "nodes": [{"text": "娱乐场所", "code": "080300"}, {"text": "夜总会", "code": "080301"}, {
                    "text": "KTV",
                    "code": "080302"
                }, {"text": "迪厅", "code": "080303"}, {"text": "酒吧", "code": "080304"}, {
                    "text": "游戏厅",
                    "code": "080305"
                }, {"text": "棋牌室", "code": "080306"}, {"text": "博彩中心", "code": "080307"}, {
                    "text": "网吧",
                    "code": "080308"
                }]
            }, {
                "text": "度假疗养场所",
                "selectable": false,
                "nodes": [{"text": "度假疗养场所", "code": "080400"}, {"text": "度假村", "code": "080401"}, {
                    "text": "疗养院",
                    "code": "080402"
                }]
            }, {
                "text": "休闲场所",
                "selectable": false,
                "nodes": [{"text": "休闲场所", "code": "080500"}, {"text": "游乐场", "code": "080501"}, {
                    "text": "垂钓园",
                    "code": "080502"
                }, {"text": "采摘园", "code": "080503"}, {"text": "露营地", "code": "080504"}, {
                    "text": "水上活动中心",
                    "code": "080505"
                }]
            }, {
                "text": "影剧院",
                "selectable": false,
                "nodes": [{"text": "影剧院相关", "code": "080600"}, {"text": "电影院", "code": "080601"}, {
                    "text": "音乐厅",
                    "code": "080602"
                }, {"text": "剧场", "code": "080603"}]
            }]
        }, {
            "text": "医疗保健服务",
            "selectable": false,
            "nodes": [{
                "text": "医疗保健服务场所",
                "selectable": false,
                "nodes": [{"text": "医疗保健服务场所", "code": "090000"}]
            }, {
                "text": "综合医院",
                "selectable": false,
                "nodes": [{"text": "综合医院", "code": "090100"}, {"text": "三级甲等医院", "code": "090101"}, {
                    "text": "卫生院",
                    "code": "090102"
                }]
            }, {
                "text": "专科医院",
                "selectable": false,
                "nodes": [{"text": "专科医院", "code": "090200"}, {"text": "整形美容", "code": "090201"}, {
                    "text": "口腔医院",
                    "code": "090202"
                }, {"text": "眼科医院", "code": "090203"}, {"text": "耳鼻喉医院", "code": "090204"}, {
                    "text": "胸科医院",
                    "code": "090205"
                }, {"text": "骨科医院", "code": "090206"}, {"text": "肿瘤医院", "code": "090207"}, {
                    "text": "脑科医院",
                    "code": "090208"
                }, {"text": "妇科医院", "code": "090209"}, {"text": "精神病医院", "code": "090210"}, {
                    "text": "传染病医院",
                    "code": "090211"
                }]
            }, {"text": "诊所", "selectable": false, "nodes": [{"text": "诊所", "code": "090300"}]}, {
                "text": "急救中心",
                "selectable": false,
                "nodes": [{"text": "急救中心", "code": "090400"}]
            }, {
                "text": "疾病预防机构",
                "selectable": false,
                "nodes": [{"text": "疾病预防", "code": "090500"}]
            }, {
                "text": "医药保健销售店",
                "selectable": false,
                "nodes": [{"text": "医药保健相关", "code": "090600"}, {"text": "药房", "code": "090601"}, {
                    "text": "医疗保健用品",
                    "code": "090602"
                }]
            }, {
                "text": "动物医疗场所",
                "selectable": false,
                "nodes": [{"text": "动物医疗场所", "code": "090700"}, {"text": "宠物诊所", "code": "090701"}, {
                    "text": "兽医站",
                    "code": "090702"
                }]
            }]
        }, {
            "text": "住宿服务",
            "selectable": false,
            "nodes": [{
                "text": "住宿服务相关",
                "selectable": false,
                "nodes": [{"text": "住宿服务相关", "code": "100000"}]
            }, {
                "text": "宾馆酒店",
                "selectable": false,
                "nodes": [{"text": "宾馆酒店", "code": "100100"}, {"text": "六星级及以上宾馆", "code": "100101"}, {
                    "text": "五星级宾馆",
                    "code": "100102"
                }, {"text": "四星级宾馆", "code": "100103"}, {"text": "三星级宾馆", "code": "100104"}, {
                    "text": "经济型连锁酒店",
                    "code": "100105"
                }]
            }, {
                "text": "旅馆招待所",
                "selectable": false,
                "nodes": [{"text": "旅馆招待所", "code": "100200"}, {"text": "青年旅舍", "code": "100201"}]
            }]
        }, {
            "text": "风景名胜",
            "selectable": false,
            "nodes": [{
                "text": "风景名胜相关",
                "selectable": false,
                "nodes": [{"text": "旅游景点", "code": "110000"}]
            }, {
                "text": "公园广场",
                "selectable": false,
                "nodes": [{"text": "公园广场", "code": "110100"}, {"text": "公园", "code": "110101"}, {
                    "text": "动物园",
                    "code": "110102"
                }, {"text": "植物园", "code": "110103"}, {"text": "水族馆", "code": "110104"}, {
                    "text": "城市广场",
                    "code": "110105"
                }, {"text": "公园内部设施", "code": "110106"}]
            }, {
                "text": "风景名胜",
                "selectable": false,
                "nodes": [{"text": "风景名胜", "code": "110200"}, {"text": "世界遗产", "code": "110201"}, {
                    "text": "国家级景点",
                    "code": "110202"
                }, {"text": "省级景点", "code": "110203"}, {"text": "纪念馆", "code": "110204"}, {
                    "text": "寺庙道观",
                    "code": "110205"
                }, {"text": "教堂", "code": "110206"}, {"text": "回教寺", "code": "110207"}, {
                    "text": "海滩",
                    "code": "110208"
                }, {"text": "观景点", "code": "110209"}]
            }]
        }, {
            "text": "商务住宅",
            "selectable": false,
            "nodes": [{
                "text": "商务住宅相关",
                "selectable": false,
                "nodes": [{"text": "商务住宅相关", "code": "120000"}]
            }, {"text": "产业园区", "selectable": false, "nodes": [{"text": "产业园区", "code": "120100"}]}, {
                "text": "楼宇",
                "selectable": false,
                "nodes": [{"text": "楼宇相关", "code": "120200"}, {"text": "商务写字楼", "code": "120201"}, {
                    "text": "工业大厦建筑物",
                    "code": "120202"
                }, {"text": "商住两用楼宇", "code": "120203"}]
            }, {
                "text": "住宅区",
                "selectable": false,
                "nodes": [{"text": "住宅区", "code": "120300"}, {"text": "别墅", "code": "120301"}, {
                    "text": "住宅小区",
                    "code": "120302"
                }, {"text": "宿舍", "code": "120303"}, {"text": "社区中心", "code": "120304"}]
            }]
        }, {
            "text": "政府机构及社会团体",
            "selectable": false,
            "nodes": [{
                "text": "政府及社会团体相关",
                "selectable": false,
                "nodes": [{"text": "政府及社会团体相关", "code": "130000"}]
            }, {
                "text": "政府机关",
                "selectable": false,
                "nodes": [{"text": "政府机关相关", "code": "130100"}, {
                    "text": "国家级机关及事业单位",
                    "code": "130101"
                }, {"text": "省直辖市级政府及事业单位", "code": "130102"}, {
                    "text": "地市级政府及事业单位",
                    "code": "130103"
                }, {"text": "区县级政府及事业单位", "code": "130104"}, {
                    "text": "乡镇级政府及事业单位",
                    "code": "130105"
                }, {"text": "乡镇以下级政府及事业单位", "code": "130106"}, {"text": "外地政府办", "code": "130107"}]
            }, {
                "text": "外国机构",
                "selectable": false,
                "nodes": [{"text": "外国机构相关", "code": "130200"}, {"text": "外国使领馆", "code": "130201"}, {
                    "text": "国际组织办事处",
                    "code": "130202"
                }]
            }, {"text": "民主党派", "selectable": false, "nodes": [{"text": "民主党派", "code": "130300"}]}, {
                "text": "社会团体",
                "selectable": false,
                "nodes": [{"text": "社会团体相关", "code": "130400"}, {"text": "共青团", "code": "130401"}, {
                    "text": "少先队",
                    "code": "130402"
                }, {"text": "妇联", "code": "130403"}, {"text": "残联", "code": "130404"}, {
                    "text": "红十字会",
                    "code": "130405"
                }, {"text": "消费者协会", "code": "130406"}, {"text": "行业协会", "code": "130407"}, {
                    "text": "慈善机构",
                    "code": "130408"
                }, {"text": "教会", "code": "130409"}]
            }, {
                "text": "公检法机构",
                "selectable": false,
                "nodes": [{"text": "公检法机关", "code": "130500"}, {"text": "公安警察", "code": "130501"}, {
                    "text": "检察院",
                    "code": "130502"
                }, {"text": "法院", "code": "130503"}, {"text": "消防机关", "code": "130504"}, {
                    "text": "公证鉴定机构",
                    "code": "130505"
                }, {"text": "社会治安机构", "code": "130506"}]
            }, {
                "text": "交通车辆管理",
                "selectable": false,
                "nodes": [{"text": "交通车辆管理相关", "code": "130600"}, {
                    "text": "交通管理机构",
                    "code": "130601"
                }, {"text": "车辆管理机构", "code": "130602"}, {"text": "验车场", "code": "130603"}, {
                    "text": "交通执法站",
                    "code": "130604"
                }, {"text": "车辆通行证办理处", "code": "130605"}, {"text": "货车相关检查站", "code": "130606"}]
            }, {
                "text": "工商税务机构",
                "selectable": false,
                "nodes": [{"text": "工商税务机构", "code": "130700"}, {"text": "工商部门", "code": "130701"}, {
                    "text": "国税机关",
                    "code": "130702"
                }, {"text": "地税机关", "code": "130703"}]
            }]
        }, {
            "text": "科教文化服务",
            "selectable": false,
            "nodes": [{
                "text": "科教文化场所",
                "selectable": false,
                "nodes": [{"text": "科教文化场所", "code": "140000"}]
            }, {
                "text": "博物馆",
                "selectable": false,
                "nodes": [{"text": "博物馆", "code": "140100"}, {"text": "奥迪博物馆", "code": "140101"}, {
                    "text": "梅赛德斯-奔驰博物馆",
                    "code": "140102"
                }]
            }, {
                "text": "展览馆",
                "selectable": false,
                "nodes": [{"text": "展览馆", "code": "140200"}, {"text": "室内展位", "code": "140201"}]
            }, {"text": "会展中心", "selectable": false, "nodes": [{"text": "会展中心", "code": "140300"}]}, {
                "text": "美术馆",
                "selectable": false,
                "nodes": [{"text": "美术馆", "code": "140400"}]
            }, {"text": "图书馆", "selectable": false, "nodes": [{"text": "图书馆", "code": "140500"}]}, {
                "text": "科技馆",
                "selectable": false,
                "nodes": [{"text": "科技馆", "code": "140600"}]
            }, {"text": "天文馆", "selectable": false, "nodes": [{"text": "天文馆", "code": "140700"}]}, {
                "text": "文化宫",
                "selectable": false,
                "nodes": [{"text": "文化宫", "code": "140800"}]
            }, {"text": "档案馆", "selectable": false, "nodes": [{"text": "档案馆", "code": "140900"}]}, {
                "text": "文艺团体",
                "selectable": false,
                "nodes": [{"text": "文艺团体", "code": "141000"}]
            }, {
                "text": "传媒机构",
                "selectable": false,
                "nodes": [{"text": "传媒机构", "code": "141100"}, {"text": "电视台", "code": "141101"}, {
                    "text": "电台",
                    "code": "141102"
                }, {"text": "报社", "code": "141103"}, {"text": "杂志社", "code": "141104"}, {
                    "text": "出版社",
                    "code": "141105"
                }]
            }, {
                "text": "学校",
                "selectable": false,
                "nodes": [{"text": "学校", "code": "141200"}, {"text": "高等院校", "code": "141201"}, {
                    "text": "中学",
                    "code": "141202"
                }, {"text": "小学", "code": "141203"}, {"text": "幼儿园", "code": "141204"}, {
                    "text": "成人教育",
                    "code": "141205"
                }, {"text": "职业技术学校", "code": "141206"}, {"text": "学校内部设施", "code": "141207"}]
            }, {"text": "科研机构", "selectable": false, "nodes": [{"text": "科研机构", "code": "141300"}]}, {
                "text": "培训机构",
                "selectable": false,
                "nodes": [{"text": "培训机构", "code": "141400"}]
            }, {"text": "驾校", "selectable": false, "nodes": [{"text": "驾校", "code": "141500"}]}]
        }, {
            "text": "交通设施服务",
            "selectable": false,
            "nodes": [{
                "text": "交通服务相关",
                "selectable": false,
                "nodes": [{"text": "交通服务相关", "code": "150000"}]
            }, {
                "text": "机场相关",
                "selectable": false,
                "nodes": [{"text": "机场相关", "code": "150100"}, {"text": "候机室", "code": "150101"}, {
                    "text": "摆渡车站",
                    "code": "150102"
                }, {"text": "飞机场", "code": "150104"}, {"text": "机场出发/到达", "code": "150105"}, {
                    "text": "直升机场",
                    "code": "150106"
                }, {"text": "机场货运处", "code": "150107"}]
            }, {
                "text": "火车站",
                "selectable": false,
                "nodes": [{"text": "火车站", "code": "150200"}, {"text": "候车室", "code": "150201"}, {
                    "text": "进站口/检票口",
                    "code": "150202"
                }, {"text": "出站口", "code": "150203"}, {"text": "站台", "code": "150204"}, {
                    "text": "售票",
                    "code": "150205"
                }, {"text": "退票", "code": "150206"}, {"text": "改签", "code": "150207"}, {
                    "text": "公安制证",
                    "code": "150208"
                }, {"text": "票务相关", "code": "150209"}, {"text": "货运火车站", "code": "150210"}]
            }, {
                "text": "港口码头",
                "selectable": false,
                "nodes": [{"text": "港口码头", "code": "150300"}, {"text": "客运港", "code": "150301"}, {
                    "text": "车渡口",
                    "code": "150302"
                }, {"text": "人渡口", "code": "150303"}, {"text": "货运港口码头", "code": "150304"}]
            }, {"text": "长途汽车站", "selectable": false, "nodes": [{"text": "长途汽车站", "code": "150400"}]}, {
                "text": "地铁站",
                "selectable": false,
                "nodes": [{"text": "地铁站", "code": "150500"}, {"text": "出入口", "code": "150501"}]
            }, {"text": "轻轨站", "selectable": false, "nodes": [{"text": "轻轨站", "code": "150600"}]}, {
                "text": "公交车站",
                "selectable": false,
                "nodes": [{"text": "公交车站相关", "code": "150700"}, {"text": "旅游专线车站", "code": "150701"}, {
                    "text": "普通公交站",
                    "code": "150702"
                }, {"text": "机场巴士", "code": "150703"}]
            }, {"text": "班车站", "selectable": false, "nodes": [{"text": "班车站", "code": "150800"}]}, {
                "text": "停车场",
                "selectable": false,
                "nodes": [{"text": "停车场相关", "code": "150900"}, {"text": "换乘停车场", "code": "150903"}, {
                    "text": "公共停车场",
                    "code": "150904"
                }, {"text": "专用停车场", "code": "150905"}, {"text": "路边停车场", "code": "150906"}, {
                    "text": "停车场入口",
                    "code": "150907"
                }, {"text": "停车场出口", "code": "150908"}, {"text": "停车场出入口", "code": "150909"}]
            }, {"text": "过境口岸", "selectable": false, "nodes": [{"text": "过境口岸", "code": "151000"}]}, {
                "text": "出租车",
                "selectable": false,
                "nodes": [{"text": "出租车", "code": "151100"}]
            }, {"text": "轮渡站", "selectable": false, "nodes": [{"text": "轮渡站", "code": "151200"}]}, {
                "text": "索道站",
                "selectable": false,
                "nodes": [{"text": "索道站", "code": "151300"}]
            }]
        }, {
            "text": "金融保险服务",
            "selectable": false,
            "nodes": [{
                "text": "金融保险服务机构",
                "selectable": false,
                "nodes": [{"text": "金融保险机构", "code": "160000"}]
            }, {
                "text": "银行",
                "selectable": false,
                "nodes": [{"text": "银行", "code": "160100"}, {"text": "中国人民银行", "code": "160101"}, {
                    "text": "国家开发银行",
                    "code": "160102"
                }, {"text": "中国进出口银行", "code": "160103"}, {"text": "中国银行", "code": "160104"}, {
                    "text": "中国工商银行",
                    "code": "160105"
                }, {"text": "中国建设银行", "code": "160106"}, {"text": "中国农业银行", "code": "160107"}, {
                    "text": "交通银行",
                    "code": "160108"
                }, {"text": "招商银行", "code": "160109"}, {"text": "华夏银行", "code": "160110"}, {
                    "text": "中信银行",
                    "code": "160111"
                }, {"text": "中国民生银行", "code": "160112"}, {"text": "中国光大银行", "code": "160113"}, {
                    "text": "上海银行",
                    "code": "160114"
                }, {"text": "上海浦东发展银行", "code": "160115"}, {"text": "平安银行", "code": "160117"}, {
                    "text": "兴业银行",
                    "code": "160118"
                }, {"text": "北京银行", "code": "160119"}, {"text": "广发银行", "code": "160120"}, {
                    "text": "农村商业银行",
                    "code": "160121"
                }, {"text": "香港恒生银行", "code": "160122"}, {"text": "东亚银行", "code": "160123"}, {
                    "text": "花旗银行",
                    "code": "160124"
                }, {"text": "渣打银行", "code": "160125"}, {"text": "汇丰银行", "code": "160126"}, {
                    "text": "荷兰银行",
                    "code": "160127"
                }, {"text": "美国运通银行", "code": "160128"}, {"text": "瑞士友邦银行", "code": "160129"}, {
                    "text": "美国银行",
                    "code": "160130"
                }, {"text": "蒙特利尔银行", "code": "160131"}, {"text": "纽约银行", "code": "160132"}, {
                    "text": "苏格兰皇家银行",
                    "code": "160133"
                }, {"text": "法国兴业银行", "code": "160134"}, {"text": "德意志银行", "code": "160135"}, {
                    "text": "日本三菱东京日联银行",
                    "code": "160136"
                }, {"text": "巴克莱银行", "code": "160137"}, {"text": "摩根大通银行", "code": "160138"}, {
                    "text": "中国邮政储蓄银行",
                    "code": "160139"
                }, {"text": "香港星展银行", "code": "160140"}, {"text": "南洋商业银行", "code": "160141"}, {
                    "text": "上海商业银行",
                    "code": "160142"
                }, {"text": "永亨银行", "code": "160143"}, {"text": "香港永隆银行", "code": "160144"}, {
                    "text": "创兴银行",
                    "code": "160145"
                }, {"text": "大新银行", "code": "160146"}, {"text": "中信银行(国际)", "code": "160147"}, {
                    "text": "大众银行(香港)",
                    "code": "160148"
                }, {"text": "北京农商银行", "code": "160149"}, {"text": "上海农商银行", "code": "160150"}, {
                    "text": "广州农商银行",
                    "code": "160151"
                }, {"text": "深圳农村商业银行", "code": "160152"}]
            }, {"text": "银行相关", "selectable": false, "nodes": [{"text": "银行相关", "code": "160200"}]}, {
                "text": "自动提款机",
                "selectable": false,
                "nodes": [{"text": "自动提款机", "code": "160300"}, {
                    "text": "中国银行ATM",
                    "code": "160301"
                }, {"text": "中国工商银行ATM", "code": "160302"}, {
                    "text": "中国建设银行ATM",
                    "code": "160303"
                }, {"text": "中国农业银行ATM", "code": "160304"}, {"text": "交通银行ATM", "code": "160305"}, {
                    "text": "招商银行ATM",
                    "code": "160306"
                }, {"text": "华夏银行ATM", "code": "160307"}, {"text": "中信银行ATM", "code": "160308"}, {
                    "text": "中国民生银行ATM",
                    "code": "160309"
                }, {"text": "中国光大银行ATM", "code": "160310"}, {
                    "text": "上海银行ATM",
                    "code": "160311"
                }, {"text": "上海浦东发展银行ATM", "code": "160312"}, {"text": "平安银行ATM", "code": "160314"}, {
                    "text": "兴业银行ATM",
                    "code": "160315"
                }, {"text": "北京银行ATM", "code": "160316"}, {"text": "广发银行ATM", "code": "160317"}, {
                    "text": "农村商业银行ATM",
                    "code": "160318"
                }, {"text": "香港恒生银行ATM", "code": "160319"}, {"text": "东亚银行ATM", "code": "160320"}, {
                    "text": "花旗银行ATM",
                    "code": "160321"
                }, {"text": "渣打银行ATM", "code": "160322"}, {"text": "汇丰银行ATM", "code": "160323"}, {
                    "text": "荷兰银行ATM",
                    "code": "160324"
                }, {"text": "美国运通银行ATM", "code": "160325"}, {"text": "瑞士友邦银行ATM", "code": "160326"}, {
                    "text": "美国银行ATM",
                    "code": "160327"
                }, {"text": "蒙特利尔银行ATM", "code": "160328"}, {
                    "text": "纽约银行ATM",
                    "code": "160329"
                }, {"text": "苏格兰皇家银行ATM", "code": "160330"}, {
                    "text": "法国兴业银行ATM",
                    "code": "160331"
                }, {"text": "德意志银行ATM", "code": "160332"}, {
                    "text": "日本三菱东京日联银行ATM",
                    "code": "160333"
                }, {"text": "巴克莱银行ATM", "code": "160334"}, {
                    "text": "摩根大通银行ATM",
                    "code": "160335"
                }, {"text": "中国邮政储蓄银行ATM", "code": "160336"}, {
                    "text": "香港星展银行ATM",
                    "code": "160337"
                }, {"text": "南洋商业银行ATM", "code": "160338"}, {"text": "上海商业银行ATM", "code": "160339"}, {
                    "text": "永亨银行ATM",
                    "code": "160340"
                }, {"text": "香港永隆银行ATM", "code": "160341"}, {"text": "创兴银行ATM", "code": "160342"}, {
                    "text": "大新银行ATM",
                    "code": "160343"
                }, {"text": "中信银行(国际)ATM", "code": "160344"}, {
                    "text": "大众银行(香港)ATM",
                    "code": "160345"
                }, {"text": "北京农商银行ATM", "code": "160346"}, {
                    "text": "上海农商银行ATM",
                    "code": "160347"
                }, {"text": "广州农商银行ATM", "code": "160348"}, {"text": "深圳农村商业银行ATM", "code": "160349"}]
            }, {
                "text": "保险公司",
                "selectable": false,
                "nodes": [{"text": "保险公司", "code": "160400"}, {
                    "text": "中国人民保险公司",
                    "code": "160401"
                }, {"text": "中国人寿保险公司", "code": "160402"}, {"text": "中国平安保险公司", "code": "160403"}, {
                    "text": "中国再保险公司",
                    "code": "160404"
                }, {"text": "中国太平洋保险", "code": "160405"}, {
                    "text": "新华人寿保险公司",
                    "code": "160406"
                }, {"text": "华泰财产保险股份有限公司", "code": "160407"}, {"text": "泰康人寿保险公司", "code": "160408"}]
            }, {
                "text": "证券公司",
                "selectable": false,
                "nodes": [{"text": "证券公司", "code": "160500"}, {"text": "证券营业厅", "code": "160501"}]
            }, {"text": "财务公司", "selectable": false, "nodes": [{"text": "财务公司", "code": "160600"}]}]
        }, {
            "text": "公司企业",
            "selectable": false,
            "nodes": [{
                "text": "公司企业",
                "selectable": false,
                "nodes": [{"text": "公司企业", "code": "170000"}]
            }, {"text": "知名企业", "selectable": false, "nodes": [{"text": "知名企业", "code": "170100"}]}, {
                "text": "公司",
                "selectable": false,
                "nodes": [{"text": "公司", "code": "170200"}, {"text": "广告装饰", "code": "170201"}, {
                    "text": "建筑公司",
                    "code": "170202"
                }, {"text": "医药公司", "code": "170203"}, {"text": "机械电子", "code": "170204"}, {
                    "text": "冶金化工",
                    "code": "170205"
                }, {"text": "网络科技", "code": "170206"}, {"text": "商业贸易", "code": "170207"}, {
                    "text": "电信公司",
                    "code": "170208"
                }, {"text": "矿产公司", "code": "170209"}]
            }, {"text": "工厂", "selectable": false, "nodes": [{"text": "工厂", "code": "170300"}]}, {
                "text": "农林牧渔基地",
                "selectable": false,
                "nodes": [{"text": "其它农林牧渔基地", "code": "170400"}, {"text": "渔场", "code": "170401"}, {
                    "text": "农场",
                    "code": "170402"
                }, {"text": "林场", "code": "170403"}, {"text": "牧场", "code": "170404"}, {
                    "text": "家禽养殖基地",
                    "code": "170405"
                }, {"text": "蔬菜基地", "code": "170406"}, {"text": "水果基地", "code": "170407"}, {
                    "text": "花卉苗圃基地",
                    "code": "170408"
                }]
            }]
        }, {
            "text": "道路附属设施",
            "selectable": false,
            "nodes": [{
                "text": "道路附属设施",
                "selectable": false,
                "nodes": [{"text": "道路附属设施", "code": "180000"}]
            }, {
                "text": "警示信息",
                "selectable": false,
                "nodes": [{"text": "警示信息", "code": "180100"}, {"text": "摄像头", "code": "180101"}, {
                    "text": "测速设施",
                    "code": "180102"
                }, {"text": "铁路道口", "code": "180103"}, {"text": "违章停车", "code": "180104"}]
            }, {
                "text": "收费站",
                "selectable": false,
                "nodes": [{"text": "收费站", "code": "180200"}, {"text": "高速收费站", "code": "180201"}, {
                    "text": "国省道收费站",
                    "code": "180202"
                }, {"text": "桥洞收费站", "code": "180203"}]
            }, {
                "text": "服务区",
                "selectable": false,
                "nodes": [{"text": "高速服务区", "code": "180300"}, {"text": "高速加油站服务区", "code": "180301"}, {
                    "text": "高速停车区",
                    "code": "180302"
                }]
            }, {"text": "红绿灯", "selectable": false, "nodes": [{"text": "红绿灯", "code": "180400"}]}, {
                "text": "路牌信息",
                "selectable": false,
                "nodes": [{"text": "路牌信息", "code": "180500"}]
            }]
        }, {
            "text": "地名地址信息",
            "selectable": false,
            "nodes": [{
                "text": "地名地址信息",
                "selectable": false,
                "nodes": [{"text": "地名地址信息", "code": "190000"}]
            }, {
                "text": "普通地名",
                "selectable": false,
                "nodes": [{"text": "普通地名", "code": "190100"}, {"text": "国家名", "code": "190101"}, {
                    "text": "省级地名",
                    "code": "190102"
                }, {"text": "直辖市级地名", "code": "190103"}, {"text": "地市级地名", "code": "190104"}, {
                    "text": "区县级地名",
                    "code": "190105"
                }, {"text": "乡镇级地名", "code": "190106"}, {"text": "街道级地名", "code": "190107"}, {
                    "text": "村庄级地名",
                    "code": "190108"
                }, {"text": "村组级地名", "code": "190109"}]
            }, {
                "text": "自然地名",
                "selectable": false,
                "nodes": [{"text": "自然地名", "code": "190200"}, {"text": "海湾海峡", "code": "190201"}, {
                    "text": "岛屿",
                    "code": "190202"
                }, {"text": "山", "code": "190203"}, {"text": "河流", "code": "190204"}, {"text": "湖泊", "code": "190205"}]
            }, {
                "text": "交通地名",
                "selectable": false,
                "nodes": [{"text": "交通地名", "code": "190300"}, {"text": "道路名", "code": "190301"}, {
                    "text": "路口名",
                    "code": "190302"
                }, {"text": "环岛名", "code": "190303"}, {"text": "高速路出口", "code": "190304"}, {
                    "text": "高速路入口",
                    "code": "190305"
                }, {"text": "立交桥", "code": "190306"}, {"text": "桥", "code": "190307"}, {
                    "text": "城市快速路出口",
                    "code": "190308"
                }, {"text": "城市快速路入口", "code": "190309"}, {"text": "隧道", "code": "190310"}, {
                    "text": "铁路",
                    "code": "190311"
                }]
            }, {
                "text": "门牌信息",
                "selectable": false,
                "nodes": [{"text": "门牌信息", "code": "190400"}, {"text": "地名门牌", "code": "190401"}, {
                    "text": "道路门牌",
                    "code": "190402"
                }, {"text": "楼栋号", "code": "190403"}]
            }, {"text": "市中心", "selectable": false, "nodes": [{"text": "城市中心", "code": "190500"}]}, {
                "text": "标志性建筑物",
                "selectable": false,
                "nodes": [{"text": "标志性建筑物", "code": "190600"}]
            }, {"text": "热点地名", "selectable": false, "nodes": [{"text": "热点地名", "code": "190700"}]}]
        }, {
            "text": "公共设施",
            "selectable": false,
            "nodes": [{
                "text": "公共设施",
                "selectable": false,
                "nodes": [{"text": "公共设施", "code": "200000"}]
            }, {"text": "报刊亭", "selectable": false, "nodes": [{"text": "报刊亭", "code": "200100"}]}, {
                "text": "公用电话",
                "selectable": false,
                "nodes": [{"text": "公用电话", "code": "200200"}]
            }, {
                "text": "公共厕所",
                "selectable": false,
                "nodes": [{"text": "公共厕所", "code": "200300"}, {"text": "男洗手间", "code": "200301"}, {
                    "text": "女洗手间",
                    "code": "200302"
                }, {"text": "残障洗手间/无障碍洗手间", "code": "200303"}, {"text": "婴儿换洗间/哺乳室/母婴室", "code": "200304"}]
            }, {"text": "紧急避难场所", "selectable": false, "nodes": [{"text": "紧急避难场所", "code": "200400"}]}]
        }, {
            "text": "事件活动",
            "selectable": false,
            "nodes": [{
                "text": "事件活动",
                "selectable": false,
                "nodes": [{"text": "事件活动", "code": "220000"}]
            }, {
                "text": "公众活动",
                "selectable": false,
                "nodes": [{"text": "公众活动", "code": "220100"}, {"text": "节日庆典", "code": "220101"}, {
                    "text": "展会展览",
                    "code": "220102"
                }, {"text": "体育赛事", "code": "220103"}, {"text": "文艺演出", "code": "220104"}, {
                    "text": "大型会议",
                    "code": "220105"
                }, {"text": "运营活动", "code": "220106"}, {"text": "商场活动", "code": "220107"}]
            }, {
                "text": "突发事件",
                "selectable": false,
                "nodes": [{"text": "突发事件", "code": "220200"}, {"text": "自然灾害", "code": "220201"}, {
                    "text": "事故灾难",
                    "code": "220202"
                }, {"text": "城市新闻", "code": "220203"}, {"text": "公共卫生事件", "code": "220204"}, {
                    "text": "公共社会事件",
                    "code": "220205"
                }]
            }]
        }, {
            "text": "室内设施",
            "selectable": false,
            "nodes": [{"text": "室内设施", "selectable": false, "nodes": [{"text": "室内设施", "code": "970000"}]}]
        }, {
            "text": "通行设施",
            "selectable": false,
            "nodes": [{
                "text": "通行设施",
                "selectable": false,
                "nodes": [{"text": "通行设施", "code": "990000"}]
            }, {
                "text": "建筑物门",
                "selectable": false,
                "nodes": [{"text": "建筑物门", "code": "991000"}, {"text": "建筑物正门", "code": "991001"}]
            }, {
                "text": "临街院门",
                "selectable": false,
                "nodes": [{"text": "临街院门", "code": "991400"}, {"text": "临街院正门", "code": "991401"}]
            }]
        }]
        return data;
    }

    $('#poiTree').treeview({data: getTree(), multiSelect: true, levels: 1});
    $scope.submitForm = function () {
        var file = document.getElementById("file");
        var fileName = file.value;
        /*if($scope.taskInfo.import=="gp" && 
         ($scope.taskInfo.for == "poi" || $scope.taskInfo.for == "cell" )){
         alert("位置聚集点数据只支持行政区域查询!");
         return;
         }*/
        if (fileName == "") {
            alert("请上传文件!");
            return;
        } else if (!(fileName.substring(fileName.lastIndexOf(".")) == ".txt"
                //|| fileName.substring(fileName.lastIndexOf(".")) == ".csv"
                //|| fileName.substring(fileName.lastIndexOf(".")) == ".dat"
            )) {
            alert("上传文件类型必须为 .txt");
            return;
        }
        if ($scope.taskInfo.for == "poi") {
            var selArr = $('#poiTree').treeview(true).getSelected();
            var out = new Array();
            for (var sel in selArr) {
                var code = selArr[sel]["code"];
                out.push(code);
            }
            var types = selArr.length != 0 ? out.join("|") : "";
        } else {
            types = "";
        }
        var formElement = document.getElementById("taskForm");
        var fd = new FormData(formElement);
        fd.append("types", types);
        fd.append("file", file);
        $http({
            method: 'POST',
            url: $rootScope.submiturl,
            data: fd, /*
             crossDomain: true,
             xhrFields: {
             withCredentials: true
             },*/
            headers: {'Content-Type': undefined},
            transformRequest: angular.identity
        }).then(function successCallback(response) {
            alert("任务提交成功!");
            $state.go($scope.taskInfo.for == "poi" ? "poi" : "area");
        }, function errorCallback(response) {
            alert("任务提交失败!");
        });

    };
    $scope.back = function () {
        $window.history.back();
    }
    $scope.downloadgp = function () {
        window.open("resource/id_location_example.txt");
    }
    $scope.downloadco = function () {
        window.open("resource/latlng_example.txt");
    }
});

