<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html ng-app="jaumApp">
<head>
    <link href="../../bootstrap/css/bootstrap.css" rel="stylesheet"/>
    <link href="../../style/style.css" rel="stylesheet"/>

    <script src="../../angular/angular.js"></script>
    <script src="../../angular/angular-route.js"></script>
    <script src="../../angular/angular-resource.js"></script>
    <script src="../../ui-bootstrap/ui-bootstrap-0.14.3.js"></script>
    <script src="../../ui-bootstrap/ui-bootstrap-tpls-0.14.3.js"></script>
    <script src="../../js/app.js"></script>
    <script src="../../js/shop-app.js"></script>
</head>
<body>

<header>
    <nav class="navbar navbar-default">
        <div class="container">
            <div class="navbar-header">
                <a class="navbar-brand" href="/">Yes-Cart Admin</a>
            </div>
            <ul class="nav navbar-nav navbar-right">
                <li><a href="#"><i class="fa fa-home"></i> Home</a></li>
                <li><a href="#about"><i class="fa fa-shield"></i> About</a></li>
                <li><a href="#contact"><i class="fa fa-comment"></i> Contact</a></li>
            </ul>
        </div>
    </nav>
</header>



<div class="container">
    <div class="row">
        <div class="col-md-3" ng-controller="MainController">
            <uib-accordion>
                <uib-accordion-group>
                    <uib-accordion-heading>
                        Customer Service
                    </uib-accordion-heading>
                    <ul class="nav">
                        <li role="presentation"><a href="#/orders">Orders</a></li>
                        <li role="presentation"><a href="#/payments">Payments</a></li>
                        <li role="presentation"><a href="#/customers">Customers</a></li>
                    </ul>
                </uib-accordion-group>
                <uib-accordion-group>
                    <uib-accordion-heading>
                        PIM &amp; Master catalog
                    </uib-accordion-heading>
                </uib-accordion-group>
                <uib-accordion-group>
                    <uib-accordion-heading>
                        Content
                    </uib-accordion-heading>
                </uib-accordion-group>
                <uib-accordion-group>
                    <uib-accordion-heading>
                        Inventory
                    </uib-accordion-heading>
                </uib-accordion-group>
                <uib-accordion-group>
                    <uib-accordion-heading>
                        Reports
                    </uib-accordion-heading>
                </uib-accordion-group>
                <uib-accordion-group>
                    <uib-accordion-heading>
                        Shipping
                    </uib-accordion-heading>
                </uib-accordion-group>
                <uib-accordion-group>
                    <uib-accordion-heading>
                        Stores
                    </uib-accordion-heading>
                    <ul class="nav" ng-controller="ShopController as ctrl">
                        <li role="presentation" ng-repeat="store in ctrl.shops" ><a href="#/store/{{store.id}}" >{{store.name}}</a></li>
                        <li role="presentation"><a href="#/store/new">Create</a></li>
                        <li role="presentation"><a href="#/shop">Shop</a></li>
                    </ul>
                </uib-accordion-group>
                <uib-accordion-group>
                    <uib-accordion-heading>
                        Employees
                    </uib-accordion-heading>
                </uib-accordion-group>
                <uib-accordion-group>
                    <uib-accordion-heading>
                        System
                    </uib-accordion-heading>
                </uib-accordion-group>
                <uib-accordion-group>
                    <uib-accordion-heading>
                        Help
                    </uib-accordion-heading>
                </uib-accordion-group>
            </uib-accordion>
        </div>
        <div class="col-md-9">
            <div ng-view>
                <!--  main view place  -->
            </div>
            <hr/>
        </div>
    </div>

</div>






</body>
</html>
