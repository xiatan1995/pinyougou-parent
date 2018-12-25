 //控制层 
app.controller('goodsController' ,function($scope,$controller ,uploadService  ,goodsService){	
	
	$controller('baseController',{$scope:$scope});//继承
	
    //读取列表数据绑定到表单中  
	$scope.findAll=function(){
		goodsService.findAll().success(
			function(response){
				$scope.list=response;
			}			
		);
	}    
	
	//分页
	$scope.findPage=function(page,rows){			
		goodsService.findPage(page,rows).success(
			function(response){
				$scope.list=response.rows;	
				$scope.paginationConf.totalItems=response.total;//更新总记录数
			}			
		);
	}
	
	//查询实体 
	$scope.findOne=function(id){				
		goodsService.findOne(id).success(
			function(response){
				$scope.entity= response;					
			}
		);				
	}
	
	//保存 
	// $scope.add=function(){
	// 	goodsService.add().success(
	// 		function(response){
	//
    //             $scope.entity.goodsDesc.introduction=editor.html();
	// 			if(response.success){
	// 				//重新查询
	// 	        	alert("添加成功");
	// 	        	$scope.entity={};
    //                 editor.html("");
	// 			}else{
	// 				alert(response.message);
	// 			}
	// 		}
	// 	);
	// }

    $scope.add=function(){
        $scope.entity.goodsDesc.introduction=editor.html();
        alert($scope.entity);
        goodsService.add( $scope.entity ).success(

            function(response){
                if(response.success){
                    alert("新增成功");
                    $scope.entity={};
                    editor.html("");//清空富文本编辑器
                }else{
                    alert(response.message);
                }
            }
        );
    }
	
	 
	//批量删除 
	$scope.dele=function(){			
		//获取选中的复选框			
		goodsService.dele( $scope.selectIds ).success(
			function(response){
				if(response.success){
					$scope.reloadList();//刷新列表
					$scope.selectIds=[];
				}						
			}		
		);				
	}
	
	$scope.searchEntity={};//定义搜索对象 
	
	//搜索
	$scope.search=function(page,rows){			
		goodsService.search(page,rows,$scope.searchEntity).success(
			function(response){
				$scope.list=response.rows;	
				$scope.paginationConf.totalItems=response.total;//更新总记录数
			}			
		);
	}


	//图片上传
	$scope.uploadFile=function () {
		uploadService.uploadFile().success(function (response) {
			if (response.success) {
				$scope.image_entity.url=response.jieguo;
			}else{
				alert(response.jieguo);
			}
        });
    }

    //图片列表

    $scope.entity={goods:{},goodsDesc:{itemImages:[]}};

	$scope.add_image_entity=function () {
		$scope.entity.goodsDesc.itemImages.push($scope.image_entity);
    }

    $scope.remove_image_entity=function (index) {
		$scope.entity.goodsDesc.itemImages.splice(index,1);
    }
    
});	
