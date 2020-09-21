kubectl create namespace order
eval $(minikube docker-env)
//docker rmi $(docker images -f "dangling=true" -q)
docker build -t order-management:latest --build-arg JAR_FILE=build/libs/order-management-1.0.jar ./order-management
helm install test-release -n order ./helm/order-management/