## Mok Server

Server developed in Ktor that mocks responses configured through a .yaml file.

You can match:
* The request path
* Headers
* Query parameters

You can replace values on the response payload with values on the headers or query string for enhanced flexibility

Using the header `MOK-DELAY-MS` or the param `mok-delay-ms` you can inject a delay per request in milliseconds, if you need control the response latency.

Current performance (the highest CPU was achieved during a 10000TPS test)

![Performance](PerformanceMok.png)