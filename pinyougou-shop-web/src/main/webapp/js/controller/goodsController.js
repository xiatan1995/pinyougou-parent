 //控制层 
app.controller('goodsController' ,function($scope,$controller,$location ,uploadService,typeTemplateService,goodsService,itemCatService){
	
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
	$scope.findOne=function(){
		var id=$location.search()['id'];
		if (id==null){
			return ;
		}
    goodsService.findOne(id).success(
			function(response){
				$scope.entity= response;
                //向富文本编辑器添加商品介绍
                editor.html($scope.entity.goodsDesc.introduction);

                //商品图片片
                $scope.entity.goodsDesc.itemImages=JSON.parse($scope.entity.goodsDesc.itemImages);

				//扩展属性
				$scope.entity.goodsDesc.customAttributeItems=JSON.parse($scope.entity.goodsDesc.customAttributeItems);

				//规格选择
				$scope.entity.goodsDesc.specificationItems=JSON.parse($scope.entity.goodsDesc.specificationItems);

				for (var i=0;i<$scope.entity.goodsDesc.specificationItems.length;i++) {
					$scope.entity.itemList[i].spec=JSON.parse($scope.entity.itemList[i].spec);

				}



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

		//从模块里查寻品牌列表
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

	$scope.updateSpecAttribute=function ($event,name, value) {


	var object=$scope.searchObjectByKey($scope.entity.goodsDesc.specificationItems,'attributeName',name);

	if (object!=null){
        if ($event.target.checked){
            object.attributeValue.push(value);
			}else{
                object.attributeValue.splice(object.attributeValue.indexOf(value),1);
                if ( object.attributeValue.length==0){
                    $scope.entity.goodsDesc.specificationItems.splice($scope.entity.goodsDesc.specificationItems.indexOf(name),1)

			}
		}
	} else {

        $scope.entity.goodsDesc.specificationItems.push({"attributeName":name,"attributeValue":[value]});

	}
    }



    $scope.createItemList=function () {

	    $scope.entity.itemList=[{spec:{},price:0,num:1234,status:'0',isDefault:'0'}];//初始化


        var items=$scope.entity.goodsDesc.specificationItems;

        //王entity。itemlist存数据,增加列
        for (var i=0;i<items.length;i++){


            $scope.entity.itemList=addColumn($scope.entity.itemList,items[i].attributeName,items[i].attributeValue);

        }

    }


    addColumn=function (list, columnName, conlumnValues) {

	    var newList=[];

	    for (var i=0;i<list.length;i++){
	        var oldRow=list[i];
	        //为什么要使用克隆，因为每次循环时都要增加记录

	        for (var j=0;j<conlumnValues.length;j++){
	            //需要多次使用list[i]，在forj的每次循环中每次的list[i]必须是同一个值
	            //如果在这直接使用oldRow的话，第一次循环值改变以后第二次就没有了原来的list[i]
	            var newRow=JSON.parse(JSON.stringify(oldRow));//深度克隆

                newRow.spec[columnName]=conlumnValues[j];

                newList.push(newRow);

            }
        }

	    return newList;

    }



    //审核状态封装

	$scope.status=['未审核','已审核','审核未通过','关闭'];


	//添加分类级别

	$scope.itemCatList=[];

	$scope.findItemCatList=function () {

		itemCatService.findAll().success(function (response) {
			for (var i=0;i<response.length;i++){
                $scope.itemCatList[response[i].id]=response[i].name;
			}

        });
    }


    //添加规格回显勾选的显示的方法

	$scope.checkAttributeValue=function (specName,optionName) {
		var items=$scope.entity.goodsDesc.specificationItems;

		var object=$scope.searchObjectByKey(items,'attributeName',specName);
		
		
		if (object!=null){
		if (object.attributeValue.indexOf(optionName)>=0){
			return true;
		} 	else {

			return false;
		}
			
		} else {

			return false;

        }
    }






    



 });
