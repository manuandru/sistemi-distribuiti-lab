package it.unibo.ds.lab.sockets;

import javax.sound.midi.SysexMessage;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.stream.Collectors;

public record TestableProcess(Process process, File stdout, File stderr) implements AutoCloseable {

    private String readAll(File file) {
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            return reader.lines().collect(Collectors.joining("\n"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public BufferedWriter stdin() {
        return process.outputWriter();
    }

    public String stdoutAsText() {
        return readAll(stdout);
    }

    public String stderrAsText() {
        return readAll(stderr);
    }

    @Override
    public void close() {
        if (process.isAlive()) {
            process.destroyForcibly();
        }
        if (stdout.exists()) {
            stdout.delete();
        }
        if (stderr.exists()) {
            stderr.delete();
        }
    }

    public void printDebugInfo(String processName) {
        System.out.printf("Stdout of `%s`:\n> ", process.info().commandLine().orElse(processName));
        System.out.println(stdoutAsText().replace("\n", "\n> "));
        System.out.print("stderr of the same process:\n> ");
        System.out.println(stderrAsText().replace("\n", "\n> "));
        System.out.println();
    }
}
