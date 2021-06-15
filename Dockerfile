FROM adoptopenjdk/openjdk11:alpine-jre

EXPOSE 8080

ADD target/cloud_drive-0.0.1-SNAPSHOT.jar cloud_drive_docker.jar

ENTRYPOINT ["java", "-jar", "/cloud_drive_docker.jar" ]