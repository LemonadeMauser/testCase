FROM amazoncorretto:17
ADD /target/testProject-0.0.1-SNAPSHOT.jar test.jar
ENTRYPOINT ["java","-jar","test.jar"]