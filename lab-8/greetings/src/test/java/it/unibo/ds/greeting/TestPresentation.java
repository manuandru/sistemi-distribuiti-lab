package it.unibo.ds.greeting;

import com.google.protobuf.util.JsonFormat;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class TestPresentation {
    @Test
    public void testHelloRequestCreation() {
        HelloRequest request1 = HelloRequest.newBuilder()
                .setName("Giovanni")
                .build();
        HelloRequest request2 = HelloRequest.newBuilder()
                .setField(HelloRequest.getDescriptor().findFieldByNumber(HelloRequest.NAME_FIELD_NUMBER), "Giovanni")
                .build();

        assertEquals(request1, request2);
    }

    @Test
    public void testArrayOfHelloRequestCreation() {
        var giovanni = HelloRequest.newBuilder().setName("Giovanni").build();
        var andrea = HelloRequest.newBuilder().setName("Andrea").build();
        var stefano = HelloRequest.newBuilder().setName("Stefano").build();

        ArrayOfHelloRequests requests1 = ArrayOfHelloRequests.newBuilder()
                .addItems(giovanni)
                .addItems(andrea)
                .addItems(stefano)
                .build();

        ArrayOfHelloRequests requests2 = ArrayOfHelloRequests.newBuilder()
                .addAllItems(List.of(giovanni, andrea, stefano))
                .build();

        assertEquals(requests1, requests2);
    }

    @Test
    public void testHelloReplyCreation() {
        HelloReply reply1 = HelloReply.newBuilder()
                .setMessage("Hello Giovanni!")
                .build();
        HelloReply reply2 = HelloReply.newBuilder()
                .setField(HelloReply.getDescriptor().findFieldByNumber(HelloReply.MESSAGE_FIELD_NUMBER), "Hello Giovanni!")
                .build();

        assertEquals(reply1, reply2);
    }

    @Test
    public void testBinarySerialization() throws IOException {
        var giovanni = HelloRequest.newBuilder().setName("Giovanni").build();
        var andrea = HelloRequest.newBuilder().setName("Andrea").build();
        var stefano = HelloRequest.newBuilder().setName("Stefano").build();

        ArrayOfHelloRequests requests = ArrayOfHelloRequests.newBuilder()
                .addItems(giovanni)
                .addItems(andrea)
                .addItems(stefano)
                .build();

        ByteArrayOutputStream writer = new ByteArrayOutputStream();
        requests.writeTo(writer);

        System.out.println(bytesToString(writer.toByteArray()));

        ByteArrayInputStream reader = new ByteArrayInputStream(writer.toByteArray());
        var deserialzied = ArrayOfHelloRequests.parseFrom(reader);

        assertEquals(requests, deserialzied);
    }

    @Test
    public void testJsonSerialization() throws IOException {
        var giovanni = HelloRequest.newBuilder().setName("Giovanni").build();
        var andrea = HelloRequest.newBuilder().setName("Andrea").build();
        var stefano = HelloRequest.newBuilder().setName("Stefano").build();

        ArrayOfHelloRequests requests = ArrayOfHelloRequests.newBuilder()
                .addItems(giovanni)
                .addItems(andrea)
                .addItems(stefano)
                .build();

        StringWriter writer = new StringWriter();
        JsonFormat.printer().appendTo(requests, writer);

        System.out.println(writer.toString());

        StringReader reader = new StringReader(writer.toString());
        var requestsBuilder = ArrayOfHelloRequests.newBuilder();
        JsonFormat.parser().merge(reader, requestsBuilder);
        var deserialzied = requestsBuilder.build();

        assertEquals(requests, deserialzied);
    }

    private String bytesToString(byte[] bytes) {
        var strings = new ArrayList<String>(bytes.length);
        for (byte b : bytes) {
            strings.add(String.format("%02X", Byte.toUnsignedInt(b)));
        }
        return String.join(", ", strings);
    }
}
