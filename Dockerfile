ARG CONFLUENT_VERSION=7.5.2

FROM amazoncorretto:11 as build
COPY . /project
WORKDIR /project
RUN ./gradlew clean build

ARG JDBC_VERSION=10.7.4
FROM confluentinc/cp-kafka-connect-base:${CONFLUENT_VERSION}

RUN confluent-hub install --no-prompt confluentinc/kafka-connect-jdbc:10.7.4

RUN mkdir /usr/share/java/rest
COPY --from=build /project/kafka-connect-rest/build/libs/kafka-connect-rest-*.jar /usr/share/java/rest
