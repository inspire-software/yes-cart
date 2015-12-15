angular.module('jaumApp',['ngRoute', 'shopApp'])

    .controller('MainController', function($scope, $route, $routeParams, $location) {
        $scope.$route = $route;
        $scope.$location = $location;
        $scope.$routeParams = $routeParams;
    })

    .config(function($routeProvider, $locationProvider) {
        $routeProvider.when(
            '/Book/:bookId', {
                templateUrl: 'book.html'
            }
        ).when(
            '/shop', {
                templateUrl: 'shop'
            }
        )

    })

;


