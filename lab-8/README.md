## Useful links

### gRPC on Java

- <https://grpc.io/docs/what-is-grpc/introduction/>
- <https://grpc.io/docs/languages/java/>
- <https://grpc.github.io/grpc-java/javadoc/>

### Protobuf 

- <https://developers.google.com/protocol-buffers/docs/reference/java-generated>
- <https://developers.google.com/protocol-buffers/docs/reference/java>

- <https://learnxinyminutes.com/docs/protocol-buffer-3/>


## Workflow

1. Listen to the teacher presenting basic notions related to gRPC and Protobuf

2. Listen to the teacher exemplifying gRPC and Protobuf

3. Fill the holes in the provided examples' code

4. (Optional) perform the suggested exercises

## Conceptual overview

General notion:
- __Interface Definition Language__ (IDL): a generic, language- and platform-independent of representing a service interface, along with the data-types it accepts as inputs or produces as outputs
    
An IDL commonly involves:
1. a __data representaion format__, supporting the representaion of data in a platform-agnostic way
2. a __presentation technology__, possibly implemented on several platforms and languages, supporting the (de)serialization of data into/from bytes or characters
3. a __middleware technology__, possibly implemented on several platforms and languages, supporting the construction of polyglot clients and servers interacting over the network
4. one or more language-specific __code generators__, supporting the genration of data types and client- or server-side stubs for the types or services defined via the IDL

> Through these facilities, the engineering of distributed systems raises its level of abstraction:
> - designers must now only focus on services and their data types
> - developers can exploit code generation to automate the most tedious parts of coding

### gRPC, Proto, and Protobuf

Using gPRC usually involves:

- adopting [`proto`](https://developers.google.com/protocol-buffers/docs/proto3) as an __IDL__

- adopting [Protocol Buffers](https://developers.google.com/protocol-buffers) (Protobuf, henceforth) as the __presentation technology__
  * which supports several languages and platforms, namely: C++, Dart, Go, Java, Kotlin, Python, Ruby, C#, Objective C, JavaScript, and PHP
    
- adopting either Protobuf's data representation format or JSON as the __data representation format__

- adopting [gRPC](https://grpc.io/docs/what-is-grpc/introduction/) as the __middleware technology__

- adopting [`protoc`](https://github.com/protocolbuffers/protobuf/releases/latest) as the __code-generator__

![gRPC concept](https://grpc.io/img/landing-2.svg)

### What kind of services

Notably, gRPC-based services support _not only_ "ordinary" request-response methods, but also:
- stream-accepting methods
- stream-returning methods
- stream-accepting methods and stream-returning methods 

In a __stream-accepting__ method, several requests can be produced by the client, and a response is commonly provided by the server after the client's requests stream is over.

In a __stream-returning__ method, several responses can be provided by the server, commonly after the client has produced a single request

In a __stream-accepting__ and __stream-returning__ method, several responses can be provided by the server, and commonly it is consuming the client's requests stream.

> These mechanisms enable many sorts of interactions among (possibly distributed) clients and servers

### What kind of middleware

gRPC provides a number of plugins letting clients and services interact:
- over the network, via a binary protocol (default)
- over the network, via the HTTP/2 protocol (cf. <https://grpc.github.io/grpc-java/javadoc/index.html?io/grpc/netty/package-summary.html>)
- within the same process (cf. <https://grpc.github.io/grpc-java/javadoc/index.html?io/grpc/inprocess/package-summary.html>)
- possibly others

> These mechanisms enable pluggability of the transport protocol

### Other nice features

- Support for securing communications via criptography
- Support for authentication
- Support for customising threads management via custom `Executor`s 

## Exercises

### Exercise 1

> **Goal:** model the `Authenticator` service from previous labs via `proto` and implement it via gRPC+Protobuf.
> To do so, just fill the holes in the many `auth-*` modules included in this repo.

![Modules structure](http://www.plantuml.com/plantuml/svg/TP1DQiGm38NtEON8FZt048Q1Ng3q1gwh90R_65bkk-ZXwx48k8X05dfw7_5xUXGsn3NuoQQGKyJ8CDzCvMDfYv3Y3AQ0Z1h9XAL9mqv061SZlh-_lSwmhlZ3ID7u-_r9xvJjI6LAd3xgfidJKRIzOAqEMv2-a7fC6JLYlMkg8p9gXB48y1ocIIu3w--o_2y_2B7FHxKXJoc3t7xmSjQvTEKm5xwSE2P6yBWnf0r6y0Pyk0Bj-pwGi2c4b_vBs9p7PamIJeBsob9ZM3mmVjRW_m00)

### Exercise 2

> **Goal:** Use the provided `.proto` file to generate a client (or server) stub for the `Authenticator` service in some language of choice (different than Java), and let the new client (or server) interact with the Java-based server (or client)

