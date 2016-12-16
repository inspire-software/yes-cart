<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%--
  ~ Copyright 2009-2016 Denys Pavlov, Igor Azarnyi
  ~
  ~    Licensed under the Apache License, Version 2.0 (the "License");
  ~    you may not use this file except in compliance with the License.
  ~    You may obtain a copy of the License at
  ~
  ~        http://www.apache.org/licenses/LICENSE-2.0
  ~
  ~    Unless required by applicable law or agreed to in writing, software
  ~    distributed under the License is distributed on an "AS IS" BASIS,
  ~    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  ~    See the License for the specific language governing permissions and
  ~    limitations under the License.
  --%>
<html>
<head>
    <meta charset="UTF-8">
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>YC - pure eCommerce</title>
    <link href="${pageContext.servletContext.contextPath}/resources/css/bootstrap.min.css" rel="stylesheet"/>
    <link href="${pageContext.servletContext.contextPath}/resources/css/font-awesome.min.css" rel="stylesheet"/>
    <link href="${pageContext.servletContext.contextPath}/resources/css/yc-main.css" rel="stylesheet"/>
</head>
<body>
<script type="text/javascript">
    <!--
    redirectToPage = "<c:url value='/resources/index.html'/>";
    setTimeout('redirectTo()', 100);
    function redirectTo() {
        window.location = redirectToPage;
    }
    //-->
</script>

<div class="container gear-top">
    <div class="row gear-bottom">
        <div class="col-sm-6 col-sm-offset-3 col-md-4 col-md-offset-4 vcenter">
            <form class="whitebg" name='loginForm' action='<c:url value='/resources/index.html' />' method='GET'>

                <div class="text-center clearfix">
                    <a href="http://yes-cart.org" target="_blank">
                        <img border="0" src="${pageContext.servletContext.contextPath}/resources/assets/img/logo-button-100x100.png"/>
                    </a>
                </div>

                <div class="text-center clearfix">
                    <img border="0" src="${pageContext.servletContext.contextPath}/resources/assets/img/ajax-loader.gif"/>
                </div>

                <div class="input-group input-sm pull-right">
                    <button type="submit"
                            class="btn btn-block btn-success btn-lg">
                        <i class="fa fa-arrow-circle-right"></i>
                    </button>
                </div>

                <div class="input-group input-sm">
                    Powered by <a href="http://yes-cart.org" target="_blank">YC - pure <sup>e</sup>Commerce</a><br/>
                    &copy; YesCart.org 2009 - <%= new java.text.SimpleDateFormat("yyyy").format(new java.util.Date()) %>
                </div>
            </form>
        </div>
    </div>
</div>
</body>
</html>
