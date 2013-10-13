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
echo  Stopping YesCart from HOME: $YC_HOME           
echo ================================================

echo ================================================
echo  Stopping Tomcat Server                         
echo ================================================

call cd %YC_HOME%/yes-server/bin

call shutdown.bat

echo ================================================
echo  Stopping Derby database (background mode)      
echo ================================================

set DERBY_INSTALL=%YC_HOME%\yes-db
set CLASSPATH=%DERBY_INSTALL%\derby.jar;%DERBY_INSTALL%\derbytools.jar;%DERBY_INSTALL%\derbyclient.jar;%DERBY_INSTALL%\derbynet.jar;.
call cd %DERBY_INSTALL%

call java -jar derbyrun.jar server shutdown

echo  Derby server stopped...                        

cd %RUNDIR%

echo ================================================
echo  YesCart has been shutdown successfully         
echo ================================================
