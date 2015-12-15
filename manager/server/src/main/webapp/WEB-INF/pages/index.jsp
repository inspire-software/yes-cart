<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<html ng-app="jaumApp">
<head>
  <link href="../../bootstrap/css/bootstrap.css" rel="stylesheet"/>
  <script src="../../angular/angular.js"></script>
  <script src="../../angular/angular-route.js"></script>
  <script src="../../js/app.js"></script>
  <script src="../../js/shop-app.js"></script>
</head>
<body>

<header>
  <nav class="navbar navbar-default">
    <div class="container">
      <div class="navbar-header">
        <a class="navbar-brand" href="/">Angular Routing Example</a>
      </div>
      <ul class="nav navbar-nav navbar-right">
        <li><a href="#"><i class="fa fa-home"></i> Home</a></li>
        <li><a href="#about"><i class="fa fa-shield"></i> About</a></li>
        <li><a href="#contact"><i class="fa fa-comment"></i> Contact</a></li>
      </ul>
    </div>
  </nav>
</header>


<div ng-controller="MainController">
  Choose:
  <a href="#/shop">Shop</a> |
  <a href="#/Book/Moby">Moby</a>

  <div ng-view>
    ng-view
  </div>

  <hr />

  <pre>$location.path() = {{$location.path()}}</pre>
  <pre>$route.current.templateUrl = {{$route.current.templateUrl}}</pre>
  <pre>$route.current.params = {{$route.current.params}}</pre>
  <pre>$route.current.scope.name = {{$route.current.scope.name}}</pre>
  <pre>$routeParams = {{$routeParams}}</pre>

</div>



</body>
</html>
