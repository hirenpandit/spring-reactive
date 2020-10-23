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
