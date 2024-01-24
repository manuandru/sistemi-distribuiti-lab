package it.unibo.queues.jobs;

import java.io.*;
import java.util.Objects;


public class Job {
    private final String argument;

    public Job(String arg) {
        this.argument = arg;
    }

    public String getArgument() {
        return argument;
    }

    public byte[] toBytes() {
        var buffer = new ByteArrayOutputStream();
        try (var writer = new DataOutputStream(buffer)) {
            writer.writeUTF(argument);
            return buffer.toByteArray();
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    public static Job fromBytes(byte[] buffer) {
        try(var reader = new DataInputStream(new ByteArrayInputStream(buffer))) {
            var arg = reader.readUTF();
            return new Job(arg);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Job that = (Job) o;
        return Objects.equals(argument, that.argument);
    }

    @Override
    public int hashCode() {
        return Objects.hash(argument);
    }

    @Override
    public String toString() {
        return "Job{" +
                "argument='" + argument + '\'' +
                '}';
    }
}
