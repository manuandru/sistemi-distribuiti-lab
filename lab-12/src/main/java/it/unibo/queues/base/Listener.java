package it.unibo.queues.base;

import com.rabbitmq.client.*;
import it.unibo.queues.Agent;

import java.io.IOException;

public class Listener extends Agent {

    public Listener(String name) {
        super(name);
    }

    @Override
    public void run(String myName, Connection connection) throws Exception {
        var channel = connection.createChannel();
        channel.exchangeDeclare("messages", BuiltinExchangeType.TOPIC);
        var declaration = channel.queueDeclare();
//                channel.queueDeclare(
//                        "messages", // queue
//                        false, // durable
//                        false, // exclusive
//                        false, // auto delete
//                        null // arguments
//                );
        channel.queueBind(declaration.getQueue(), "messages", "from.*");

        System.out.println("Listening for messages...");

        channel.basicConsume(declaration.getQueue(), new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                var sender = envelope.getRoutingKey().replace("from.", "");
                System.out.printf("[%s] %s\n", sender, new String(body));
            }
        });

        while (System.in.read() >= 0);

        channel.close();
        connection.close();
    }

    public static void main(String[] args) throws InterruptedException {
        Thread listener = new Listener(args.length > 0 ? args[0] : "nobody");
        listener.start();
        listener.join();
    }
}
