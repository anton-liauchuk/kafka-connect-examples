ARG CONFLUENT_VERSION=7.5.2
ARG JDBC_VERSION=10.7.4
FROM confluentinc/cp-kafka-connect-base:${CONFLUENT_VERSION}

RUN confluent-hub install --no-prompt confluentinc/kafka-connect-jdbc:${JDBC_VERSION}
