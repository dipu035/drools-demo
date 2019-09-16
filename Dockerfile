FROM azul/zulu-openjdk-alpine:11.0.2-11.29

MAINTAINER 	Shamsuddin Tibriz (dipu035.mbstu@gmail.com)

VOLUME /tmp

COPY target/drools-demo.jar drools-demo.jar

# Make port 8080 available to the world outside this container
EXPOSE 8080

# Fix timezone (see https://serverfault.com/questions/683605/docker-container-time-timezone-will-not-reflect-changes)
ENV TZ=Europe/Amsterdam
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone

ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/drools-demo.jar"]