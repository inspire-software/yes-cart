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
    <script>
        window.opener.postMessage("ok", '*');
    </script>
</head>
<body>
<div class="container">
  <div class="row">
    <div class="col-xs-12 text-center">
      <button type="button" onclick="javascript:window.close()"
              class="btn btn-circle btn-success btn-lg">
        <i class="fa fa-check"></i>
      </button>
    </div>
  </div>
</div>
</body>
</html>
