FROM openjdk:21-jdk-slim
ADD /build/libs/*.jar siruchat.jar
ENTRYPOINT ["java", "-jar", "/siruchat.jar"]