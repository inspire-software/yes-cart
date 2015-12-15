<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<html ng-app="jaumApp">
<head>
  <link href="../../bootstrap/css/bootstrap.css" rel="stylesheet"/>
  <link href="../../style/style.css" rel="stylesheet"/>

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

  <hr>
  <div class="navbar-default sidebar" role="navigation">
    <div class="sidebar-nav navbar-collapse">
      <ul class="nav in" id="side-menu">
        <sidebar-search></sidebar-search>
        <li ui-sref-active="active">
          <a ui-sref="dashboard.home"><i class="fa fa-dashboard fa-fw"></i> Dashboard</a>
        </li>
        <li ui-sref-active="active">

          <a ui-sref="dashboard.chart"><i class="fa fa-bar-chart-o fa-fw"></i> Charts<span></span></a>

        </li>
        <li ui-sref-active="active">
          <a ui-sref="dashboard.table"><i class="fa fa-table fa-fw"></i> Tables</a>
        </li>
        <li ui-sref-active="active">
          <a ui-sref="dashboard.form"><i class="fa fa-edit fa-fw"></i> Forms</a>
        </li>
        <li ng-class="{active: collapseVar==1}">{{dropDown}}
          <a href="" ng-click="check(1)"><i class="fa fa-wrench fa-fw"></i> UI Elements<span
                  class="fa arrow"></span></a>
          <ul class="nav nav-second-level" collapse="collapseVar!=1">
            <li ui-sref-active="active">
              <a ui-sref="dashboard.panels-wells">Panels and Wells</a>
            </li>
            <li ui-sref-active="active">
              <a ui-sref="dashboard.buttons">Buttons</a>
            </li>
            <li ui-sref-active="active">
              <a ui-sref="dashboard.notifications">Notifications</a>
            </li>
            <li ui-sref-active="active">
              <a ui-sref="dashboard.typography">Typography</a>
            </li>
            <li ui-sref-active="active">
              <a ui-sref="dashboard.icons"> Icons</a>
            </li>
            <li ui-sref-active="active">
              <a ui-sref="dashboard.grid">Grid</a>
            </li>
          </ul>
          <!-- /.nav-second-level -->
        </li>
        <li ng-class="{active: collapseVar==2}">
          <a href="" ng-click="check(2)"><i class="fa fa-sitemap fa-fw"></i> Multi-Level Dropdown<span
                  class="fa arrow"></span></a>
          <ul class="nav nav-second-level" collapse="collapseVar!=2">
            <li>
              <a href="">Second Level Item</a>
            </li>
            <li>
              <a href="">Second Level Item</a>
            </li>
            <li ng-init="third=!third" ng-class="{active: multiCollapseVar==3}">
              <a href="" ng-click="multiCheck(3)">Third Level <span class="fa arrow"></span></a>
              <ul class="nav nav-third-level" collapse="multiCollapseVar!=3">
                <li>
                  <a href="">Third Level Item</a>
                </li>
                <li>
                  <a href="">Third Level Item</a>
                </li>
                <li>
                  <a href="">Third Level Item</a>
                </li>
                <li>
                  <a href="">Third Level Item</a>
                </li>

              </ul>
              <!-- /.nav-third-level -->
            </li>
          </ul>
          <!-- /.nav-second-level -->
        </li>
        <li ng-class="{active:collapseVar==4}">
          <a href="" ng-click="check(4)"><i class="fa fa-files-o fa-fw"></i> Sample Pages<span
                  class="fa arrow"></span></a>
          <ul class="nav nav-second-level" collapse="collapseVar!=4">
            <li ng-class="{active: selectedMenu=='blank'}">
              <a ui-sref="dashboard.blank" ng-click="selectedMenu='blank'">Blank Page</a>
            </li>
            <li>
              <a ui-sref="login">Login Page</a>
            </li>
          </ul>
          <!-- /.nav-second-level -->
        </li>
        <li><a href="http://www.strapui.com/">Premium Angular Themes</a></li>
      </ul>
    </div>
    <!-- /.sidebar-collapse -->
  </div>



<hr>


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
