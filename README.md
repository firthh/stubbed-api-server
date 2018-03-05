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
- [ ] Return a random response body and status using one from paths
- [ ] Provide a mechanism to specify which response should be used
- [ ] Provide a mechanism to specify which response should be used based on request
- [ ] Generate random data to fill out response body
