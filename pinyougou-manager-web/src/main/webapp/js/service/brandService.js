//创建服务
app.service("brandService",function ($http) {

    //搜索/分页
    this.search=function (page,size,searchEntity) {
        return $http.post('../brand/search.do?page='+page+'&size='+size,searchEntity);

    }

    //删除
    this.dele=function (deleteid) {
        return $http.get('../brand/delete.do?ids='+deleteid)
    }

    //修改回显
    this.findOne=function (id) {
        return $http.get('../brand/findOne.do?id='+id);
    }

    //修改
    this.update=function (entity) {
        return $http.post('../brand/update.do',entity);
    }

    //添加
    this.add=function (entity) {
        return $http.post('../brand/add.do',entity);
    }

    //下拉框数据
    this.selectOptionList=function () {
        return $http.post('../brand/selectOptionList.do');
    }

});