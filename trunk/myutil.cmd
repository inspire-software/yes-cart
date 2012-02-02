rem mvn install -DskipTests=true -Pmysql
xcopy /i /f /e D:\dev\yes-cart\manager\server\target\yes-manager D:\dev\apache-tomcat-7.0.14\webapps\yes-manager
xcopy /i /f /e D:\dev\yes-cart\web\store\target\yes-shop D:\dev\apache-tomcat-7.0.14\webapps\yes-shop

cd D:\dev\yes-cart\manager\client
call ant
cd D:\dev\yes-cart
copy D:\dev\yes-cart\manager\client\target\en_US\ShopManagerApplication.swf D:\dev\apache-tomcat-7.0.14\webapps\yes-manager\ShopManagerApplication.swf