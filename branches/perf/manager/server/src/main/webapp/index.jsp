<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%--
  ~ Copyright 2009 Igor Azarnyi, Denys Pavlov
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
    <title>YUM Yes cart update manager</title>
    <link rel="stylesheet" href="style/style.css" type="text/css"/>
</head>
<body>
<script type="text/javascript">
    <!--

    var browserLanguage = "en" ;
    if (navigator.userLanguage) { // IE
        browserLanguage = navigator.userLanguage.substr(0,2);
    } else if (navigator.language) {// FF
        browserLanguage = navigator.language.substr(0,2);
    }

    var redirectToPage = "ShopManagerApplication.html";
    document.write('<p align="center"><a href="ru_RU/'+redirectToPage+'">Старт</a></p>');
    document.write('<p align="center"><a href="en_US/'+redirectToPage+'">Start</a></p>');

    if(browserLanguage == "ru" || browserLanguage == "ua" || browserLanguage == "be") {
        redirectToPage = "ru_RU/" + redirectToPage;
    } else {
        redirectToPage = "en_US/" + redirectToPage;
    }
    setTimeout('redirectTo()', 1);
    function redirectTo() {
        window.location = redirectToPage;
    }
    //-->
</script>
</body>
</html>
