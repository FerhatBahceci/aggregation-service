# Getting Started

## Design decisions
For solving this assessment, a reactive non-blocking API has been implemented with Spring Web Flux to meet and fulfill the demands and the criteria mentioned
in the assessment document.

Utilising and leveraging```reactor.core.publisher.Flux``` for solving the criteria mentioned in T2,T3.
<br/>
<br/><br/>
<br/>
```
 Flux<Flux<T>> windowTimeout(int maxSize, Duration maxTime)
```

For splitting the source Flux into windows, criteria of size and subscribing from these publishers in a regulated manner.
Flux sequence is split into multiple Flux windows containing maxSize elements (or less for the final window) and starting
from the first item. Each Flux window will onComplete once it contains maxSize elements OR it has been open for the given Duration.<br/>
  <br/>

-> We are here gathering emissions until our window cap is reached and then executing this merged reactive sequence instead of doing calls one by one to the upstream publisher.
<br/>
<br/>
<br/>
<br/>

aggregation-service structure is constructed with SOLID principles in mind.
1. Single responsibility. Attempt to keep methods and classes minimal, concise and straightforward format. A class should have one, and only one, reason to change.
2. Open for extension, closed for modification. Feel free to implement new Response classes Response<K, V> extends OverloadingPreventionHandler and utilise the client logic for new clients.
3. Liskov principle is applied by utilising multiple inheritance of type and building generic utility-method instead of repeating code for constructing cold publishers.
4. Interface segregation is applied by separating interfaces one by one, for instance PricingGateway, ShipmentGateway, TrackGateway are all different and should be constructed in their respective interfaces and not together.
5. Dependency inversion is applied by not allowing high-level details depend on low-level details but on abstractions instead. This makes it a lot easier for testing as well as making the code more loosely coupled. 
Today, we have a client. Tomorrow we might have some other source for retrieving this data and should therefore not depend on the concrete implementation.


## Reference Documentation
For further reference, please consider the following sections:

[Web on Reactive Stack](https://docs.spring.io/spring-framework/docs/current/reference/html/web-reactive.html)

[Spring Cloud Contract](https://cloud.spring.io/spring-cloud-contract/reference/html/)

[Project reactor](https://projectreactor.io/docs/core/release/reference/)

[Windowing with Flux<Flux<T>>](https://projectreactor.io/docs/core/release/reference/#_windowing_with_fluxfluxt)

[Buffering with Flux<List<T>>](https://projectreactor.io/docs/core/release/reference/#_buffering_with_fluxlistt)

[TestContainers](https://www.testcontainers.org/)

[Mockito](https://site.mockito.org/)

[Clean Coder](https://blog.cleancoder.com/uncle-bob/2020/10/18/Solid-Relevance.html)


### Run instructions

1. AggregationIntegrationTest are established with TestContainers. Please run the tests for verifying expected behaviour:
```
src/test/java/com/fedex/aggregation/service/AggregationIntegrationTest.java 
```

2. Mock test throwing error from all provider APIs are established with Spring Cloud Contract. Please have a look
``` build/generated-test-source ``` for the actual test. <br/>

Springboot InjectMock setup class throwing exceptions are defined in:
``` src/test/java/com/fedex/aggregation/service/AggregationBase.java ```
<br/>


3. Booting aggregation-service locally, please run docker-compose up from your shell in your project.root.dir. Once the provided image for the BE API (track,shipment,pricing) is up,
feel free to boot aggregation-service and call (either curl or PostMan API tool) the below uri that contains prepared queryParams with multiples of 5 concatenated.
```
http://localhost:8081/aggregation?pricing=NL,CN,SE,DK,ES&track=109347263,123456891,123456892,123456895,123456894&shipments=109347263,123456891,123456892,123456895,123456894

http://localhost:8081/aggregation?pricing=AF,AL,DZ,AS,AD,AO,AI,AQ,AG,AR,AM,AW,AU,AT,AZ,BS,BH,BD,BB,BY,BZ,SE,BE,ES,NO,DK,IT,PLs&track=123456799,123456791,123456792,123456793,123456794,123456795,123456796,123456797,123456798,123456719,123456729,123456739,123456749,123456759,123456769,123456789,223456799,323456799,423456799,523456799,623456799,723456799,823456799,125456799,123656799,123466799&shipments=123456799,123456791,123456792,123456793,123456794,123456795,123456796,123456797,123456798,123456719,123456729,123456739,123456749,123456759,123456769,123456789,223456799,323456799,423456799,523456799,623456799,723456799,823456799,125456799,123656799,123466799
```

##### Docker cleanup commands in case of issues

1. Stop the container(s) using the following command:
   **docker stop $(docker ps -aq)**

2. Delete all containers using the following command:
   **docker rm -f $(docker ps -a -q)**

3. Delete all volumes using the following command:
   **docker volume rm $(docker volume ls -q)**
