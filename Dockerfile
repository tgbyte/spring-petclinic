FROM maven:3.5.4-jdk-8 AS build
COPY . /app
RUN cd app; mvn clean package

FROM openjdk:8
COPY --from=build /app/target/spring-petclinic-*.jar /app.jar  

ENV JAVA_MAX_METASPACE_SIZE=512m \
    JAVA_OPTS= \
    JAVA_XMS=512m \
    JAVA_XMX=512m \
    ELASTIC_APM_AGENT_VERSION=0.7.0

COPY start.sh /start.sh
ADD http://repo1.maven.org/maven2/co/elastic/apm/elastic-apm-agent/${ELASTIC_APM_AGENT_VERSION}/elastic-apm-agent-${ELASTIC_APM_AGENT_VERSION}.jar /elastic-apm-agent.jar

EXPOSE 8080
VOLUME /tmp

CMD ["/start.sh"]
