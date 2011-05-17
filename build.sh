#! /bin/bash                                                                                                                                                                                              
#. /etc/profile                                                                                                                                                                                           
#. $HOME/.bash_profile

mvn clean
if [ $? -eq 0 ];                                                                                                                                                                                      
  then echo "clean done..."                                                                                                                                                        
  else echo "clean failed..."; exit 1;                                                                                                                        
fi

cd ./manager/client
ant depl
if [ $? -eq 0 ];                                                                                                                                                                                      
  then echo "shop manager done..."                                                                                                                                                        
  else echo "shop manager failed..."; exit 1;                                                                                                                        
fi
cd ../../

rem mvn -o -Dmaven.test.skip=true install
mvn -o install
if [ $? -eq 0 ];                                                                                                                                                                                      
  then echo "install done..."                                                                                                                                                        
  else echo "install failed..."; exit 1;                                                                                                                        
fi
