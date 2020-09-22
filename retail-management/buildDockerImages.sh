./customer-management/gradlew :clean build --build-file ./customer-management/build.gradle
./product-management/gradlew :clean build --build-file ./product-management/build.gradle
./order-management/gradlew :clean build --build-file ./order-management/build.gradle
./retail-api/gradlew :clean build --build-file ./retail-api/build.gradle
./messaging-service/gradlew :clean build --build-file ./messaging-service/build.gradle

#minikube stop &&
minikube start --cpus 6 --memory 8192

eval $(minikube docker-env)
docker rmi $(docker images -f "dangling=true" -q)

docker build -t messaging-service:latest --build-arg JAR_FILE=build/libs/messaging-service-1.0.jar ./messaging-service
docker build -t customer-management:latest --build-arg JAR_FILE=build/libs/customer-management-1.0.jar ./customer-management
docker build -t product-management:latest --build-arg JAR_FILE=build/libs/product-management-1.0.jar ./product-management
docker build -t order-management:latest --build-arg JAR_FILE=build/libs/order-management-1.0.jar ./order-management
