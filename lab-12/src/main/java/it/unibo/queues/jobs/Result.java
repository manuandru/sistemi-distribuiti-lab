package it.unibo.queues.jobs;

import java.io.*;
import java.util.Objects;


public class Result {
    private final int value;

    public Result(int arg) {
        this.value = arg;
    }

    public int getValue() {
        return value;
    }

    public byte[] toBytes() {
        var buffer = new ByteArrayOutputStream();
        try (var writer = new DataOutputStream(buffer)) {
            writer.writeInt(value);
            return buffer.toByteArray();
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    public static Result fromBytes(byte[] buffer) {
        try(var reader = new DataInputStream(new ByteArrayInputStream(buffer))) {
            var value = reader.readInt();
            return new Result(value);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Result that = (Result) o;
        return Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return "Result{" +
                "value='" + value + '\'' +
                '}';
    }
}
