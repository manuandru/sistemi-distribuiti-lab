## Example 2 -- Hit Counter

Consider the `hit-counter` directory
- this is Node project (yeah, JavaScript, apologies for that)
- it simply starts a web server listening for HTTP requests on the port specified by the `HIT_COUNTER_PORT` _environment_ variable
- the server responds to each request by providing a string of the form:
    ```
    [<server name>@<host name>:<port>] Hit <count> times
    ```
    where:
   
    + __`server name`__ is a string identifying the server
    + __`host name`__ is a string identifying the hostname of the machine the server is running upon
    + __`port`__ is a number identifying the port the server is listening on for HTTP requests
    + __`count`__ is the amount of requests the server has served so far

Locally you may try the server by running:
1. `npm install` to restore NPM dependencies (this is to be run just __once__)
2. `npm start` to start the service on `localhost:8080`

Once the service is running, you may query it by either:
- browsing to http://localhost:8080 in your browser
- run `curl http://localhost:8080`
- run the `poll-localhost.sh` script as follows:
    ```bash
    ../poll-localhost.sh 8080
    ```

### Part 1:

> __Goal__: Dockerify the Hit counter project

#### Steps

1. Create a `Dockerfile`
    1. start some image including `node` and `npm`
    3. copy all files from the `hit-counter` directory into the container's `$HOME/hit-counter` directory
    4. provide default values for environment variables, accordingly (e.g. `HIT_COUNTER_PORT`)
    5. set the container's `$HOME/hit-counter` directory as the current working directory
    6. restore the project dependencies from NPM
    7. specify NPM `start` task as the default command being launched upon container startup

2. Build the image & tag it as `<your DockerHub account>/hit-counter`

3. Start a new container out of the `<your DockerHub account>/hit-counter` image
    - __IMPORTANT__: recall to publish at least one port to let the service receive requests from outer clients

4. Push the image on Dockerhub


### Part 2 (after example 3):

> __Goal__: Understand analogies and differences among Docker-Compose and -Swarm

#### Steps

1. Consider the `docker-compose.yaml` file
    - replace the `<your hit counter image here>` string with `<your DockerHub account>/hit-counter`

2. Try to start the service via Docker-**Compose**
    ```bash
    docker-compose up 
    ```

    - with and without the commented code in `docker-compose.yaml`

    - recall to clean up your system once the experiment is over, via:
        ```bash
        docker-compose down
        ```

3. Try to start the service as a single-service stack via Docker-**Swarm**
    ```bash
    docker stack deploy hit-counter -c docker-compose.yaml
    ```

    - with and without the commented code in `docker-compose.yaml`

    - recall to clean up your system once the experiment is over, via:
        ```bash
        docker stack rm hit-counter
        ```

#### Questions

> 1. Why is replication failing on Compose, while succeeding on Swarm?

> 2. Which replica will serve our requests?