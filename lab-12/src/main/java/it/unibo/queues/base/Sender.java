package it.unibo.queues.base;

import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Connection;
import it.unibo.queues.Agent;

public class Sender extends Agent {

    private final String message;

    public Sender(String name, String message) {
        super(name);
        this.message = message;
    }

    @Override
    public void run(String myName, Connection connection) throws Exception {
        var channel = connection.createChannel();
        channel.exchangeDeclare("messages", BuiltinExchangeType.TOPIC);
        channel.basicPublish("messages", "from." + myName, null, message.getBytes());
        System.out.printf("[%s] Sent message %s\n", myName, message);
        channel.close();
        connection.close();
    }

    public static void main(String[] args) throws InterruptedException {
        Thread sender = new Sender(
                args.length > 0 ? args[0] : "nobody",
                args.length > 1 ? args[1] : "hello"
            );
        sender.start();
        sender.join();
    }
}
