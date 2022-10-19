## TODO

### Example 1

1. Have a look to the `:server` and `:client` sub-projects

2. Notice that they both come with a `run` task
   - `gradle server:run -Pport=XXX` launches the `EchoServer` class, passing `XXX` as the first and only argument
     * `XXX` defaults to `10000` (cf. the file `gradle.properties`)
   
   - `gradle client:run -Phost=HHH -Pport=XXX` launches the `EchoClient` class, passing arguments `HHH` and `XXX`
       * `HHH` defaults to `localhost` (cf. the file `gradle.properties`)
       * `XXX` defaults to `10000` (cf. the file `gradle.properties`)

> __Goal__: listen to the teacher showing how to implement a _sequential_ echo service


### Exercise 1

- Go on editing the `:server` and `:client` sub-projects

> __Goal__: complete the implementation of a **concurrent** echo service

Notice that:

- Your implementation should satisfy all the tests from all modules (`:server`, `:client`, and `:test`)
    > Tests are based on printed lines and their ordering.
    > __So, pay attention to do exactly the same output prints as the teacher__
 
- Your client/server implementation should terminate gracefully whenever their standard input stream is closed
    - the standard input can be closed via `Ctrl+D` on Unix, `Ctrl+Z` followed by `Enter` on Windows (perhaps) 
    - this is a subtle requirement, 'cause it forces you to use several threads
