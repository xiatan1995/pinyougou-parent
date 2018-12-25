 //控制层 
app.controller('goodsController' ,function($scope,$controller ,uploadService,typeTemplateService,goodsService,itemCatService){
	
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

    $scope.entity={goods:{},goodsDesc:{itemImages:[],specificationItems:[]}};

	$scope.add_image_entity=function () {
		$scope.entity.goodsDesc.itemImages.push($scope.image_entity);
    }

    $scope.remove_image_entity=function (index) {
		$scope.entity.goodsDesc.itemImages.splice(index,1);
    }

    //规格下拉列表

    $scope.selectItemCat1List=function () {
        itemCatService.findByParentId(0).success(function (response) {
            $scope.itemCat1List=response;
        })
    }

    //规格二级目录
    $scope.$watch('entity.goods.category1Id',function (newValue, oldValue) {

        itemCatService.findByParentId(newValue).success(function (response) {
            $scope.itemCat2List=response;
        });

    });

	//商品下拉框三级
	$scope.$watch('entity.goods.category2Id',function (newValue, oldValue) {
        itemCatService.findByParentId(newValue).success(function (response) {
            $scope.itemCat3List=response;
        });
    });

	//模块id回显
    $scope.$watch('entity.goods.category3Id', function(newValue, oldValue) {
        itemCatService.findOne(newValue).success(
            function(response){
                $scope.entity.goods.typeTemplateId=response.typeId; //更新模板ID
            }
        );
    });

    //品牌列表展示

	$scope.$watch('entity.goods.typeTemplateId',function (newValue, oldValue) {

		typeTemplateService.findOne(newValue).success(function (response) {

			$scope.typeTemplate=response;

            $scope.typeTemplate.brandIds= JSON.parse($scope.typeTemplate.brandIds);

            $scope.entity.goodsDesc.customAttributeItems=JSON.parse($scope.typeTemplate.customAttributeItems);

        });

        typeTemplateService.findSpecList(newValue).success(
            function(response){
                $scope.specList=response;
            });

    });


	//商品复选框选中的值

	// $scope.updateSpecAttribute=function ($event,name, value) {
	//
	//
	// var object=$scope.searchObjectByKey($scope.entity.goodsDesc.specificationItems,name,value);
	//
	// if (object!=null){
    //     if ($event.target.checked){
    //         object.attributeValue.push(value);
	// 		}else{
    //             object.attributeValue.splice(object.attributeValue.indexOf(value),1);
    //             if ( object.attributeValue.length==0){
    //                 $scope.entity.goodsDesc.specificationItems.splice($scope.entity.goodsDesc.specificationItems.indexOf(name),1)
    //
	// 		}
	// 	}
	// } else {
	//
    //     $scope.entity.goodsDesc.specificationItems.push({"attributeName":name,"attributeValue":[value]});
	//
	// }
    // }
    $scope.updateSpecAttribute=function($event,name,value){
        var object= $scope.searchObjectByKey(
            $scope.entity.goodsDesc.specificationItems ,'attributeName', name);
        if(object!=null){
            if($event.target.checked ){
                object.attributeValue.push(value);
            }else{//取消勾选				object.attributeValue.splice( object.attributeValue.indexOf(value ) ,1);//移除选项
                //如果选项都取消了，将此条记录移除
                if(object.attributeValue.length==0){
                    $scope.entity.goodsDesc.specificationItems.splice(
                        $scope.entity.goodsDesc.specificationItems.indexOf(object),1);
                }
            }
        }else{
            $scope.entity.goodsDesc.specificationItems.push(
                {"attributeName":name,"attributeValue":[value]});
        }
    }




 });
