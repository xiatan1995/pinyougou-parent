app.service('uploadService',function ($http) {
    this.upload=function () {
        var formdata=new formData;

        formdata.append("file",file.files[0]);
        formdata.appendData()
       return $http({
           method:'past',
           url:"../upload",
           Data:formdata,
           headers: {'Content-Type':undefined},

           //表单二进制序列化
           transformRequest: angular.identity

       });
    }
});