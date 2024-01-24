## Useful links

- https://www.rabbitmq.com/api-guide.html#jdk-versions
- https://www.rabbitmq.com/tutorials/tutorial-two-java.html
- https://www.cloudamqp.com/blog/part1-rabbitmq-for-beginners-what-is-rabbitmq.html
- https://rabbitmq.github.io/rabbitmq-stream-java-client/stable/htmlsingle/#what-is-a-rabbitmq-stream
- https://search.maven.org/artifact/com.rabbitmq/amqp-client/5.14.0/jar

## Example 1 (`base` package) -- Workflow 

0. Customize your credentials in the `docker-compose.yaml` file
    - use the same credentials into the `Agent.java` file

1. Start the broker

2. Open and inspect the dashboard

3. Start the listener via `./gradlew runListener`

4. Use the dashboard to inspect the newly created queue

5. Start the sender via `./gradlew runSender -Pmessage=ciao`

6. Send messages to the listener via the dashboard, to demonstrate the functioning of topics and routing keys

7. Repeat the experiment playing with ACKs

## Exercise 1 (`jobs` package) -- Workflow 

1. Design the master-worker protocol:
    - which and how many exchanges?
    - which and how many queues?
    - who creates what? why?
    - which is the minimal unit of work?
    - when are partial results aggregated?

2. Implement the protocol