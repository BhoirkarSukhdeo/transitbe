FROM openjdk:12
ARG jar_path=build/libs/*.jar
ADD ${jar_path} tarnsit-app.jar
EXPOSE 8080
CMD java -jar tarnsit-app.jar