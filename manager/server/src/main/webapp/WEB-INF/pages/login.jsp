<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>


<%--
  ~ Copyright 2009 - 2016 Denys Pavlov, Igor Azarnyi
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
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>Login page</title>
    <link href="<c:url value='../../bootstrap/css/bootstrap.css' />" rel="stylesheet"></link>
</head>

<body>
<form name='loginForm' action='<c:url value="/j_spring_security_check"/>'
      method='POST'>
    <c:if test="${param.error != null}">
        <div class="alert alert-danger">
            <p>Invalid username and password.</p>
        </div>
    </c:if>
    <c:if test="${param.logout != null}">
        <div class="alert alert-success">
            <p>You have been logged out successfully.</p>
        </div>
    </c:if>
    <div class="input-group input-sm">
        <label class="input-group-addon" for="j_username"><i class="fa fa-user"></i></label>
        <input type="text" class="form-control" id="j_username" name="j_username" placeholder="Enter Username" required>
    </div>
    <div class="input-group input-sm">
        <label class="input-group-addon" for="j_password"><i class="fa fa-lock"></i></label>
        <input type="password" class="form-control" id="j_password" name="j_password" placeholder="Enter Password"
               required>
    </div>
    <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>

    <div class="form-actions">
        <input type="submit"
               class="btn btn-block btn-primary btn-default" value="Log in">
    </div>
</form>

</body>
</html>
