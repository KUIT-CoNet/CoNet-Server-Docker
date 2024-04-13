FROM openjdk:17-jdk
EXPOSE 9000
ARG JAR_FILE=build/libs/*-SNAPSHOT.jar
COPY ${JAR_FILE} app.jar
ENTRYPOINT [ \
"java", \
"-jar", \
"-Duser.timezone=Asia/Seoul", \
"app.jar" \
]