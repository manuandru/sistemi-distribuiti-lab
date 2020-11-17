package sd.lab.linda.textual.impl;

import org.apache.commons.collections4.MultiSet;
import org.apache.commons.collections4.multiset.HashMultiSet;
import sd.lab.linda.textual.RegexTemplate;
import sd.lab.linda.textual.StringTuple;
import sd.lab.linda.textual.TextualSpace;

import java.util.Iterator;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;

public class TextualSpaceImpl implements TextualSpace {

    private final ExecutorService executor;
    private final String name;

    private final MultiSet<StringTuple> tuples = new HashMultiSet<>();
    private final MultiSet<PendingRequest> pendingRequests = new HashMultiSet<>();

    public TextualSpaceImpl(final ExecutorService executor) {
        this(TextualSpace.class.getSimpleName(), executor);
    }

    public TextualSpaceImpl(final String name, final ExecutorService executor) {
        this.name = Objects.requireNonNull(name) + "#" + System.identityHashCode(this);
        this.executor = Objects.requireNonNull(executor);
    }

    @Override
    public CompletableFuture<StringTuple> rd(final RegexTemplate template) {
        log("Requested `rd` operation on template: %s", template);
        final CompletableFuture<StringTuple> promise = new CompletableFuture<>();
        executor.execute(() -> this.handleRd(template, promise));
        return promise;
    }

    @Override
    public CompletableFuture<StringTuple> in(final RegexTemplate template) {
        log("Requested `in` operation on template: %s", template);
        final CompletableFuture<StringTuple> promise = new CompletableFuture<>();
        executor.execute(() -> this.handleIn(template, promise));
        return promise;
    }

    @Override
    public CompletableFuture<StringTuple> out(final StringTuple tuple) {
        log("Requested `out` operation for tuple: %s", tuple);
        final CompletableFuture<StringTuple> promise = new CompletableFuture<>();
        executor.execute(() -> this.handleOut(tuple, promise));
        return promise;
    }

    @Override
    public CompletableFuture<MultiSet<? extends StringTuple>> get() {
        log("Requested `get` operation");
        final CompletableFuture<MultiSet<? extends StringTuple>> promise = new CompletableFuture<>();
        executor.execute(() -> this.handleGet(promise));
        return promise;
    }

    @Override
    public CompletableFuture<Integer> count() {
        log("Requested `count` operation");
        final CompletableFuture<Integer> promise = new CompletableFuture<>();
        executor.execute(() -> this.handleGetSize(promise));
        return promise;
    }

    private synchronized void handleRd(final RegexTemplate template, final CompletableFuture<StringTuple> promise) {
        log("Handling `rd` operation on template: %s", template);
        final Optional<StringTuple> matching = tuples.stream()
                .filter(template::matches)
                .findFirst();

        if (matching.isPresent()) {
            log("A tuple matching template %s was found: %s", template, matching.get());
            promise.complete(matching.get());
        } else {
            pendingRequests.add(new PendingRequest(RequestTypes.RD, template, promise));
            log("No tuple matching template %s was found, the `rd` operation will be suspended", template);
        }
    }

    private synchronized void handleIn(final RegexTemplate template, final CompletableFuture<StringTuple> promise) {
        final Iterator<StringTuple> i = tuples.iterator();
        while (i.hasNext()) {
            final StringTuple tuple = i.next();
            if (template.matches(tuple)) {
                i.remove();
                log("A tuple matching template %s was removed: %s", template, tuple);
                promise.complete(tuple);
                return;
            }
        }

        pendingRequests.add(new PendingRequest(RequestTypes.IN, template, promise));
        log("No tuple matching template %s was found, the `in` operation will be suspended", template);
    }

    private synchronized void handleOut(final StringTuple tuple, final CompletableFuture<StringTuple> promise) {
        log("Handling `out` operation on tuple: %s", tuple);
        final Iterator<PendingRequest> i = pendingRequests.iterator();
        while (i.hasNext()) {
            final PendingRequest pending = i.next();
            if (pending.getTemplate().matches(tuple)) {
                i.remove();
                pending.getPromise().complete(tuple);
                log("Resuming operation %s because tuple %s has been inserted", pending, tuple);
                if (pending.getRequestType() == RequestTypes.IN) {
                    log("No actual tuple has been inserted in this tuple space since tuple %s has been consumed by a pending `in` operation", tuple);
                    promise.complete(tuple);
                    return;
                }
            }
        }
        log("The tuple %s has been actually inserted in this tuple space", tuple);
        tuples.add(tuple);
        promise.complete(tuple);
    }

    private synchronized void handleGetSize(CompletableFuture<Integer> promise) {
        log("Handling `get` operation");
        promise.complete(tuples.size());
    }

    private synchronized void handleGet(CompletableFuture<MultiSet<? extends StringTuple>> promise) {
        log("Handling `get` operation");
        promise.complete(new HashMultiSet<>(tuples));
    }

    protected void log(String format, Object... args) {
        System.out.printf("[" + getName() + "] " + format + "\n", args);
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "TextualSpaceImpl{" +
                ", name='" + name + '\'' +
                ", tuples=" + tuples +
                ", pendingRequests=" + pendingRequests +
                '}';
    }
}
