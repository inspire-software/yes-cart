
call mvn clean 

cd manager\client
call ant depl

cd ..\..

mvn -o -Dmaven.test.skip=true install
rem mvn -o install
rem mvn idea:idea -DdownloadSources=true
rem connect 'jdbc:derby://localhost:1527/yes;create=true';