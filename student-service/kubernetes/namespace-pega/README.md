###############################################################################
# Minikube
# start minikube
minikube start --memory=10240 --cpus=4 --kubernetes-version=v1.14.2
minikube stop
# Open the minikube dashboard
minikube dashboard
# get the IP of the minikube cluster
minikube ip
# build images in minikube docker env directly. Normally docker images in loca terminal would show images from local system and not within the minikube cluster.
# But executing the following command the local docker env points to minikube docker env.
eval $(minikube docker-env)
docker build -t myproject/myimage .

# export from local docker into minikube docker
docker save mynewimage > /tmp/mynewimage.tar
docker load < /tmp/mynewimage.tar
###############################################################################

###############################################################################
# Kubernetes basics

# Create a new resource
kubectl create -f filename.yml
# Create or Update a resource
kubectl apply -f filename.yml
# Delete a resource
kubectl delete -f filename.yml

# Get list of all pods
kubectl get pods
# Get list of all pods with namespace filter with -n flag
kubectl get pods -n pega

# Get list of all deployments
kubectl get deployments
###############################################################################