package it.unibo.queues.jobs;

import com.rabbitmq.client.*;
import it.unibo.queues.Agent;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;

public class Master extends Agent {

    public Master(String name) {
        super(name);
    }

    private final BufferedReader stdin = new BufferedReader(new InputStreamReader(System.in));

    @Override
    public void run(String myName, Connection connection) throws Exception {
        var channel = connection.createChannel();
        var todoQueue = myName + ".todolist";
        var resultsQueue = myName + ".results";

        // TODO declare queues
        declareQueueForSend(todoQueue, channel);

        var declaration = declareQueueForReceive(channel);
        channel.queueBind(declaration, resultsQueue, "");

        // TODO accept text from stdin
        var lines = new ArrayList<String>();
        String line = null;
        while ( (line = stdin.readLine()) != null) {
            lines.add(line);
        }

        // TODO publish jobs for workers
        for (var job : lines) {
            channel.basicPublish(todoQueue, "", null, new Job(job).toBytes());
        }

        // TODO collect partial results from workers
        // TODO aggregate partial results
        var resultConsumer = new ResultConsumer(channel, lines.size());
        channel.basicConsume(declaration, resultConsumer);

        // TODO await for the final result to be available and then print it
        var result = resultConsumer.getFinalResult().get();
        System.out.println(result);

        channel.close();
        connection.close();
    }

    private class ResultConsumer extends DefaultConsumer {

        private final CompletableFuture<Integer> finalResult = new CompletableFuture<>();
        private int jobsCount;
        private int partialResult = 0;

        public ResultConsumer(Channel channel, int jobsCount) {
            super(channel);
            this.jobsCount = jobsCount;
        }

        public CompletableFuture<Integer> getFinalResult() {
            return finalResult;
        }

        @Override
        public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
            // TODO accumulate partial results
            // TODO once all partial results have been received, aggregate them and complete the future
            Result result = Result.fromBytes(body);
            this.jobsCount--;
            partialResult += result.getValue();
            if (jobsCount == 0) {
                finalResult.complete(partialResult);
            }
        }
    }

    public static void main(String[] args) throws InterruptedException {
        Thread master = new Master(args.length > 0 ? args[0] : "master");
        master.start();
        master.join();
    }
}
