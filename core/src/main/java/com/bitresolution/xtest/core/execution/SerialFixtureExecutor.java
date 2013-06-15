package com.bitresolution.xtest.core.execution;

import com.bitresolution.xtest.events.Publisher;
import com.bitresolution.xtest.events.XEventSource;

import java.util.concurrent.Future;

import static com.bitresolution.xtest.core.events.CompleteEvent.complete;
import static com.bitresolution.xtest.core.events.QueuedEvent.queued;
import static com.bitresolution.xtest.core.events.StartEvent.start;

public class SerialFixtureExecutor implements FixtureExecutor, XEventSource {

    private final FixtureInvoker fixtureInvoker;
    private final Publisher publisher;

    public SerialFixtureExecutor(FixtureInvoker fixtureInvoker, Publisher publisher) {
        this.fixtureInvoker = fixtureInvoker;
        this.publisher = publisher;
    }

    @Override
    public void execute(Fixtures fixtures) {
        publisher.publish(start(this));
        for(Fixture fixture : fixtures) {
            execute(fixture);
        }
        publisher.publish(complete(this));
    }

    private Future<Boolean> execute(Fixture fixture) {
        publisher.publish(queued(fixture));
        return fixtureInvoker.execute(fixture, publisher);
    }
}
