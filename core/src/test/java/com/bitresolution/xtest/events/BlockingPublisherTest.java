package com.bitresolution.xtest.events;

import com.bitresolution.commons.Factory;
import com.bitresolution.commons.ListBuilder;
import com.google.common.base.Function;
import com.google.common.collect.Lists;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.op4j.Op;
import org.op4j.functions.Call;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

import static com.bitresolution.TestCategories.Integration;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

@Category(Integration.class)
public class BlockingPublisherTest {

    private static final Logger log = LoggerFactory.getLogger(BlockingPublisherTest.class);

    private static final int SUBSCRIPTION_EXECUTOR_THREADS = 1;
    private static final int PUBLISHER_THREADS = 5;
    private static final int SUBSCRIBER_COUNT = 100;
    private static final int EVENT_COUNT = 500;

    private BlockingPublisher<Subscriber> publisher;
    private List<Subscriber> subscribers;
    private List<SubscribeTask> subscriptionTasks;
    private List<PublishEventTask> publishTasks;

    @Before
    public void setup() {
        publisher = new BlockingPublisher<Subscriber>();

        subscribers = ListBuilder.using(new ArrayList<Subscriber>()).insert(SUBSCRIBER_COUNT, new Factory<Subscriber>() {
            @Override
            public Subscriber create() {
                return new MockSubscriber();
            }
        }).build();
        log.info("Built {} subscribers", subscribers.size());

        subscriptionTasks = Lists.transform(subscribers, new Function<Subscriber, SubscribeTask>() {
            @Override
            public SubscribeTask apply(@Nullable Subscriber subscriber) {
                return new SubscribeTask(subscriber, publisher);
            }
        });
        log.info("Built {} subscription tasks", subscriptionTasks.size());


        publishTasks = ListBuilder.using(new ArrayList<PublishEventTask>(EVENT_COUNT)).insert(EVENT_COUNT, new Factory<PublishEventTask>() {
            @Override
            public PublishEventTask create() {
                return new PublishEventTask(publisher);
            }
        }).build();
        log.info("Built {} publish tasks", publishTasks.size());
    }

    @Test
    public void shouldAllowConcurrentSubscribeUnsubscribeAndPublish() throws InterruptedException, ExecutionException {
        //given
        ScheduledExecutorService subscriptionService = Executors.newScheduledThreadPool(SUBSCRIPTION_EXECUTOR_THREADS);
        ScheduledExecutorService publisherService = Executors.newScheduledThreadPool(PUBLISHER_THREADS);

        //when
        List<Future<Boolean>> subscriptionFutures = new ArrayList<Future<Boolean>>(subscriptionTasks.size());
        for(SubscribeTask task : subscriptionTasks) {
            subscriptionFutures.add(subscriptionService.schedule(task, 100, TimeUnit.MILLISECONDS));
        }
        List<Future<Boolean>> publishingFutures = new ArrayList<Future <Boolean>>(publishTasks.size());
        for(PublishEventTask task : publishTasks) {
            publishingFutures.add(publisherService.schedule(task, 100, TimeUnit.MILLISECONDS));
        }

        //then
        for(Future<Boolean> f : subscriptionFutures) {
            assertThat(f.get(), is(true));
        }
        for(Future<Boolean> f : publishingFutures) {
            assertThat(f.get(), is(true));
        }
    }

    private static class MockSubscriber implements Subscriber {
        private List<XEvent> events;

        private MockSubscriber() {
            events = new ArrayList<XEvent>();
        }

        @Override
        public void process(XEvent event) {
            events.add(event);
        }
    }

    private static class SubscribeTask implements Callable<Boolean> {

        private final Subscriber subscriber;
        private final Publisher<Subscriber> publisher;

        private SubscribeTask(Subscriber subscriber, Publisher<Subscriber> publisher) {
            this.subscriber = subscriber;
            this.publisher = publisher;
        }

        @Override
        public Boolean call() throws Exception {
            return publisher.subscribe(subscriber);
        }
    }

    private static class PublishEventTask implements Callable<Boolean> {

        private final Publisher<Subscriber> publisher;

        private PublishEventTask(Publisher<Subscriber> publisher) {
            this.publisher = publisher;
        }

        @Override
        public Boolean call() throws Exception {
            publisher.publish(mock(XEvent.class));
            return true;
        }
    }
}
