angular.module('jaumApp',['ngRoute'])

    .controller('MainController', function($scope, $route, $routeParams, $location) {

            console.info("sadfasdfdsaf");
        $scope.$route = $route;
        $scope.$location = $location;
        $scope.$routeParams = $routeParams;

    })


    .config(function($routeProvider, $locationProvider) {
            $routeProvider.when(
                '/Book/:bookId', {
                    templateUrl: 'book.html'
                }
            )
        })

;


