#!/bin/sh
#
# 02-Aug-2017 Igor Azarny (iazarny@yahoo.com)

#if which aws >/dev/null; then
        echo "skip awscli installation"
#else
        echo "aws cli installation"


       add-apt-repository ppa:fkrull/deadsnakes-python2.7
       apt-get update 
       apt-get install python2.7


       pip install awscli


#fi