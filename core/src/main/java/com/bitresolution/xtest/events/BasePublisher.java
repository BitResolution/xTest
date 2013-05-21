package com.bitresolution.xtest.events;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

public abstract class BasePublisher<L extends Subscriber> implements Publisher<L> {
    protected final Set<L> subscribers;

    public BasePublisher() {
        this(new CopyOnWriteArraySet<L>());
    }

    protected BasePublisher(Set<L> subscribers) {
        this.subscribers = subscribers;
    }

    public boolean subscribe(L subscriber) {
        return subscribers.add(subscriber);
    }

    public boolean subscribe(Collection<L> subscribers) {
        return this.subscribers.addAll(subscribers);
    }

    public boolean unsubscribe(L subscriber) {
        return this.subscribers.remove(subscriber);
    }

    public boolean unsubscribe(Collection<L> subscribers) {
        return this.subscribers.removeAll(subscribers);
    }

    public Set<L> getSubscribers() {
        return Collections.unmodifiableSet(subscribers);
    }

    public abstract void publish(XEvent event);
}