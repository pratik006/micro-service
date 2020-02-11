docker build --build-arg JAR_FILE=build/libs/*.jar -t pratik006/db-service:latest .

eval $(minikube docker-env)
docker build --build-arg JAR_FILE=build/libs/*.jar -t pratik006/db-service:latest .