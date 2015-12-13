rmdir /s /q C:\dev\apache-tomcat-8.0.29\webapps\yes-manager

call mvn install -Pmysql -DskipTests=true

xcopy /s /e target\yes-manager C:\dev\apache-tomcat-8.0.29\webapps\yes-manager\
time /t