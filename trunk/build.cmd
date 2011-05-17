
call mvn clean 

cd manager\client
call ant depl

cd ..\..

call mvn -o -Dmaven.test.skip=true install
