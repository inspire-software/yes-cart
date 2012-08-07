@echo off

rem
rem YesCart demo startup script
rem
rem @author Denys Pavlov

set RUNDIR=%CD%

set YC_HOME=%~dp0

rem Remove last slash if there is one
if "x%YC_HOME:~-1%".=="x\". (
    set YC_HOME=%YC_HOME:~0,-1%
)

cd %YC_HOME%

echo ================================================
echo  Starting YesCart from HOME: %YC_HOME%           
echo ================================================

echo ================================================
echo  Starting Derby database (background mode)      
echo ================================================

set DERBY_INSTALL=%YC_HOME%\yes-db
set CLASSPATH=%DERBY_INSTALL%\derby.jar;%DERBY_INSTALL%\derbytools.jar;%DERBY_INSTALL%\derbyclient.jar;%DERBY_INSTALL%\derbynet.jar;.
call cd %DERBY_INSTALL%

start "yes-db" cmd /K java -jar derbyrun.jar server start

echo  Derby server started on port 1527...

cd %YC_HOME%/yes-server/bin

echo ================================================
echo  Starting Tomcat Server                         
echo ================================================

set JAVA_OPTS="-Xmx512m -Xms512m -XX:MaxPermSize=256m"
start "yes-server" cmd /K startup.bat

cd %RUNDIR%


echo ================================================
echo  YesCart has been started successfully          
echo  YesShop URL http://localhost:8080/yes-shop     
echo  YesManager URL http://localhost:8080/yes-manager
echo ================================================

