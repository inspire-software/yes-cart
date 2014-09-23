@echo off

rem
rem YesCart. Open Yes-Cart manager
rem @author Igor Azarny (iazarny@yahoo.com)

echo  tomcat shutdown 
cd ..\bin
call shutdown.bat

echo derby shutdown
cd ..\derbydb\bin
call start stopNetworkServer.bat

exit
