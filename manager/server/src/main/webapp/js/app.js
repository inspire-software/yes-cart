angular.module('jaumApp',['ngRoute', 'shopApp', 'ui.bootstrap'])

    .controller('MainController', function($scope, $route, $routeParams, $location) {
        $scope.$route = $route;
        $scope.$location = $location;
        $scope.$routeParams = $routeParams;
    })

    .config(function($routeProvider, $locationProvider) {
        $routeProvider.when(
            '/shop', {
                templateUrl: 'shop'
            }
        ).when(
            '/store/:storeId', {
                templateUrl: 'store'
            }
        )
    })

;


