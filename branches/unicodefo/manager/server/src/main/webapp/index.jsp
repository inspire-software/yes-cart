<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Yes-shop managment panel</title>
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

        if(browserLanguage == "ru" || browserLanguage == "ua" || browserLanguage == "be") {
            document.write('<p align="center"><a href="'+redirectToPage+'">Старт</a></p>');
        } else {
			document.write('<p align="center"><a href="'+redirectToPage+'">Start</a></p>');
		}
		setTimeout('redirectTo()', 1);
		function redirectTo() {
			window.location = redirectToPage;
		}
        //-->
    </script>
</body>
</html>
