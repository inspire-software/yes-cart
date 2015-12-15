angular.module('shopApp',[])

    .controller('ShopController', ['$scope', 'ShopService', function($scope, ShopService) {
        var self = this;
        self.shop={id:null,name:''};
        self.shops=[];

        self.fetchAllShops = function(){
            ShopService.fetchAll()
                .then(
                function(d) {
                    self.shops = d;
                },
                function(errResponse){
                    console.error('Error while fetching Shops');
                }
            );
        };

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

        /*self.submit = function() {
         if(self.user.id==null){
         console.log('Saving New User', self.user);
         self.createUser(self.user);
         }else{
         self.updateUser(self.user, self.user.id);
         console.log('User updated with id ', self.user.id);
         }
         self.reset();
         };

         self.edit = function(id){
         console.log('id to be edited', id);
         for(var i = 0; i < self.users.length; i++){
         if(self.users[i].id == id) {
         self.user = angular.copy(self.users[i]);
         break;
         }
         }
         };

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
    factory('ShopService', ['$http', '$q', function($http, $q){

        return {


             fetchAll: function() {

             return $http.get('../../service/shop/all')
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


