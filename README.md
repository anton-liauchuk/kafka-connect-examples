# Kafka Connect examples
This repository contains a collection of example configurations, connectors, transformations, and utilities for Kafka Connect. 

## To run connectors
`kafka-connect-datagen` is a Kafka Connect connector for generating mock data https://github.com/confluentinc/kafka-connect-datagen.

## Custom Transformation
A custom transformation is available in the `kafka-connect-transformation` module. This transformation, called `MessageTransformation`, generates an `id` field and sets the `name` parameter equal to the `pageid` field from the original message. This transformation can be tested with the `pageviews` schema from the `kafka-connect-datagen` connector.

This transformation is utilized in the example connector provided in `connectors/datagen.json`. You can use `kafka-connect-ui` to create a connector with the provided configuration.

## Custom REST Extension
A custom REST extension is available in the `kafka-connect-rest` module. This implementation uses a random UUID generator as a source of identifiers for connectors. In `docker-compose.yaml` this extension is already enabled.

An example of using this endpoint:
```http request
### custom endpoint to load identifiers for connectors
GET http://127.0.0.1:8083/meta
Accept: application/json
```

This and other http requests are placed in `/requests/rest.http`.
