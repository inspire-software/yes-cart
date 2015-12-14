<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<html>
<head>
  <!-- SCROLLS -->
  <!-- load bootstrap and fontawesome via CDN -->
  <link href="<c:url value='../../bootstrap/css/bootstrap.css' />" rel="stylesheet"></link>

  <!-- SPELLS -->
  <!-- load angular and angular route via CDN -->
<%--
  <script src="<c:url value='../../angular/angular.js' />"></script>
  <script src="<c:url value='../../angular/angular-route.js' />"></script>
--%>


  <script src="https://cdnjs.cloudflare.com/ajax/libs/angular.js/1.5.0-rc.0/angular.js"></script>
  <script src="https://cdnjs.cloudflare.com/ajax/libs/angular.js/1.5.0-rc.0/angular-route.js"></script>

</head>
<body>

<!-- HEADER AND NAVBAR -->
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

<!-- MAIN CONTENT AND INJECTED VIEWS -->
<div id="main">

  < angular templating >
  < this is where content will be injected >

</div>

</body>
</html>
