package com.bitresolution.xtest.events

import org.junit.experimental.categories.Category

import static com.bitresolution.TestCategories.Unit

@Category(Unit)
class BlockingPublisherSpec extends PublisherSpec {

    def setup() {
        publisher = new BlockingPublisher<Subscriber>()
    }

    @Override
    def "should publish events to all subscribera"() {
        given:
        Subscriber a = Mock()
        Subscriber b = Mock()
        publisher.subscribe([a, b])
        XEvent event = Mock(XEvent)
        when:
        publisher.publish(event)
        then:
        1 * a.process(event)
        1 * b.process(event)
    }
}
