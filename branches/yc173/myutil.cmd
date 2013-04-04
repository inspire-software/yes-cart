rem call mvn clean install 
rem  -DskipTests=true 
rem -Pmysql

rd /q/s D:\dev\apache-tomcat-7.0.23\webapps\yes-manager
rd /q/s D:\dev\apache-tomcat-7.0.23\webapps\yes-shop

mkdir D:\dev\apache-tomcat-7.0.23\webapps\yes-manager
mkdir D:\dev\apache-tomcat-7.0.23\webapps\yes-shop

xcopy /i /f /e D:\dev\yes-cart\manager\server\target\yes-manager D:\dev\apache-tomcat-7.0.23\webapps\yes-manager
xcopy /i /f /e D:\dev\yes-cart\web\store\target\yes-shop D:\dev\apache-tomcat-7.0.23\webapps\yes-shop

cd D:\dev\yes-cart\manager\client
call ant
cd D:\dev\yes-cart
copy D:\dev\yes-cart\manager\client\target\ru_RU\ShopManagerApplication.swf D:\dev\apache-tomcat-7.0.23\webapps\yes-manager\ShopManagerApplication.swf