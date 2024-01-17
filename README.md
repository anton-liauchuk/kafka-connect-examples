# Custom REST Extension
A custom REST extension is available in the `kafka-connect-rest` module. This implementation uses a random UUID generator as a source of identifiers for connectors. In `docker-compose.yaml` this extension is already enabled.

An example of using this endpoint:
```http request
### custom endpoint to load identifiers for connectors
GET http://127.0.0.1:8083/meta
Accept: application/json
```

This and other http requests are placed in `rest.http`.
