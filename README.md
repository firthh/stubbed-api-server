# Stubbed API server

Run a stubbed out version of an API based on a swagger.json file.

Currently it's just a spike to see what's possible and learn more about swagger.

## Usage

Ideally it should be as simple as: 
```shell
stubbed-api --swagger-path swagger.json
```
Perhaps Docker will make that possible

## TODO

- [x] Match paths based on URI from swagger
- [x] Match paths based on request method from swagger
- [ ] Match paths based on query string parameters
- [ ] Match paths based on on body of the request
- [ ] Return a random response body and status using one from paths (in JSON)
- [ ] Validate request content-type matches an accepted content-type
- [ ] Retune a response body that matches the request body
- [ ] Provide a mechanism to specify which response type should always be used
- [ ] Provide a mechanism to specify which response type should be used based on request
- [ ] Provide your own response based on request
- [ ] Generate random data to fill out response body
- [ ] Support more formats than Swagger V2
