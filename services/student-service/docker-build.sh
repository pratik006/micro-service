docker build --build-arg JAR_FILE=build/libs/*.jar -t pratik006/student-service:latest .

eval $(minikube docker-env)
docker build --build-arg JAR_FILE=build/libs/*.jar -t pratik006/student-service:latest .