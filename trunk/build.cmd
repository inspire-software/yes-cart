
call mvn clean 

cd manager\client
call ant depl

cd ..\..

rem call mvn -o -Dmaven.test.skip=true install
mvn -o install
