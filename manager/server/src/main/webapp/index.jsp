<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>E-Shop managment panel</title>
    <link rel="stylesheet" href="style/style.css" type="text/css"/>
</head>
<body>
    <script type="text/javascript">
        <!--
        var browserLanguage = "en" ;
        if (navigator.userLanguage) { // IE
            browserLanguage = navigator.userLanguage.substr(0,2);;
        } else if (navigator.language) {// FF
            browserLanguage = navigator.language.substr(0,2);;
        }
        var redirectToPage = "en_EN/index.html";
        if(browserLanguage == "ru" || browserLanguage == "ua" || browserLanguage == "be") {
            redirectToPage = "ru_RU/index.html";
			document.write('<p align="center"><a href="ru_RU/index.html">Приложение будет загруженно через 3 секунды</a></p>');
			document.write('<p align="center"><a href="en_EN/index.html">Click here to force load english version</a></p>');
        } else {
			document.write('<p align="center"><a href="en_EN/index.html">Application will be loaded in 3 seconds</a></p>');
			document.write('<p align="center"><a href="ru_RU/index.html">Нажми здесь для запуска русской версии</a></p>');
		}
		setTimeout('redirectTo()', 3000);
		function redirectTo() {
			window.location = redirectToPage;
		}
        //-->
    </script>
</body>
</html>
