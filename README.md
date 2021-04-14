# spring-reactive
### Overview:

Microservices and big data increasingly confront us with the limitations of traditional input/output. In traditional IO, work that is IO-bound dominates threads. This wouldn't be such a big deal if we could add more threads cheaply, but threads are expensive on the JVM, and most other platforms. Even if threads were cheap and infinitely scalable, we'd still be confronted with the faulty nature of networks. Things break, and they often do so in subtle, but non-exceptional ways. Traditional approaches to integration bury the faulty nature of networks behind overly simplifying abstractions. We need something better.

Spring Framework 5 is here! It introduces the Spring developer to a growing world of support for reactive programming across the Spring portfolio, starting with a new Netty-based web runtime, component model and module called Spring WebFlux, and then continuing to Spring Data Kay, Spring Security 5.0, Spring Boot 2.0 and Spring Cloud Finchley.

### Main component of reactive programming:

Documentation: [https://www.reactive-streams.org/reactive-streams-1.0.3-javadoc/org/reactivestreams/package-summary.html](https://www.reactive-streams.org/reactive-streams-1.0.3-javadoc/org/reactivestreams/package-summary.html)

- *Publisher*

> A `Publisher<T>` is provider of potentially unbounded number of sequenced elements, publishing them according to the demand received from it's `Subscriber`(s).

- *Subscriber*

> Will receive call to `Subscriber.onSubscribe(Subscription)` once after passing an instance of `Subscriber` to `Publisher.subscribe(Subscriber)`.

- *Subscription*

> A `Subscription` represent of one to one lifecycle of a `Subscriber`  subscribing to a `Publisher`

- *Processor*:

> `Processor<T, R>` is represents a processing stage - which is both `Subscriber` and `Publisher` and obeys the contract of both.

Above interfaces are part of `java.util.concurrent` package since JDK9.  These interfaces are bundled into `Flow` class.

[https://docs.oracle.com/javase/9/docs/api/java/util/concurrent/Flow.html](https://docs.oracle.com/javase/9/docs/api/java/util/concurrent/Flow.html)

Link to the Flow class documentation.

### Publishers:

- Mono

> A `Mono` is a specialized `Publisher` that emits at most one item and then optionally terminates with `onComplete` signal or an `onError` signal. In short, it returns 0 or 1 element.

example:

```java
Mono<String> noData = Mono.empty();
Mono<String> data = Mono.just("foo");
```

![https://s3-us-west-2.amazonaws.com/secure.notion-static.com/3c136877-eaeb-405b-a1bb-7adce8bfb245/Untitled.png](https://s3-us-west-2.amazonaws.com/secure.notion-static.com/3c136877-eaeb-405b-a1bb-7adce8bfb245/Untitled.png)

- Flux

> A `Flux` is a standard `Publisher` representing an asynchronous sequence of 0 to N emitted items, optionally terminate either by completion signal or error signal. Three types of signals translate to calls to a downstream `Subscriber`'s `onNext`, `onComplete` or `onError` methods.

example:

```java
Flux<String> flux1 = Flux.just("foo", "bar", "foobar");
Flux<String> flux2 = Flux.fromIterable(Arrays.asList("foo", "bar", "foobar"));
Flux<Integer> flux3 = Flux.range(5,3);
//subscribe
flux.subscribe();
```

![https://s3-us-west-2.amazonaws.com/secure.notion-static.com/8078fefc-573e-4ee7-b32d-5d13bfb99bf5/Untitled.png](https://s3-us-west-2.amazonaws.com/secure.notion-static.com/8078fefc-573e-4ee7-b32d-5d13bfb99bf5/Untitled.png)


### Demo Application

*Webflux with reactive mongoDB demo:*

To demonstrate spring reactive implementation we have created two Springboot based applications as you can see in repository. First application named reactive-spring, will demonstrate spring framework's reactive nature with web-flux. We are using embedded MongDB  as document based database and for reactive, non-blocking nature we are using mongodb-reactive. 

```xml
    <dependency>
	<groupId>org.springframework.boot</groupId>
	<artifactId>spring-boot-starter-data-mongodb-reactive</artifactId>
    </dependency>
    <dependency>
	<groupId>de.flapdoodle.embed</groupId>
	<artifactId>de.flapdoodle.embed.mongo</artifactId>
    </dependency>
```

we are using web-flux to handle the request response. 

```xml
    <dependency>
	<groupId>org.springframework.boot</groupId>
	<artifactId>spring-boot-starter-webflux</artifactId>
    </dependency>
```

To make db operations non-blocking we are using `ReactiveCrudRepository<T, U>`. This will perform all database operation like `save`, `delete`, `findOne`, `findAll` etc. just like `JPARepository` but in non-blocking (reactive) fashion. 

*Webflux:*

Now, to demonstrate the reactive implementation of spring we are using `webflux` and created a controller. The controller has an endpoint which when called get the values from MongoDB (which we are adding at the time of application startup) and publish it in response. Please check the `ReservationController.java`.

To emit stream of infinite values we have created endpoint `/see/{name}` in `[ReservationController.java](http://reservationcontroller.java)` this endpoint will created message with timestamp and publish it in interval of 1 second.

*How to run:*

Running the application is similar to running any springboot application. Go to reactive-spring folder. Run command `mvn clean package` and then run the .jar file created in target folder with `java -jar` command. As the application start we can see in the logs that it is running on netty server (non blocking io) instead of normal tomcat server.

```
2020-10-30 14:20:54.166  INFO 36395 --- [  restartedMain] o.s.b.web.embedded.netty.NettyWebServer  : Netty started on port(s): 8080
2020-10-30 14:20:54.189  INFO 36395 --- [  restartedMain] o.s.b.rsocket.netty.NettyRSocketServer   : Netty RSocket started on port(s): 8000
```

*RSocket demo:*

To demonstrate use of RSocket we are going to use spring-reactive application as server and spring-rsocket-client as client application. Bothe the application are springboot applications. 

1. To make spring-reactive application as server we will add rsocket dependency and define the port on which rsocket will listen.

```xml
    <dependency>
	<groupId>org.springframework.boot</groupId>
	<artifactId>spring-boot-starter-rsocket</artifactId>
    </dependency>
```

In applicatio.yml

```yaml
**spring:
  rsocket:
    server:
      port: 8000
```

2. Now we are going to create a route endpoint at which request will be received and processed. After the request is processed we will publish the response. Please check `RSocketGreetingsController.java`

```java
    @MessageMapping(value = "greetings")
    Flux<GreetingResponse> greet(GreetingRequest request) {
        return messageProducer.produce(request);
    }
```

3. Once the server is ready, we are going to created below `Beans` in client application (i.e spring-rsocket-client). This will establish connection to our server application via rsocket.

```java
@Bean
	RSocket rSocket(){
		return RSocketFactory
				.connect()
				.dataMimeType(MimeTypeUtils.APPLICATION_JSON_VALUE)
				.frameDecoder(PayloadDecoder.ZERO_COPY)
				.transport(TcpClientTransport.create(8000))
				.start().block();
	}

	@Bean
	RSocketRequester requester(RSocketStrategies strategies) {
		return RSocketRequester.builder()
				.rsocketFactory(clientRSocketFactory ->
						clientRSocketFactory
								.dataMimeType(MimeTypeUtils.ALL_VALUE)
								.frameDecoder(PayloadDecoder.ZERO_COPY))
				.rsocketStrategies(strategies)
				.connect(TcpClientTransport.create(8000))
				.retry().block();
	}
```

4. Now we are going to create simple @RestController in client application which will make call to our server application over rsocket and publish the response. To make request with rsocket RsocketRequest is used which is similar to RestTemplate in RESTful application.

```java
private final RSocketRequester requester;

    @GetMapping(value = "/greetings/{name}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    Publisher<GreetingResponse> greetingResponsePublisher(@PathVariable String name) {
        return requester.route("greetings")
                .data(new GreetingRequest(name))
                .retrieveFlux(GreetingResponse.class);
    }
```
