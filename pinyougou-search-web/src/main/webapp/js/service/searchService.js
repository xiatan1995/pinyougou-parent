app.service('searchService',function ($http) {
    this.search2=function (searchMap) {
       return $http.post('itemsearch/search.do',searchMap);
    }
});