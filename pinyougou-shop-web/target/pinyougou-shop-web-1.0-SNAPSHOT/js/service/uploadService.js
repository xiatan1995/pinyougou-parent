// app.service('uploadService',function ($http) {
//     this.upload=function () {
//         var formdata=new formData;
//
//         formdata.append("file",file.files[0]);
//         formdata.appendData()
//        return $http({
//            method:'past',
//            url:"../upload",
//            Data:formdata,
//            headers: {'Content-Type':undefined},
//
//            //表单二进制序列化
//            transformRequest: angular.identity
//
//        });
//     }
// });

app.service("uploadService",function($http){
    this.uploadFile=function(){
        var formData=new FormData();
        formData.append("file",file.files[0]);
        return $http({
            method:'POST',
            url:"../upload.do",
            data: formData,
            headers: {'Content-Type':undefined},
            transformRequest: angular.identity
        });
    }
});


