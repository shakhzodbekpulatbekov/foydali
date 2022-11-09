FROM openjdk:8-jdk-alpine
EXPOSE 7744
ADD target/lorettouz.jar lorettouz.jar
ENTRYPOINT ["java","-jar","lorettouz.jar"]