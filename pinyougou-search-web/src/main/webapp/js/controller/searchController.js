app.controller('searchController',function($scope,searchService){
	//定义搜索传递类
    $scope.searchMap={'keywords':'','category':'','brand':'','spec':{}};

	//搜索
	$scope.search=function () {
		searchService.search2($scope.searchMap).success(function (response) {
			$scope.resultMap=response;
        })
    }

    //实现选择值的添加

	$scope.addSearchItem=function (key, value) {
		if (key=='brand'|| key=='category') {
			//选择的是品牌和分类
            $scope.searchMap[key]=value;

		}else{

			//选择的是规格
            $scope.searchMap.spec[key]=value;

		}
        $scope.search();

		
    }


    //删除前台选中框

	$scope.removeSearchItem=function (key) {
        if (key=='brand'|| key=='category') {
            //选择的是品牌和分类
            $scope.searchMap[key]="";

        }else{

            //选择的是规格
           delete $scope.searchMap.spec[key];

        }
        $scope.search();

    }




	
});