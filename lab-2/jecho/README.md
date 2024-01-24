## Example 1 -- JEcho

Consider the `jecho` directory
- this is Gradlefied version of JEcho (cf. lab 1)
- notice that this version of JEcho outputs some logs
- in particular, it greets the user, possibly by name in case a `JECHO_USER` _environment_ variable is set
- try for instance running `./gradlew --console=plain -q run` before and after setting the environment variable `JECHO_USER=<your name here>`

> __Goal__: Dockerify the JEcho project

### Steps:

1. Create a `Dockerfile`
    1. start from image `alpine:latest`
    2. install the JDK
    3. copy all files from the `jecho` directory into the container's `$HOME/jecho` directory
    4. provide default values for environment variables, accordingly (e.g. `JECHO_USER`)
    5. set the container's `$HOME/jecho` directory as the current working directory
    6. let `gradlew` self-install & download dependencies
    7. specify Gradle's `run` task as the default command being launched upon container startup

2. Build the image & tag it as `<you DockerHub account>/jecho`

3. Start a new container out of the `<you DockerHub account>/jecho` image

4. Push the image on Dockerhub
