## Example 3 -- My Blog

Consider the `my-blog` directory
- it contains a reproducible recipe to set up a Wordpress blog via Docker
- the recipe is _declaratively_ described by the `docker-compose.yaml` file
- it relies on 2 images, to be instantiated in an orderly fashion, namely:
    + [`mysql`](https://hub.docker.com/_/mysql) hosting the well-known RDBMS
    + [`wordpress`](https://hub.docker.com/_/mysql) hosting the well-known CMS

> __Goal__: Exemplify Docker-**Compose** and Docker-**Swarm**

### Steps (Compose):

1. Start the `my-blog` service via Docker-**Compose**
    ```bash
    docker-compose up
    ```

2. Browse to http://locahost:8000 with your browser

3. Set up a blog with arbitrary data

4. Stop the service with `Ctrl+C`, then browse to http://locahost:8000 again
    + this should fail

5. Restart the service with `docker-compose up`, then browse to http://locahost:8000 again
    + is the configuration of your blog still there? It should

6. Tear the service down with
    ```bash
    docker-compose down
    ```

    - what should happen if the service is restarted now? Should the blog configuration be still there or not?

### Steps (Swarm):

1. Ensure your Docker engine is in __Swarm mode__ by running
    ```bash
    docker swarm init
    ```

2. Repeat the whole workflow by running:
    ```bash
    docker stack deploy my-blog -c docker-compose.yaml
    ```
    instead of `docker-compose up`, and
    ```bash
    docker stack rm my-blog
    ```
    instead of `docker-compose down`
