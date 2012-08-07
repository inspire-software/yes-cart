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

set MVN=%M2_HOME%\bin\mvn.bat
rem set MVN=%M3_HOME%\bin\mvn3.bat

rem Default come back label
set comeBack=finish

if %1.==help. (
	goto show_help
)
if %1.==env. (
    call cd %YC_HOME%
	set comeBack=env
    goto show_env;
:env
    call cd %RUNDIR%
	goto finish
)
if %1.==i3rd. (
    call cd %YC_HOME%
	set comeBack=i3rd
    goto add_mvn_extra_dep
:i3rd
    call cd %RUNDIR%
	goto finish
)
if %1.==cpclient. (
    call cd %YC_HOME%
	set comeBack=cpclient
    goto cp_client_to_webapp
:cpclient
    call cd %RUNDIR%
	goto finish
)
if %1.==dbimysql. (
    call cd %YC_HOME%
	set comeBack=dbimysql
    goto init_db_mysql
:dbimysql
    call cd %RUNDIR%
	goto finish
)
if %1.==dbiderby. (
    call cd %YC_HOME%
	set comeBack=dbiderby
    goto init_db_derby
:dbiderby
    call cd %RUNDIR%
	goto finish
)
if %1.==derbygo. (
    call cd %YC_HOME%
	set comeBack=derbygo
    goto db_derby_go
:derbygo
    call cd %RUNDIR%
	goto finish
)
if %1.==derbygob. (
    call cd %YC_HOME%
	set comeBack=derbygob
    goto db_derby_gob
:derbygob
    call cd %RUNDIR%
	goto finish
)
if %1.==derbyend. (
    call cd %YC_HOME%
	set comeBack=derbyend
    goto db_derby_end
:derbyend
    call cd %RUNDIR%
	goto finish
)
if %1.==derbycon. (
    call cd %YC_HOME%
	set comeBack=derbycon
    goto db_derby_connect
:derbycon
    call cd %RUNDIR%
	goto finish
)
if %1.==clndemo. (
    call cd %YC_HOME%
	set comeBack=clndemo
    goto prepare_demo_clean
:clndemo
    call cd %RUNDIR%
	goto finish
)
if %1.==pkgdemo. (
    call cd %YC_HOME%
	set comeBack=pkgdemo1
    goto prepare_demo_clean
:pkgdemo1
	set comeBack=pkgdemo2
    goto prepare_demo_pkg
:pkgdemo2
    call cd %RUNDIR%
	goto finish
)

echo Provide command...
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
    echo   cpclient  - copy swf (from target) to webapps 
    echo.                                                 
    echo   dbimysql  - initialise db for mysql           
    echo.                                                 
    echo   dbiderby  - initialise db for derby           
    echo   derbygo   - start derby server                
    echo   derbygob  - start derby server (in back mode) 
    echo   derbyend  - stop derby server                 
    echo   derbycon  - connect to derby with ij          
    echo.                                                 
    echo   pkgdemo   - prepare demo package              
    echo   clndemo   - clean demo package                
    echo ================================================
	goto %comeBack%
	
:add_mvn_extra_dep

    echo ================================================
    echo  Setup environment           
    echo ================================================
    echo Adding extra dependencies...

    call cd %YC_HOME%\env\setup\lib3rdparty

    set LIBFILE=%YC_HOME%\env\setup\lib3rdparty\authorize.net\anet-java-sdk-1.4.2.jar
    call %MVN% install:install-file -DgroupId=net.authorize -DartifactId=authorize-client -Dversion=1.4.2 -Dpackaging=jar -Dfile=%LIBFILE%

    set LIBFILE=%YC_HOME%\env\setup\lib3rdparty\cybersource\cybssecurity.jar
    call %MVN% install:install-file -DgroupId=com.cybersource -DartifactId=cybersource-security -Dversion=1.5 -Dpackaging=jar -Dfile=%LIBFILE%

    set LIBFILE=%YC_HOME%\env\setup\lib3rdparty\cybersource\cybsclients15.jar
    call %MVN% install:install-file -DgroupId=com.cybersource -DartifactId=cybersource-client -Dversion=1.5 -Dpackaging=jar -Dfile=%LIBFILE%

    set LIBFILE=%YC_HOME%\env\setup\lib3rdparty\payflow\payflow.jar
    call %MVN% install:install-file -DgroupId=paypal.payflow -DartifactId=payflow-client -Dversion=4.31 -Dpackaging=jar -Dfile=%LIBFILE%

    set LIBFILE=%YC_HOME%\env\setup\lib3rdparty\paypal\paypal_base.jar
    call %MVN% install:install-file -DgroupId=com.paypal -DartifactId=paypal-client -Dversion=5.1.1 -Dpackaging=jar -Dfile=%LIBFILE%

    set LIBFILE=%YC_HOME%\env\setup\lib3rdparty\google.checkout\checkout-sdk-2.5.1.jar
    call %MVN% install:install-file -DgroupId=com.google.checkout -DartifactId=checkout-sdk -Dversion=2.5.1 -Dpackaging=jar -Dfile=%LIBFILE%

    set LIBFILE=%YC_HOME%\env\setup\lib3rdparty\jai\mlibwrapper_jai.jar
    call %MVN% install:install-file -DgroupId=com.sun.media -DartifactId=mlibwrapper_jai -Dversion=1.1.3 -Dpackaging=jar -Dfile=%LIBFILE%

    call cd %YC_HOME%

    echo ================================================
	goto %comeBack%
	
	
:cp_client_to_webapp

    echo ================================================
    echo  Copy SWF client to webapps                     
    echo ================================================

    echo  copying... %YC_HOME%\manager\client\target\ShopManagerApplication.swf
    copy %YC_HOME%\manager\client\target\ShopManagerApplication.swf  %YC_HOME%\manager\server\src\main\webapp\ShopManagerApplication.swf
	goto %comeBack%

	
	
:init_db_mysql

    echo ================================================
    echo  Initialise MySQL database                      
    echo ================================================

    set DBINITSCRIPT=%YC_HOME%\env\setup\dbi\mysql\dbinit.sql
    echo Running init script as root user %DBINITSCRIPT%
    call mysql -uroot -p < %DBINITSCRIPT%

    echo Initialisation complete.
	goto %comeBack%

	
	
	
:db_derby_go

    echo ================================================
    echo  Starting Derby database                        
    echo ================================================

	echo %YC_HOME%
    set DERBY_INSTALL=%YC_HOME%\env\derby
    set CLASSPATH=%DERBY_INSTALL%\lib\derby.jar;%DERBY_INSTALL%\lib\derbytools.jar;%DERBY_INSTALL%\lib\derbyclient.jar;%DERBY_INSTALL%\lib\derbynet.jar;.
    call cd %DERBY_INSTALL%\lib

    call java -jar derbyrun.jar server start
	goto %comeBack%


	
:db_derby_gob

    echo ================================================
    echo  Starting Derby database (background mode)      
    echo ================================================

    set DERBY_INSTALL=%YC_HOME%\env\derby
    set CLASSPATH=%DERBY_INSTALL%\lib\derby.jar;%DERBY_INSTALL%\lib\derbytools.jar;%DERBY_INSTALL%\lib\derbyclient.jar;%DERBY_INSTALL%\lib\derbynet.jar;.
    call cd %DERBY_INSTALL%\lib

    start java -jar derbyrun.jar server start

    echo  Derby server started on port 1527...           
	goto %comeBack%


	
:db_derby_end

    echo ================================================
    echo  Stopping Derby database                        
    echo ================================================

    set DERBY_INSTALL=%YC_HOME%\env\derby
    set CLASSPATH=%DERBY_INSTALL%\lib\derby.jar;%DERBY_INSTALL%\lib\derbytools.jar;%DERBY_INSTALL%\lib\derbyclient.jar;%DERBY_INSTALL%\lib\derbynet.jar;.
    call cd %DERBY_INSTALL%\lib

    call java -jar derbyrun.jar server shutdown

    echo  Derby server stopped...                        
	goto %comeBack%


	
:init_db_derby

    echo ================================================
    echo  Initialise Derby database                      
    echo ================================================
    
    echo Setting Derby environment variables
    set DERBY_INSTALL=%YC_HOME%\env\derby
    set CLASSPATH=%DERBY_INSTALL%\lib\derby.jar;%DERBY_INSTALL%\lib\derbytools.jar;%DERBY_INSTALL%\lib\derbyclient.jar;%DERBY_INSTALL%\lib\derbynet.jar;.
    call cd %DERBY_INSTALL%\lib

rem   call java org.apache.derby.tools.sysinfo
    set DBINITSCRIPT=%YC_HOME%\env\setup\dbi\derby\dbinit.sql
    echo Running init script %DBINITSCRIPT%
    call java -Dderby.system.home=%YC_HOME% -Dij.outfile=%YC_HOME%\derbyinit.log org.apache.derby.tools.ij %DBINITSCRIPT%
rem   call java -Dderby.system.home=%YC_HOME% org.apache.derby.tools.ij %DBINITSCRIPT%
    
    call cd %YC_HOME%;
    echo Initialisation complete. See log: %YC_HOME%\derbyinit.log
	goto %comeBack%


	
:db_derby_connect

    echo ================================================
    echo  Starting Derby client                          
    echo ================================================

    set DERBY_INSTALL=%YC_HOME%\env\derby
    set CLASSPATH=%DERBY_INSTALL%\lib\derby.jar;%DERBY_INSTALL%\lib\derbytools.jar;%DERBY_INSTALL%\lib\derbyclient.jar;%DERBY_INSTALL%\lib\derbynet.jar;.
    call cd %DERBY_INSTALL%\lib

    echo Working directory is
    echo %CD%
    
    echo Use:
    echo   connect 'jdbc:derby://localhost:1527/yes'; - to connect to main db
    echo   connect 'jdbc:derby://localhost:1527/yespay'; - to connect to payment gateway db
    echo   disconnect; - to quit
    
    call java org.apache.derby.tools.ij
	goto %comeBack%


	
:prepare_demo_clean

    echo ================================================
    echo  Cleaning DEMO package                          
    echo ================================================
    echo.                                                 

    echo  Cleaning Tomcat Logs                           
    call del /Q %YC_HOME%\demo\yes-server\logs\*.log
    call del /Q %YC_HOME%\demo\yes-server\logs\catalina.out
    echo  done...                                        

    echo  Cleaning Tomcat Temp                           
    call del /Q %YC_HOME%\demo\yes-server\work\*
    call del /Q %YC_HOME%\demo\yes-server\temp\*
    echo  done...                                        

    echo  Cleaning Derby bundle %YC_HOME%\demo\yes-db\*   
    call rmdir /S /Q %YC_HOME%\demo\yes-db\yes
	call rmdir /S /Q %YC_HOME%\demo\yes-db\yespay
    echo  done...                                        

    set YESCONF=%YC_HOME%\demo\yes-server\conf\Catalina\localhost

    set YESCONFSHOP=%YESCONF%\yes-shop.xml
    set YESCONFMANAGER=%YESCONF%\yes-manager.xml

    echo  Removing old context.xml:                      
    echo  %YESCONFSHOP%                                   
    call del /Q %YESCONFSHOP%
    echo  %YESCONFMANAGER%                                
    call del /Q %YESCONFMANAGER%

    set YESWEBAPPS=%YC_HOME%\demo\yes-server\webapps

    set YESSHOP_OLD=%YESWEBAPPS%\yes-shop
    set YESSHOPWAR_OLD=%YESWEBAPPS%\yes-shop.war
    set YESMANAGER_OLD=%YESWEBAPPS%\yes-manager
    set YESMANAGERWAR_OLD=%YESWEBAPPS%\yes-manager.war

    echo  Removing old wars:                             
    echo  %YESSHOP_OLD%                                   
    call rmdir /S /Q %YESSHOP_OLD%
    echo  %YESSHOPWAR_OLD%                                
    call del /Q %YESSHOPWAR_OLD%
    echo  %YESMANAGER_OLD%                                
    call rmdir /S /Q %YESMANAGER_OLD%
    echo  %YESMANAGERWAR_OLD%                             
    call del /Q %YESMANAGERWAR_OLD%
    echo  done...                                        
	goto %comeBack%



	
:prepare_demo_pkg

    echo ================================================
    echo  Preparing DEMO package                         
    echo ================================================
    echo.                                                 
    echo  Make sure that you have prepared derby dbs and 
    echo  created a full maven build with derby profile. 
    echo.                                                 

    echo  Copying Derby package                          
    call xcopy %YC_HOME%\env\derby\lib\*.jar %YC_HOME%\demo\yes-db
    echo  done...                                        

    set YESDB_OLD=%YC_HOME%\demo\yes-db\yes
    set YESDB_NEW=%YC_HOME%\env\derby\lib\yes
    echo  Copying new db: %YESDB_NEW%                     
    call xcopy /E %YESDB_NEW% %YESDB_OLD%
    echo  done...                                        

    set YESPAYDB_OLD=%YC_HOME%\demo\yes-db\yespay
    set YESPAYDB_NEW=%YC_HOME%\env\derby\lib\yespay
    echo  Copying new db: %YESPAYDB_NEW%                  
    call xcopy /E %YESPAYDB_NEW% %YESPAYDB_OLD%
    echo  done...                                        

    set YESWEBAPPS=%YC_HOME%\demo\yes-server\webapps


    set YESSHOPWAR_NEW=%YC_HOME%\web\store\target\yes-shop.war
    set YESMANAGERWAR_NEW=%YC_HOME%\manager\server\target\yes-manager.war
    echo  Copying new wars:                              
    echo  %YESSHOPWAR_NEW%                                
    echo  %YESMANAGERWAR_NEW%                             
    call copy %YESSHOPWAR_NEW% %YESWEBAPPS%
    call copy %YESMANAGERWAR_NEW% %YESWEBAPPS%
    echo  done...                                        

    echo  Creating zip package...                        
    call del /Q %YC_HOME%\yescart.zip
    call zip -r --exclude=*.svn* %YC_HOME%\yescart.zip %YC_HOME%\demo
    echo  done...                                        
    echo.                                                 
    echo ================================================
    echo  Demo package created                           
    echo ================================================
	goto %comeBack%

:finish