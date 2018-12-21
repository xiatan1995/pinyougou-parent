//控制器
app.controller('brandController',function($scope,$http,$controller,brandService){

    $controller("besaController",{$scope:$scope})
    //刷新局部页面
    $scope.reloadList = function(){
        $scope.search($scope.paginationConf.currentPage,$scope.paginationConf.itemsPerPage);
    }

    $scope.save=function(){
        //判断是添加还是修改
        var ojb={};
        if ($scope.entity.id!=null){
            ojb=brandService.update($scope.entity);
        }else{
            ojb=brandService.add($scope.entity);

        }
        //添加数据/修改数据
        ojb.success(
            function (response) {
                if (response.success){
                    $scope.reloadList();
                } else{
                    alert(response.jieguo);
                }
            }
        )
    }

    //修改-数据回显
    $scope.findOne=function(id){
        brandService.findOne(id).success(
            function (response) {
                $scope.entity=response;
            })
    }

    //调用后台删除
    $scope.dele=function(){
        brandService.dele($scope.deleteid).success(function (response) {
            if (response.success){
                $scope.reloadList();
            } else{
                alert(response.jieguo);
            }
        })
    }

    $scope.searchEntity={};

    //条件查询
    $scope.search=function(page,size){
        brandService.search(page,size,$scope.searchEntity).success(
            function (responce) {
                $scope.list=responce.rows;
                $scope.paginationConf.totalItems=responce.total;//更新总记录数
            })
    }

})