package sd.lab.linda.textual.impl;

import sd.lab.linda.textual.RegexTemplate;
import sd.lab.linda.textual.StringTuple;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;

public class PendingRequest {
    private final RequestTypes requestType;
    private final RegexTemplate template;
    private final CompletableFuture<StringTuple> promise;

    public PendingRequest(RequestTypes requestType, RegexTemplate template, CompletableFuture<StringTuple> promise) {
        this.requestType = Objects.requireNonNull(requestType);
        this.template = Objects.requireNonNull(template);
        this.promise = Objects.requireNonNull(promise);
    }

    public RequestTypes getRequestType() {
        return requestType;
    }

    public RegexTemplate getTemplate() {
        return template;
    }

    public CompletableFuture<StringTuple> getPromise() {
        return promise;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PendingRequest that = (PendingRequest) o;
        return requestType == that.requestType &&
                template.equals(that.template) &&
                promise.equals(that.promise);
    }

    @Override
    public int hashCode() {
        return Objects.hash(requestType, template, promise);
    }

    @Override
    public String toString() {
        return "PendingRequest{" +
                "requestType=" + requestType +
                ", template=" + template +
                ", promise=" + promise +
                '}';
    }
}
