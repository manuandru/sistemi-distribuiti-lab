# Agents & Communication: TuCSoN vs JADE

### General remarks

* Always remember to run
   ```bash
   ./gradlew --stop
   ```
   after each demo is completed, in order to ensure all TuCSoN nodes are actually shut down.
* To start a local TuCSoN node:
   ```bash
   ./gradlew tucson -Pport=<node port>
   ```
   before each demo in order to switch on a working TuCSoN node on your machine.
* To start a TuCSoN CLI:
   ```bash
   ./gradlew cli -Pport=<node port>
   ```
* To start a TuCSoN inspector:
   ```bash
   ./gradlew inspector
   ```
* To start a JADE platform:
   ```bash
   ./gradlew jadePlatform
   ```
* To start a JADE container:
   ```bash
   ./gradlew jadeContainer
   ```

### NOTICE THAT

> Closing the JADE platform GUI **does not** shut down the platform

## Lab 2 / Exercise 1 – `HelloAgent`

0. Switch on local TuCSoN node
0. Start Inspector
0. Start HelloAgent with
   ```bash
   ./gradlew runHelloAgent
   ```
0. Switch off Inspector & TuCSoN

## Lab 2 / Exercise 2 – Agent Communication & Synchronisation in TuCSoN

0. Switch on local TuCSoN node
0. Start Inspector
0. Start `SteAgent` with
   ```bash
   ./gradlew runSte
   ```
0. Start `GioAgent` with
    ```bash
    ./gradlew runGio
    ```
0. Switch off Inspector & TuCSoN

## Lab 2 / Exercise 3 – Agent Communication in JADE

0. Start a local JADE platform
   ```bash
   ./gradlew jadePlatform
   ```
0. Start a JADE container for `GioAgent`:
   ```bash
   ./gradlew jadeContainerForGio
   ```
0. Start a JADE container for `SteAgent`:
   ```bash
   ./gradlew jadeContainerForSte
   ```
0. Stop all containers and the platform
0. Restart this exercise running `SteAgent` **before** `GioAgent`
