# Getting Started

## Design decisions

 For solving this assessment, a reactive non-blocking API has been implemented with Spring Web Flux to meet and fulfill the demands and the criteria mentioned
in the assessment document.

Utilising and leveraging reactor.core.publisher.Flux for solving the criterias mentioned in T2,T3

- window() <br/>
<br/>For splitting the source Flux into windows, criteria of size and subscribing from these publishers in a regulated manner.
Flux sequence is split into multiple Flux windows containing maxSize elements (or less for the final window) and starting
from the first item. Each Flux window will onComplete once it contains maxSize elements OR it has been open for the given Duration (as measured on the parallel Scheduler).

- buffer() <br/>
<br/>  Collect incoming values into multiple List buffers that will be emitted by the returned Flux every bufferingTimespan.
Discard Support: This operator discards the currently open buffer upon cancellation or error triggered by a data signal.


## Reference Documentation
For further reference, please consider the following sections:

[Web on Reactive Stack](https://docs.spring.io/spring-framework/docs/current/reference/html/web-reactive.html)

[Spring Cloud Contract](https://cloud.spring.io/spring-cloud-contract/reference/html/)

[Project reactor](https://projectreactor.io/docs/core/release/reference/)

[Windowing with Flux<Flux<T>>](https://projectreactor.io/docs/core/release/reference/#_windowing_with_fluxfluxt)

[Buffering with Flux<List<T>>](https://projectreactor.io/docs/core/release/reference/#_buffering_with_fluxlistt)

[TestContainers](https://www.testcontainers.org/)

[Mockito](https://site.mockito.org/)


### Run instructions

1. AggregationIntegrationTest are established with TestContainers. Please run tests for verifying expected behaviour.
```
src/test/java/com/fedex/aggregation/service/AggregationIntegrationTest.java 
```

2. Mock test throwing error from all provider APIs are established with Spring Cloud Contract. Please have a look
``` build/generated-test-source ``` for the actual test. <br/>
   <br/>
Mock setup class throwing exceptions are defined in 
``` src/test/java/com/fedex/aggregation/service/AggregationBase.java ```

3. Booting aggregation-service locally, please run docker-compose up from your shell in your project.root.dir. Once the provided image for the BE API (track,shipment,pricing) is up,
feel free to boot aggregation-service and call (either curl or PostMan API tool) the below uri that contains prepared queryParams with multiples of 5 concatenated.
```
http://localhost:8081/aggregation?pricing=NL,CN,SE,DK,ES&track=109347263,123456891,123456892,123456895,123456894&shipments=109347263,123456891,123456892,123456895,123456894
```

##### Docker cleanup commands in case of issues

1. Stop the container(s) using the following command:
   **docker stop $(docker ps -aq)**

2. Delete all containers using the following command:
   **docker rm -f $(docker ps -a -q)**

3. Delete all volumes using the following command:
   **docker volume rm $(docker volume ls -q)**
