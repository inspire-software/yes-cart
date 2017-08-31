#!/bin/bash
# 04-Jul-2017 Igor Azarny (iazarny@yahoo.com)

export K8S_VERSION=v1.5.3
export ARCH=amd64

TIMEOUT_START=60

while getopts "d" opt; do
  case $opt in
    d) dev=true ;;
  esac
done

cd $(dirname $0)

# check if environment is already running
kubectl get pods > /dev/null 2>&1; if [ $? = 0 ]; then exit 0; fi

# workaround for vagrant ubuntu environment
if [ "$dev" = true ]; then
    sudo mount --make-shared /
fi

sudo iptables -t filter --policy FORWARD ACCEPT

docker run -d \
           --volume=/:/rootfs:ro \
           --volume=/sys:/sys:ro \
           --volume=/var/lib/docker/:/var/lib/docker:rw \
           --volume=/var/lib/kubelet/:/var/lib/kubelet:rw,rslave \
           --volume=/var/run:/var/run:rw \
           --net=host \
           --pid=host \
           --privileged \
           gcr.io/google_containers/hyperkube-${ARCH}:${K8S_VERSION} \
           /hyperkube kubelet \
           --containerized \
           --hostname-override=127.0.0.1 \
           --api-servers=http://localhost:8080 \
           --config=/etc/kubernetes/manifests \
           --cluster-dns=10.0.0.10 \
           --cluster-domain=cluster.local \
           --maximum-dead-containers-per-container=2 \
           --maximum-dead-containers=30 \
           --image-gc-high-threshold=30 \
           --image-gc-low-threshold=20 \
           --allow-privileged --v=2

printf "Waiting for kube master and sky-dns"; i=0
while [ $i -lt $TIMEOUT_START ]; do
	kubectl get pods > /dev/null 2>&1
	if [ $? = 0 ] && [ $(kubectl get pods --namespace="kube-system" | grep kube-dns | grep -cv '\([1-9]\)/\1\s*Running') = 0 ]; then
		echo " done"
		kubectl get pods
        break
	fi
	printf "."; sleep 1; i=$((i+1))
done

exit 0
