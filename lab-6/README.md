## TODO

### Overview

Consider the `auth-*` subprojects, providing a notion of _authenticator_, and its local and remote implementation
- `:auth-core` exposes a `Authenticator` interface and its local implementation
    * an `Authenticator` is a simple service supporting the _registration_ of `User`s, as well as their _authorization_
        + __registration__ accepts a `User` description as input, and can either succeed with no result or fail in case
          of _conflict_ or _badly_ formatted request
        + __authorization__ accepts some user's `Credentials` as input, and can either succeed returning a `Token` or
          fail in case of `wrong` credentials or _badly_ formatted request
- `:auth-client` and `:auth-ws` provide a remote implementation for the `Authenticator` interface, realising the client-
  and server-side, respectively
    * they essentially exploit the HTTP protocol to support such remote implementation
- `:auth-presentation` provides (de)serialization facilities to be used by both `:auth-client` and `:auth-ws`
- `:auth-test` provides unit tests for the above
- Their dependencies are provided below:

  ![`auth-*` dependencies](http://www.plantuml.com/plantuml/svg/PP11ZeOW38Ntd8AuXmCOO_wh46sG2L03rPc5Xn-mJCA4ZRRlVULHFfbCeYjuiM4uCK8aEStceaCL8M2SfCcIon4v4-JI6d8Dx8KUBwMsYDQ3td07RJF6kNuckIpmGv2YyUjQZwXgF-fGVvdRU1VdzXzdcZ5Uy3k_rygGRaYZlNbNkXVXSaG8CWEr489VGYwFKl-NxHksM-XeSDHW96SFKhm_8uo_mNnO9uYsisnpp_eN-86uNy7_0W00)

### Organization of the server code

Canonical structure of a web-server project:

```
it/unibo/ds/ws/                         Main package
├── AuthService.java                    Entry point of the server
├── JavalinGsonAdapter.java             Adapter aimed at letting Javalin exploit custom Gson serializers
├── ...                                 Abstract classes and other stuff
├── resource1/                          Code related to Resource 1 (e.g. "Tokens")
│   ├── Resource1Controller.java        Controller (a.k.a. server-side stub) of routes concerning resource 1 
│   ├── Resource1Api.java               Api (~= business logic) of functionalities handling resources of type 1
│   └── impl/                           Implementation of Controllers and APIs for resource 1 
├── resource2/                          Code related to Resource 2 (e.g. "Users")
│   ├── Resource2Controller.java        Controller (a.k.a. server-side stub) of routes concerning resource 2 
│   ├── Resource2Api.java               Api (~= business logic) of functionalities handling resources of type 2
│   └── impl/                           Implementation of Controllers and APIs for resource 2 
├── ...                                 Other packages of other resources, if any
└── utils                               Miscellanea:
    ├── Plugins.java                    Custom plugins
    └── Filters.java                    Custom filters
```

#### What is a __resource__?

> A _type_ of entity to be managed through a web server, via CRUD operations

#### What is a __route__?

> The interface of the server-side functionality supporting a given operation on a given resource.
> It includes:
> - the relative __path__ of the resource (w.r.t. the service's domain)
> - the __method__ of the operation (i.e. which CRUD operation should be performed on the resource)
> - which input arguments are expected in the operation __request__ (e.g. expected path/query parameters, headers, or
    bodies)
> - which output data should be admissible in the operation __response__ (e.g. status codes, headers, bodies)

#### What is a __controller__?

(This is a Javalin-specific jargon!)

> The server-side __stub__, i.e. where:
> 1. deserialization of clients' requests is performed
> 2. the business logic of each request is triggered (this is where interation with a DB may occur)
> 3. the result the business logic is serialized and returned to the requesting client
> 4. any error possibly occurred in the process is transformed into a response for the client

Example of controller:

```java
public interface Resource1Controller {
    // gets the parent path of all routes handled by this controller
    String path();

    // builds a sub-path relative to path()
    String path(String subPath);

    // registers the routes handled by this controller into the server
    void registerRoutes(Javalin server);

    // one or more of the following methods may be present (one per method/path)

    // GET /path
    void getAllResources1(Context context) throws HttpResponseException;

    // GET /path/{id}
    void getResource1(Context context) throws HttpResponseException;

    // POST /path
    void postResource1(Context context) throws HttpResponseException;

    // PUT /path/{id}
    void putResource1(Context context) throws HttpResponseException;

    // DELETE /path/{id}
    void deleteResource1(Context context) throws HttpResponseException;
}
```

#### What is an __API__?

(This is a Javalin-specific jargon!)

> The server side interface of a business-logic-level operation backing some route's functionality.

Example of API:

```java
public interface Resource1Api {

    CompletableFuture<Collection<? extends String>> getAllIds(int skip, int limit, String filter);

    CompletableFuture<String> addResource1(Resource1 user);

    CompletableFuture<Resource1> getResource1(String id);

    CompletableFuture<Void> removeResource1(String id);

    CompletableFuture<String> editResource1(String id, Resource1 changes);
}
```

Completable futures are exploited to support asynchronous computation of business-logic operations.

## Exercise 1 _(mandatory)_

1. The remote implementation attempts to highlight the client and server stubs of a WS solution aimed at performing
   basic user management operations
    - HTTP is used as the transport protocol behind the scenes
    - Service and clients may interoperate via a fixed API, described here: <http://localhost:10000/doc/ui>
        + recall to start the service via `./gradlew run` first

2. Look for the many placeholders in these project and fill them, in order to make the provided tests pass
    - Also, take care of understanding the reasons behind the overall structure of the project...
    - ... and how it is mirroring the aforementioned API
    - You may inspect the currently available routes (for the server) by browsing to: <http://localhost:10000/routes>

3. In particular, you may need to operate upon the following classes:
    - `it.unibo.ds.lab.ws.client.RemoteAuthenticator`
    - `it.unibo.ds.lab.ws.tokens.impl.TokenControllerImpl`
    - `it.unibo.ds.lab.ws.tokens.impl.TokenApiImpl`
    - `it.unibo.ds.lab.ws.users.impl.UserControllerImpl`
    - `it.unibo.ds.lab.ws.users.impl.UserApiImpl`
    - `it.unibo.ds.lab.ws.JavalinGsonAdapter`

> __Goal__: realise a simple WS and the client to use it, while understanding the benefits of a standardadised API
> + ensure the tests in `TestRemoteAuthenticator` succeed
