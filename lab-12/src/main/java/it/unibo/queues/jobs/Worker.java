package it.unibo.queues.jobs;

import com.rabbitmq.client.*;
import it.unibo.queues.Agent;

import java.io.IOException;

public class Worker extends Agent {

    private final String masterName;

    public Worker(String name, String masterName) {
        super(name);
        this.masterName = masterName;
    }

    @Override
    public void run(String myName, Connection connection) throws Exception {
        var channel = connection.createChannel();
        var todoQueue = masterName + ".todolist";
        var resultsQueue = masterName + ".results";

        // TODO declare queues

        log("Listening for jobs...");

        // NOTICE autoAck = false
        channel.basicConsume(todoQueue, false, new JobConsumer(channel));
    }

    private class JobConsumer extends DefaultConsumer {
        public JobConsumer(Channel channel) {
            super(channel);
        }

        private static final String VOWELS = "aeiouAEIOU";

        private Result countVowels(Job job) {
            var string = job.getArgument();
            int vowels = 0;
            for (int i = 0; i < string.length(); i++) {
                if (VOWELS.indexOf(string.charAt(i)) >= 0) {
                    vowels++;
                }
            }
            return new Result(vowels);
        }

        @Override
        public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
            // TODO somewhere in this method the ack should be explicitly managed

            Job job = Job.fromBytes(body);
            log("Received new job request: %s", job);
            Result result = null; // TODO count the vowels for the current job
            log("Counted vowels: %d", result.getValue());
            // TODO return the partial result to the master
            log("Returning result to master: %s", result);
        }
    }

    public static void main(String[] args) throws InterruptedException {
        Thread worker = new Worker(
                args.length > 0 ? args[0] : "worker-" + System.currentTimeMillis(),
                args.length > 1 ? args[1] : "master"
            );
        worker.start();
        worker.join();
    }
}
