## TODO

### Example 1 (in `:threads`)

1. Have a look to `sd.lab.concurrency.MultiReadingThreadExample`
   + class located in `threads/src/main/java`
   + tests located in `threads/src/test/java`
   + example runnable via Gradle's task `runMultiReadingThread`

2. Have a look to `sd.lab.concurrency.MultiReadingServiceExample`
   + class located in `threads/src/main/java`
   + tests located in `threads/src/test/java`
   + example runnable via Gradle's task `runMultiReadingService`

### Example 2 (in `:async`)

1. Have a look to examples in `sd.lab.concurrency.ExecutorServicesExamples`
   + class located in `async/src/test/java`

2. Have a look to examples in `sd.lab.concurrency.TestAsyncCounter1`
   + class located in `async/src/test/java`
   + requires understanding class `sd.lab.concurrency.AsyncCounter1` located in  `async/src/main/java`

3. Have a look to examples in `sd.lab.concurrency.FuturesExamples`

4. Have a look to examples in `sd.lab.concurrency.PromisesExamples`

5. Have a look to examples in `sd.lab.concurrency.TestAsyncCounter2`
   + class located in `async/src/test/java`
   + requires understanding class `sd.lab.concurrency.AsyncCounter2` located in  `async/src/main/java`

### Exercise 1 (in `:async`)

> __Goal:__ practice with asynchronous programming + learn how to split long-lasting computations in tasks

1. Have a look to the interface `sd.lab.concurrency.exercise.AsyncFactorialCalculator`
   + class located in `async/src/main/java`
   + tests located in `async/src/test/java`

2. Provide an implementation for such an interface
   + Ensure all tests in `sd.lab.concurrency.exercise.TestAsyncCalculator` are satisfied

### Exercise 2 (in `:async`)

> __Goal:__ practice with completable futures + learn how executor services are implemented

1. Have a look to the interface `sd.lab.concurrency.exercise.SingleThreadedExecutorService`
   + class located in `async/src/main/java`
   + tests located in `async/src/test/java`

2. Provide an implementation for such an interface
   + Ensure all tests in `sd.lab.concurrency.exercise.TestSingleThreadedExecutorService` are satisfied

### Exercise 3 (in `:threads`)

> __Goal:__ executor service & blocking operations + learn read from multiple sources via executor services

1. Have a look to the class `sd.lab.concurrency.exercise.MultiReadingExecutor`
   + class located in `threads/src/main/java`
   + tests located in `threads/src/test/java`
   + example runnable via Gradle's task `runMultiReadingExecutor`

2. Complete the implementation of such a class, using `ExecutorService`s
   + Ensure all tests in `sd.lab.concurrency.exercise.TestMultiReadingExecutor` are satisfied
