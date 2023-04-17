FROM openjdk:17-jdk-slim
COPY build/libs/aggregation-service-0.0.1-SNAPSHOT.jar aggregation-service-0.0.1-SNAPSHOT.jar
EXPOSE 50051
CMD ["java","-jar","aggregation-service-0.0.1-SNAPSHOT.jar"]
