eval $(minikube docker-env)
docker rmi $(docker images -f "dangling=true" -q)

docker build -t customer-management:latest --build-arg JAR_FILE=build/libs/customer-management-1.0.jar -t customer-management:latest ./customer-management
docker build -t product-management:latest --build-arg JAR_FILE=build/libs/product-management-1.0.jar -t product-management:latest ./product-management
docker build -t order-management:latest --build-arg JAR_FILE=build/libs/order-management-1.0.jar -t order-management:latest ./order-management