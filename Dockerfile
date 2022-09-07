FROM adoptopenjdk/openjdk11:alpine-jre
COPY /target/redisHamster-0.0.1-SNAPSHOT.jar redisHamster-0.0.1-SNAPSHOT.jar
ENTRYPOINT ["java","-jar","redisHamster-0.0.1-SNAPSHOT.jar"]