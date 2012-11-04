@echo off

rem
rem YesCart. Open Yes-Cart manager
rem @author Igor Azarny (iazarny@yahoo.com)


echo derby shutdown
start %~dp0\stopNetworkServer.bat

exit 0
