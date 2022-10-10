FROM openjdk:8-jdk-alpine
EXPOSE 7744
ADD target/foydali.jar foydali.jar
ENTRYPOINT ["java","-jar","foydali.jar"]