## TODO

### Overview

Consider the `auth-*` subprojects, providing a notion of _authenticator_, and its local and remote implementation
- `:auth-core` exposes a `Authenticator` interface and its local implementation
    * an `Authenticator` is a simple service supporting the _registration_ of `User`s, as well as their _authorization_
        + __registration__ accepts a `User` description as input, and can either succeed with no result or fail in case of _conflict_ or _badly_ formatted request
        + __authorization__ accepts some user's `Credentials` as input, and can either succeed returning a `Token` or fail in case of `wrong` credentials or _badly_ formatted request
- `:auth-client` and `:auth-ws` provide a remote implementation for the `Authenticator` interface, realising the client- and server-side, respectively
    * they essentially exploit the HTTP protocol to support such remote implementation
- `:auth-presentation` provides (de)serialization facilities to be used by both `:auth-client` and `:auth-ws`
- `:auth-test` provides unit tests for the above
- Their dependencies are provided below:

  ![`auth-*` dependencies](http://www.plantuml.com/plantuml/svg/PP11ZeOW38Ntd8AuXmCOO_wh46sG2L03rPc5Xn-mJCA4ZRRlVULHFfbCeYjuiM4uCK8aEStceaCL8M2SfCcIon4v4-JI6d8Dx8KUBwMsYDQ3td07RJF6kNuckIpmGv2YyUjQZwXgF-fGVvdRU1VdzXzdcZ5Uy3k_rygGRaYZlNbNkXVXSaG8CWEr489VGYwFKl-NxHksM-XeSDHW96SFKhm_8uo_mNnO9uYsisnpp_eN-86uNy7_0W00)


### Exercise 1 _(mandatory)_

1. The remote implementation attempts to highlight the client and server stubs of a WS solution aimed at performing basic user management operations
    - HTTP is used as the transport protocol behind the scenes
    - Service and clients may interoperate via a fixed API, described here: <http://localhost:10000/doc/ui>
        + recall to start the service via `./gradlew run` first

2. Look for the many placeholders in these project and fill them, in order to make the provided tests pass
    - Also, take care of understanding the reasons behind the overall structure of the project...
    - ... and how it is mirroring the aforementioned API
    - You may inspect the currently available routes (for the server) by browsing to: <http://localhost:10000/routes>

> __Goal__: realise a simple WS and the client to use it, while understanding the benefits of a standardadised API
> + ensure the tests in `TestRemoteAuthenticator` pass
