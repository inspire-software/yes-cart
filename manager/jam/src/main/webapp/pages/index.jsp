<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%--
  Created by IntelliJ IDEA.
  User: Igor_Azarny
  Date: 3/11/2016
  Time: 9:52 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Title</title>
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

    if(browserLanguage == "ru" || browserLanguage == "be") {
        redirectToPage = "ru_RU/" + redirectToPage;
    } else if(browserLanguage == "ua") {
        redirectToPage = "uk_UK/" + redirectToPage;
    } else if(browserLanguage == "de") {
        redirectToPage = "de_DE/" + redirectToPage;
    } else {
        redirectToPage = "en_US/" + redirectToPage;
    }

    redirectToPage = "<c:url value='/resources/index.html'/>";

    setTimeout('redirectTo()', 1);
    function redirectTo() {
        window.location = redirectToPage;
    }
    //-->
</script>

</body>
</html>
