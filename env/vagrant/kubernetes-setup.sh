#!/bin/sh
#
# 04-Jul-2017 Igor Azarny (iazarny@yahoo.com)

if which kubectl  >/dev/null; then
   	echo "skip kubernetes installation"
else
# ATM minikube has some issues https://github.com/kubernetes/minikube/issues/1565 
# So make sense continue with obsolete hyperkube

    echo "kubernetes installation"

    #apt-get update
    #apt-get install -y juju-local
    #apt-get install -y snapd


    #su - vagrant

    #juju init
    
    #juju switch local 

    #juju bootstrap   



    #apt-get install snapd

    #snap install conjure-up --classic

    #conjure-up kubernetes
    curl -sSL  http://storage.googleapis.com/kubernetes-release/release/v1.7.0/bin/linux/amd64/kubectl > /usr/bin/kubectl

    chmod +x /usr/bin/kubectl

    #curl -Lo minikube https://storage.googleapis.com/minikube/releases/v0.20.0/minikube-linux-amd64 && chmod +x minikube && sudo mv minikube /usr/local/bin/

    echo "fin ................................ "

fi    