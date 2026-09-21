ARG CONFLUENT_VERSION=8.3.2

FROM amazoncorretto:25 as build
COPY . /project
WORKDIR /project
RUN yum install -y findutils && yum clean all
RUN ./gradlew clean build -x integrationTest shadowJar

FROM confluentinc/cp-kafka-connect-base:${CONFLUENT_VERSION}

RUN confluent-hub install --no-prompt confluentinc/kafka-connect-jdbc:10.9.6
RUN confluent-hub install --no-prompt confluentinc/kafka-connect-datagen:latest

RUN mkdir /usr/share/java/rest
COPY --from=build /project/kafka-connect-rest/build/libs/kafka-connect-rest-*.jar /usr/share/java/rest

RUN mkdir /usr/share/confluent-hub-components/transformation
COPY --from=build /project/kafka-connect-transformation/build/libs/kafka-connect-transformation-*.jar /usr/share/confluent-hub-components/transformation
