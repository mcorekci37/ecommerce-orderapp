package com.emce.ecommerce.order.infrastructure.kafka;

import com.emce.ecommerce.order.domain.entity.Order;
import com.emce.ecommerce.order.domain.events.EventType;
import com.emce.ecommerce.order.domain.events.OrderEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

class KafkaEventPublisherTest {

    @Mock
    private OrderProducer producer;

    @InjectMocks
    private KafkaEventPublisher kafkaEventPublisher;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void publish_whenEventTypeCreate(){
        Order order = mock(Order.class);
        OrderEvent event = new OrderEvent(order, EventType.CREATE);
        kafkaEventPublisher.publish(event);

        verify(producer, times(1)).publishCreateEvent(order);
        verify(producer, never()).publishCancelEvent(order);
    }

    @Test
    void publish_whenEventTypeCancel(){
        Order order = mock(Order.class);
        OrderEvent event = new OrderEvent(order, EventType.CANCEL);
        kafkaEventPublisher.publish(event);

        verify(producer, times(1)).publishCancelEvent(order);
        verify(producer, never()).publishCreateEvent(order);
    }

}
