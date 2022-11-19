package it.unibo.ds.lab.consensus.client;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import io.etcd.jetcd.*;
import io.etcd.jetcd.common.exception.EtcdException;
import io.etcd.jetcd.watch.WatchEvent;
import it.unibo.ds.lab.consensus.presentation.GsonUtils;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.time.Duration;
import java.util.Arrays;
import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;


public class ChatClient {

    private static final Gson gson = GsonUtils.createGson();
    private static final int BUFFER_SIZE = 1024;
    private static final byte[] buffer = new byte[BUFFER_SIZE];

    public static void main(String[] args){
        try {
            String username = args[0];
            String chat = args[1];
            String[] servers = Arrays.copyOfRange(args, 2, args.length);
            chatter(username, chat, servers);
        } catch (EtcdException e) {
            System.out.println("Cannot initialise chat client.");
            System.exit(1);
        } catch (IOException e) {
            System.out.println("Cannot use IO.");
            System.exit(1);
        } catch (InterruptedException e) {
            System.out.println("Client interruption.");
            System.exit(1);
        }
    }

    private static void chatter(String username, String chat, String ... servers) throws IOException, InterruptedException {
        try {
            System.out.printf("Contacting host(s) %s...\n", Arrays.toString(servers));
            Client client = Client.builder().endpoints(servers).build();
            System.out.println("Connection established");
            CountDownLatch latch = new CountDownLatch(1);
            chatImpl(username, chat, client, latch);
            latch.await();
            System.exit(0);
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        } catch (InterruptedException | ExecutionException e) {
            // Silently ignores
        }
    }

    private static void chatImpl(String username, String chat, Client client, CountDownLatch latch) throws IOException, ExecutionException, InterruptedException {
        propagateServerToStdout(username, chat, client, latch);
        propagateStdinToServer(username, chat, client);
    }

    private static void propagateStdinToServer(String username, String chat, Client client) throws IOException, ExecutionException, InterruptedException {
        InputStream inputStream = System.in;
        KV kv = client.getKVClient();
        var key = ByteSequence.from(chat.getBytes());
        while (true) {
            int readBytes = System.in.read(buffer);
            if (readBytes < 0) {
                var message = new Message(username, "exited!\n".getBytes());
                kv.put(key, ByteSequence.from(gson.toJson(message).getBytes())).get();
                break;
            } else {
                var msgBody = new byte[readBytes];
                System.arraycopy(buffer, 0, msgBody, 0, readBytes);
                var message = new Message(username, msgBody);
                kv.put(key, ByteSequence.from(gson.toJson(message).getBytes())).get();
            }

        }
    }

    private static void propagateServerToStdout(String username, String chat, Client client, CountDownLatch latch) {
        OutputStream outputStream = System.out;
        ByteSequence key = ByteSequence.from(chat.getBytes());
        Watch.Listener listener = Watch.listener(response -> {
            response.getEvents().stream()
                    .map(WatchEvent::getKeyValue)
                    .map(KeyValue::getValue)
                    .map(ByteSequence::toString)
                    .map(str -> gson.fromJson(str, Message.class))
                    .map(Message::toPrettyString)
                    .map(String::getBytes)
                    .forEach(bytes -> {
                        try {
                            outputStream.write(bytes);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    });
        });
        Watch watch = client.getWatchClient();
        Watch.Watcher watcher = watch.watch(key, listener);
    }
}
