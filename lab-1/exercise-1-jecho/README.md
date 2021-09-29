## TODO

0. Inspect and understand the JEcho project (mostly consisting of the `it.unibo.jecho.Main` class)
    + it consists of a command-line program echoing the standard input, using one of three modalities:
        - __lower case__, activated by passing `l` or `lowercase` as argument to the program
        - __upper case__, activated by passing `u` or `uppercase` as argument to the program
        - __normal__, activated by passing _no argument_ to the program
    + when in lower (resp. upper) case mode, the standard input of the program is echoed by converting all lines in lower (resp. upper) case
    + when in normal mode, the standard input of the program is echoed _as is_
    + you can compile the program by running:
        ```bash
        javac -cp libs/commons-cli-1.4.jar src/**/*.java
        ```
    + you can run the compiled program by running
        ```bash
        java -cp src:libs/commons-cli-1.4.jar it.unibo.jecho.Main <MODE_HERE>
        ```
        (recall that to use `;` in place of `:` on Windows)

1. Gradlefy the JEcho project
    + consider initialising a new project via `gradle init` to setup the basic structure

2. Use Gradle to declare dependencies instead of tracking .jar files
    + notice that JEcho depends on [Apache Commons CLI](https://commons.apache.org) `v1.4`

3. Use Gradle's `run` task to launch `it.unibo.jecho.Main`

4. Use Gradle properties to set up the echo modality (`lowercase`, `uppercase`, `normal`) upon `run`
    + the property name should be `mode`, admissible values should be:
        - `l` or `lowercase`
        - `u` or `uppercase`
    + the lack of the property should be interpreted as the `normal` case

5. Define 2 new tasks, namely `runUpper` and `runLower`, starting JEcho in `lowercase` and `uppercase` mode directly, without requiring any property
