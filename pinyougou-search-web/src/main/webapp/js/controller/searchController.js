app.controller('searchController',function($scope,$location,searchService){
	//定义搜索传递类
    $scope.searchMap={'keywords':'','category':'','brand':'','price':'','spec':{},'pageNo':1,'pageSize':40,'sort':'','sortField':''};

	//搜索
	$scope.search=function () {
	    $scope.searchMap.pageNo= parseInt($scope.searchMap.pageNo);
		searchService.search2($scope.searchMap).success(function (response) {
			$scope.resultMap=response;
			buildPageLabel();

        })
    }


    buildPageLabel=function(){
        //构建分页栏
        $scope.pageLabel=[];
        var firstPage=1;//开始页码
        var lastPage=$scope.resultMap.totalPages;//截止页码
        $scope.firstDot=true;//前面有点
        $scope.lastDot=true;//后边有点

        if($scope.resultMap.totalPages>5){  //如果页码数量大于5

            if($scope.searchMap.pageNo<=3){//如果当前页码小于等于3 ，显示前5页
                lastPage=5;
                $scope.firstDot=false;//前面没点
            }else if( $scope.searchMap.pageNo>= $scope.resultMap.totalPages-2 ){//显示后5页
                firstPage=$scope.resultMap.totalPages-4;
                $scope.lastDot=false;//后边没点
            }else{  //显示以当前页为中心的5页
                firstPage=$scope.searchMap.pageNo-2;
                lastPage=$scope.searchMap.pageNo+2;
            }
        }else{
            $scope.firstDot=false;//前面无点
            $scope.lastDot=false;//后边无点
        }


        //构建页码
        for(var i=firstPage;i<=lastPage;i++){
            $scope.pageLabel.push(i);
        }
    }

    // //构建分页标签
    // buildPageLabel=function(){
	//     $scope.pageLabel=[];
    //     var maxPageNo= $scope.resultMap.totalPages;//得到最后页码
    //     var firstPage=1;//开始页码
    //     var lastPage=maxPageNo;
    //     $scope.lastDot=true;
    //     $scope.firstDot=true;
    //     if ($scope.resultMap.totalPages>5){
    //
    //         if ($scope.searchMap.pageNo<=3){
    //             $scope.searchMap.pageNo=5;
    //             $scope.firstDot=false;
    //         } else {
    //
    //             if ($scope.searchMap.pageNo>=lastPage-2) {
    //                 $scope.lastDot=false;
    //                 $scope.searchMap.pageNo=lastPage-4;
    //             }else{
    //
    //                 firstPage= $scope.searchMap.pageNo-2;
    //                 lastPage = $scope.searchMap.pageNo+2;
    //             }
    //         }
    //     } else {
    //
    //         $scope.lastDot=false;
    //         $scope.firstDot=false;
    //
    //     }
    //
	//     for (var i=firstPage;i<=lastPage;i++){
    //         $scope.pageLabel.push(i);
    //     }
    //
    //
    // }

    //判断是否是第一页
    $scope.queryByPage=function(pageNo){

        if(pageNo<1 || pageNo>$scope.resultMap.totalPages){
            return;
        }
        $scope.searchMap.pageNo=pageNo;
        $scope.search();

    }

    //上一页

    $scope.isTopPage=function(){

        if ( $scope.searchMap.pageNo==1){


            return true;
        } else{

            return false;
        }

    }

    //下一页
    $scope.isEndPage=function(){


        if ( $scope.searchMap.pageNo==$scope.resultMap.totalPages){


            return true;
        } else{

            return false;
        }


    }

    //排序
    $scope.sortSearch=function(sortField,sort){

	    $scope.searchMap.sort=sort;

	    $scope.searchMap.sortField=sortField;

        $scope.search();


    }



    //实现选择值的添加
	$scope.addSearchItem=function (key, value) {
		if (key=='brand'|| key=='category' ||key=='price') {
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
        if (key=='brand'|| key=='category' || key=='price') {
            //选择的是品牌和分类
            $scope.searchMap[key]="";

        }else{

            //选择的是规格
           delete $scope.searchMap.spec[key];

        }
        $scope.search();

    }

    //隐藏品牌列表

    $scope.keywordsIsBrand=function () {

	    for (var i = 0; i < $scope.searchMap.brand.length; i++){


	        if ($scope.searchMap.keywords.indexOf($scope.searchMap.brand[i].text)>=0) {

	     return true;

            }
            }
            return false;
    }

    //建立连接
    $scope.loadkeywords=function(){
        $scope.searchMap.keywords=  $location.search()['keywords'];
        $scope.search();
    }


});