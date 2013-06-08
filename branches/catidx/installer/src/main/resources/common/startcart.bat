@echo off

rem
rem YesCart. Start db and tomcat
rem @author Igor Azarny (iazarny@yahoo.com)

cd ..\derbydb\bin
call start startNetworkServer.bat
cd ..\..\bin
call catalina start

exit
