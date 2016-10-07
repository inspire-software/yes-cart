angular.module('shopApp', ['ngResource'])

    .controller('ShopController', ['$scope', '$routeParams', 'ShopService',  function ($scope, $routeParams ,ShopService) {
        var self = this;
        self.shops = [];
        self.shop = {shopId: null, code: '', name: '', description: ''};

        if ($routeParams.storeId == 'new') {
            self.shop = {};
            $scope.shop = {};
        } else if (!isNaN($routeParams.storeId)) {
            ShopService.fetchOne($routeParams.storeId).then(
                function (d) {
                    self.shop = d;
                    $scope.shop = d;
                },
                function (errResponse) {
                    console.error('Error while fetching Shops');
                }
            );
        }

        self.fetchAllShops = function () {
            ShopService.fetchAll()
                .then(
                function (d) {
                    self.shops = d;
                },
                function (errResponse) {
                    console.error('Error while fetching Shops');
                }
            );
        };

        self.saveOrUpdate = function(s) {
            if (s.shopId == null) {
                console.log('Saving new store', s);
                ShopService.createShop(s);
            } else {
                console.log('Update store', s);
                ShopService.updateShop(s);
            }
        };



        /*self.edit = function (id) {
            console.log('store id to be edited', id);
            console.log('seelf', self);
            ShopService.fetchOne(id).then(
                function (d) {
                    self.shop = d;
                    $scope.shop = d;
                    $scope.zzz = 'asdfadsfadfadsf';
                    $location.path('/store/'+id)
                },
                function (errResponse) {
                    console.error('Error while fetching Shops');
                }
            );
        };*/



        /*self.createUser = function(user){
         UserService.createUser(user)
         .then(
         self.fetchAllUsers,
         function(errResponse){
         console.error('Error while creating User.');
         }
         );
         };

         self.updateUser = function(user, id){
         UserService.updateUser(user, id)
         .then(
         self.fetchAllUsers,
         function(errResponse){
         console.error('Error while updating User.');
         }
         );
         };

         self.deleteUser = function(id){
         UserService.deleteUser(id)
         .then(
         self.fetchAllUsers,
         function(errResponse){
         console.error('Error while deleting User.');
         }
         );
         };*/

        self.fetchAllShops();



        /*



         self.remove = function(id){
         console.log('id to be deleted', id);
         for(var i = 0; i < self.users.length; i++){
         if(self.users[i].id == id) {
         self.reset();
         break;
         }
         }
         self.deleteUser(id);
         };


         self.reset = function(){
         self.user={id:null,username:'',address:'',email:''};
         $scope.myForm.$setPristine(); //reset Form
         };*/

    }])
    .
    factory('ShopService', ['$http', '$resource', '$q', function ($http, $resource, $q) {

        return {
            fetchAll: function () {
                return $http.get('../../service/shop/all')
                    .then(
                    function (response) {
                        return response.data;
                    },
                    function (errResponse) {
                        console.error('Error while fetching shops');
                        return $q.reject(errResponse);
                    }
                );
            },
            fetchOne: function (id) {
                return $http.get('../../service/shop/' + id)
                    .then(
                    function (response) {
                        return response.data;
                    },
                    function (errResponse) {
                        console.error('Error while fetching shop');
                        return $q.reject(errResponse);
                    }
                );
            },
            createShop: function(shop){
                return $http.post('../../service/shop/', shop)
                    .then(
                    function(response){
                        return response.data;
                    },
                    function(errResponse){
                        console.error('Error while creating shop');
                        return $q.reject(errResponse);
                    }
                );
            },

            updateShop: function(shop){
                return $http.put('../../service/shop/', shop)
                    .then(
                    function(response){
                        return response.data;
                    },
                    function(errResponse){
                        console.error('Error while updating shop');
                        return $q.reject(errResponse);
                    }
                );
            },

            /*fetchOne: function (id) {
                return $resource('../../service/shop/:shopId', { shopId: '@shopId' }).get({ shopId: id }).$promise
                    .then(
                    function (response) {
                        var ddd = response.data;
                        console.info(">>>>>>>>>>>>>>>>>> " + ddd);
                        return response.data;
                    },
                    function (errResponse) {
                        console.error('Error while fetching shop');
                        return $q.reject(errResponse);
                    }
                );
            },*/


            /* fetchAll: function() {
             return $q.when(eval('[{ "id" : 1, "name" : "Ivn" }, { "id" : 2, "name" : "QWqwdsfddfdsaf" }]'));
             },*/



            /*
             fetchAllUsers: function() {
             return $http.get('http://localhost:8080/Spring4MVCAngularJSExample/user/')
             .then(
             function(response){
             return response.data;
             },
             function(errResponse){
             console.error('Error while fetching users');
             return $q.reject(errResponse);
             }
             );
             },
             */

            /*
             createUser: function(user){
             return $http.post('http://localhost:8080/Spring4MVCAngularJSExample/user/', user)
             .then(
             function(response){
             return response.data;
             },
             function(errResponse){
             console.error('Error while creating user');
             return $q.reject(errResponse);
             }
             );
             },

             updateUser: function(user, id){
             return $http.put('http://localhost:8080/Spring4MVCAngularJSExample/user/'+id, user)
             .then(
             function(response){
             return response.data;
             },
             function(errResponse){
             console.error('Error while updating user');
             return $q.reject(errResponse);
             }
             );
             },

             deleteUser: function(id){
             return $http.delete('http://localhost:8080/Spring4MVCAngularJSExample/user/'+id)
             .then(
             function(response){
             return response.data;
             },
             function(errResponse){
             console.error('Error while deleting user');
             return $q.reject(errResponse);
             }
             );
             }
             */

        };

    }]);


