package com.bitresolution.xtest.events

import org.junit.experimental.categories.Category

import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

import static com.bitresolution.xtest.commons.TestCategories.Unit

@Category(Unit)
class NonBlockingPublisherSpec extends PublisherSpec {

    ExecutorService executor

    def setup() {
        executor = Executors.newFixedThreadPool(10);
        publisher = new NonBlockingPublisher(executor)
    }

    def "should publish events to all subscribera"() {
        given:
        Subscriber a = Mock(Subscriber)
        Subscriber b = Mock(Subscriber)
        publisher.subscribe([a, b])
        XEvent event = Mock(XEvent)
        when:
        publisher.publish(event)
        executor.shutdown()
        executor.awaitTermination(5, TimeUnit.SECONDS)
        then:
        1 * a.process(event)
        1 * b.process(event)
    }
}