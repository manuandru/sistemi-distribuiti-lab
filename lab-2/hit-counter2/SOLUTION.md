# Solution

1. Changed [docker-compose-yaml](./docker-compose.yaml) 
2. Not work on MacOS M1 (arm64)

        Changed this: ME_CONFIG_MONGODB_URL: mongodb://username:password@hostname:port/

        To this: ME_CONFIG_MONGODB_SERVER: 'mongo'   # bad: allow connections from everyone

3. Add enviroment variable to get the number of replica from inside the container and changed server script

        REPLICA: {{.Task.Slot}}

4. To run with docker-compose, comment the deploy section
5. Image that contain the script is built and pushed on dockerhub in order to create swarm without local registry
6.      docker stack deploy -c docker-compose.yaml my_hit-counter2
        docker service scale my_hit-counter2_server=10

        output:
        my_hit-counter2_server scaled to 10
        overall progress: 10 out of 10 tasks 
        1/10: running   [==================================================>] 
        2/10: running   [==================================================>] 
        3/10: running   [==================================================>] 
        4/10: running   [==================================================>] 
        5/10: running   [==================================================>] 
        6/10: running   [==================================================>] 
        7/10: running   [==================================================>] 
        8/10: running   [==================================================>] 
        9/10: running   [==================================================>] 
        10/10: running   [==================================================>] 
        verify: Service converged

7.      ../poll-localhost.sh 8080

        output:
        [5f2f020574044d3e@server:8080] Hit 1 times - From replica 4
        [b5140854e66b07bc@server:8080] Hit 2 times - From replica 3
        [26a43487ed0b6940@server:8080] Hit 3 times - From replica 6
        [ef44844ba51b6504@server:8080] Hit 4 times - From replica 7
        [91b6222265844755@server:8080] Hit 5 times - From replica 8
        [016bc7c12a7ffedb@server:8080] Hit 6 times - From replica 5
        [bb211e4b3f5f0539@server:8080] Hit 7 times - From replica 9
        [da400337538aad4f@server:8080] Hit 8 times - From replica 10
        [91d3575b45105941@server:8080] Hit 9 times - From replica 1
        [5186e710fe7b0dfe@server:8080] Hit 10 times - From replica 2
        [5f2f020574044d3e@server:8080] Hit 11 times - From replica 4
        [b5140854e66b07bc@server:8080] Hit 12 times - From replica 3
        [26a43487ed0b6940@server:8080] Hit 13 times - From replica 6
        [ef44844ba51b6504@server:8080] Hit 14 times - From replica 7
        [91b6222265844755@server:8080] Hit 15 times - From replica 8
        [016bc7c12a7ffedb@server:8080] Hit 16 times - From replica 5
        [bb211e4b3f5f0539@server:8080] Hit 17 times - From replica 9
        [da400337538aad4f@server:8080] Hit 18 times - From replica 10
        ...