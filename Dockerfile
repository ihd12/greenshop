FROM openjdk:17-oracle
CMD ["./gradlew", "clean", "build"]
ARG JAR_FILE_PATH=target/*.jar
COPY ${JAR_FILE_PATH} springshop.jar
ENTRYPOINT ["java","-jar","springshop.jar"]
