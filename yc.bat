@echo off

rem
rem YesCart development script
rem @author Denys Pavlov


set RUNDIR=%CD%

set YC_HOME=%~dp0

rem Remove last slash if there is one
if "x%YC_HOME:~-1%".=="x\". (
    set YC_HOME=%YC_HOME:~0,-1%
)

set MVN="%M2_HOME%\bin\mvn.bat"
rem set MVN="%M3_HOME%\bin\mvn3.bat"

rem Default come back label
set comeBack=finish

if %1.==help. (
	goto show_help
)
if %1.==env. (
    call cd "%YC_HOME%"
	set comeBack=env
    goto show_env;
:env
    call cd "%RUNDIR%"
	goto finish
)
if %1.==i3rd. (
    call cd "%YC_HOME%"
	set comeBack=i3rd
    goto add_mvn_extra_dep
:i3rd
    call cd "%RUNDIR%"
	goto finish
)
if %1.==cpres. (
    call cd "%YC_HOME%"
	set comeBack=cpres
    goto cp_resources_to_webapp
:cpres
    call cd "%RUNDIR%"
	goto finish
)
if %1.==dbimysql. (
    call cd "%YC_HOME%"
	set comeBack=dbimysql
    goto init_db_mysql
:dbimysql
    call cd "%RUNDIR%"
	goto finish
)
if %1.==dbiderby. (
    call cd "%YC_HOME%"
	set comeBack=dbiderby
    goto init_db_derby
:dbiderby
    call cd "%RUNDIR%"
	goto finish
)
if %1.==derbygo. (
    call cd "%YC_HOME%"
	set comeBack=derbygo
    goto db_derby_go
:derbygo
    call cd "%RUNDIR%"
	goto finish
)
if %1.==derbygob. (
    call cd "%YC_HOME%"
	set comeBack=derbygob
    goto db_derby_gob
:derbygob
    call cd "%RUNDIR%"
	goto finish
)
if %1.==derbyend. (
    call cd "%YC_HOME%"
	set comeBack=derbyend
    goto db_derby_end
:derbyend
    call cd "%RUNDIR%"
	goto finish
)
if %1.==derbycon. (
    call cd "%YC_HOME%"
	set comeBack=derbycon
    goto db_derby_connect
:derbycon
    call cd "%RUNDIR%"
	goto finish
)
if %1.==luke. (
    call cd "%YC_HOME%"
	set comeBack=luke
    goto start_luke
:luke
    call cd "%RUNDIR%"
	goto finish
)

if %1.==nullsmtp. (
    call cd "%YC_HOME%"
	set comeBack=nullsmtp
    goto start_nullsmtp
:nullsmtp
    call cd "%RUNDIR%"
	goto finish
)

echo Provide a command...
goto show_help
goto finish
        
rem Sub routines below this comment

:show_env
    echo ================================================
    echo  Environment variables                          
    echo ================================================
    echo  user.name     : %HOME%
    echo  user.home     : %HOME%
    echo  YC_HOME       : %YC_HOME%
    echo  RUNNIG_HOME   : %RUNDIR%
    echo  ANT_HOME  : %ANT_HOME%
    echo  FLEX_HOME : %FLEX_HOME%
    echo  JAVA_HOME : %JAVA_HOME%
    echo  M2_HOME   : %M2_HOME%
    echo ================================================
	goto %comeBack%


:show_help
    echo ================================================
    echo  YesCart environment script                     
    echo ================================================
    echo  commands:                                      
    echo   env       - show environment variables        
    echo.                                                 
    echo   i3rd      - setup necessary artifacts 3rd     
    echo               party artifacts for maven         
    echo.                                                 
    echo   cpres     - copy extra resources to webapps
    echo.                                                 
    echo   luke      - start luke
    echo.
    echo   nullsmtp  - start DevNullSmtp
    echo.
    echo   dbimysql  - initialise db for mysql
    echo.                                                 
    echo   dbiderby  - initialise db for derby           
    echo   derbygo   - start derby server                
    echo   derbygob  - start derby server (in back mode) 
    echo   derbyend  - stop derby server                 
    echo   derbycon  - connect to derby with ij          
    echo ================================================
	goto %comeBack%
	
:add_mvn_extra_dep

    echo ================================================
    echo  Setup environment           
    echo ================================================
    echo Adding extra dependencies...

 rem   call cd "%YC_HOME%\env\setup\lib3rdparty"

 rem   call cd "%YC_HOME%"

    echo All dependencies are setup
    echo ================================================
	goto %comeBack%
	
	
:cp_resources_to_webapp

    echo ================================================
    echo  Copy resources to webapps
    echo ================================================

    echo  copying resources... manager/server
    call cd "%YC_HOME%\manager\server"
    call "%MVN%" validate -Ptemplates

    echo  copying resources... web/store
    call cd "%YC_HOME%\web\store"
    call "%MVN%" validate -Ptemplates

    echo  copying resources... web/api
    call cd "%YC_HOME%\web\api"
    call "%MVN%" validate -Ptemplates

	goto %comeBack%

	
:init_db_mysql

    echo ================================================
    echo  Initialise MySQL database                      
    echo ================================================

    set "DBINITSCRIPT=%YC_HOME%\env\setup\dbi\mysql\dbinit.sql"
    echo Running init script as root user %DBINITSCRIPT%
    call mysql -uroot -p < "%DBINITSCRIPT%"

    echo Initialisation complete.
	goto %comeBack%

	
	
	
:db_derby_go

    echo ================================================
    echo  Starting Derby database                        
    echo ================================================

	echo %YC_HOME%
    set "DERBY_INSTALL=%YC_HOME%\env\derby"
    set "CLASSPATH=%DERBY_INSTALL%\lib\derby.jar;%DERBY_INSTALL%\lib\derbytools.jar;%DERBY_INSTALL%\lib\derbyclient.jar;%DERBY_INSTALL%\lib\derbynet.jar;"
    call cd "%DERBY_INSTALL%\lib"

    call java -jar derbyrun.jar server start
	goto %comeBack%


	
:db_derby_gob

    echo ================================================
    echo  Starting Derby database (background mode)      
    echo ================================================

    set "DERBY_INSTALL=%YC_HOME%\env\derby"
    set "CLASSPATH="%DERBY_INSTALL%\lib\derby.jar;%DERBY_INSTALL%\lib\derbytools.jar;%DERBY_INSTALL%\lib\derbyclient.jar;%DERBY_INSTALL%\lib\derbynet.jar;"
    call cd "%DERBY_INSTALL%\lib"

    start java -jar derbyrun.jar server start

    echo  Derby server started on port 1527...           
    echo.
    echo  Tips:
    echo  * if you see 'started and ready to accept connections on port 1527' but no prompt, just hit ENTER
    echo  * if you encounter problems check http://www.inspire-software.com/confluence/display/YC3EN/Derby+quick+start
    echo.
	goto %comeBack%


	
:db_derby_end

    echo ================================================
    echo  Stopping Derby database                        
    echo ================================================

    set "DERBY_INSTALL=%YC_HOME%\env\derby"
    set "CLASSPATH=%DERBY_INSTALL%\lib\derby.jar;%DERBY_INSTALL%\lib\derbytools.jar;%DERBY_INSTALL%\lib\derbyclient.jar;%DERBY_INSTALL%\lib\derbynet.jar;"
    call cd "%DERBY_INSTALL%\lib"

    call java -jar derbyrun.jar server shutdown

    echo  Derby server stopped...                        
	goto %comeBack%


	
:init_db_derby

    echo ================================================
    echo  Initialise Derby database                      
    echo ================================================
    
    echo Setting Derby environment variables
    set "DERBY_INSTALL=%YC_HOME%\env\derby"
    set "CLASSPATH=%DERBY_INSTALL%\lib\derby.jar;%DERBY_INSTALL%\lib\derbytools.jar;%DERBY_INSTALL%\lib\derbyclient.jar;%DERBY_INSTALL%\lib\derbynet.jar;"
    call cd "%DERBY_INSTALL%\lib"

rem   call java org.apache.derby.tools.sysinfo
    set "DBINITSCRIPT=%YC_HOME%\env\setup\dbi\derby\dbinit.sql"
    echo Running init script %DBINITSCRIPT%
    call java -Dderby.system.home="%YC_HOME%" -Dij.outfile="%YC_HOME%\derbyinit.log" -Dderby.ui.codeset=UTF8 org.apache.derby.tools.ij "%DBINITSCRIPT%"
rem   call java -Dderby.system.home="%YC_HOME%" org.apache.derby.tools.ij "%DBINITSCRIPT%"
    
    call cd "%YC_HOME%;"
    echo Initialisation complete. See log: %YC_HOME%\derbyinit.log
	goto %comeBack%


	
:db_derby_connect

    echo ================================================
    echo  Starting Derby client                          
    echo ================================================

    set "DERBY_INSTALL=%YC_HOME%\env\derby"
    set "CLASSPATH=%DERBY_INSTALL%\lib\derby.jar;%DERBY_INSTALL%\lib\derbytools.jar;%DERBY_INSTALL%\lib\derbyclient.jar;%DERBY_INSTALL%\lib\derbynet.jar;"
    call cd "%DERBY_INSTALL%\lib"

    echo Working directory is
    echo %CD%
    
    echo Use:
    echo   connect 'jdbc:derby://localhost:1527/yes'; - to connect to main db
    echo   connect 'jdbc:derby://localhost:1527/yespay'; - to connect to payment gateway db
    echo   disconnect; - to quit
    
    call java -Dderby.ui.codeset=UTF8 org.apache.derby.tools.ij
	goto %comeBack%


:start_luke

    echo ================================================
    echo  Starting Luke (lucene index browser)
    echo ================================================

    start javaw -jar "%YC_HOME%\env\luke\lukeall-3.5.0.jar"

    goto %comeBack%


:start_nullsmtp

    echo ================================================
    echo  Starting DevNullSmtp (dummy SMTP server)
    echo ================================================

    call java -jar "%YC_HOME%\env\devnullsmtp\DevNullSmtp.jar"

    goto %comeBack%

:finish