FROM adoptopenjdk:11-jre-openj9

COPY build/libs/*-all.jar /tabletop/tabletop-server.jar

WORKDIR /tabletop

CMD ["java", "-jar", "tabletop-server.jar"]

EXPOSE 8080
