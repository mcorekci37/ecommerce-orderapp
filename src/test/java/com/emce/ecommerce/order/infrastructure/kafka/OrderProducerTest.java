package com.emce.ecommerce.order.infrastructure.kafka;

import com.emce.ecommerce.order.domain.entity.Order;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.kafka.core.KafkaTemplate;

import static org.mockito.Mockito.*;

class OrderProducerTest {

    @Mock
    private KafkaTemplate<String, String> kafkaTemplate;

    @Mock
    private ObjectMapper serializer;

    @InjectMocks
    private OrderProducer orderProducer;

    private Order mockOrder;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        mockOrder = mock(Order.class);
    }

    @Test
    void publishCreateEvent_ShouldPublishOrderCreationEvent() throws JsonProcessingException {
        // Arrange
        String serializedOrder = "{\"id\":123,\"username\":\"testUser\"}";
        when(serializer.writeValueAsString(mockOrder)).thenReturn(serializedOrder);

        // Act
        orderProducer.publishCreateEvent(mockOrder);

        // Assert
        verify(serializer, times(1)).writeValueAsString(mockOrder);
        verify(kafkaTemplate, times(1)).send(any(), any());
    }

    @Test
    void publishCancelEvent_ShouldPublishOrderCancellationEvent() throws JsonProcessingException {
        // Arrange
        String serializedOrder = "{\"id\":123,\"username\":\"testUser\"}";
        when(serializer.writeValueAsString(mockOrder)).thenReturn(serializedOrder);

        // Act
        orderProducer.publishCancelEvent(mockOrder);

        // Assert
        verify(serializer, times(1)).writeValueAsString(mockOrder);
        verify(kafkaTemplate, times(1)).send(any(), any());
    }

    @Test
    void publishEvent_ShouldHandleSerializationFailureGracefully() throws JsonProcessingException {
        // Arrange
        when(serializer.writeValueAsString(mockOrder)).thenThrow(new JsonProcessingException("Serialization error") {});

        // Act
        orderProducer.publishCreateEvent(mockOrder);

        // Assert
        verify(serializer, times(1)).writeValueAsString(mockOrder);
        verify(kafkaTemplate, never()).send(anyString(), anyString());
    }

    @Test
    void publishEvent_ShouldSendMessageToCorrectTopic() throws JsonProcessingException {
        // Arrange
        String topic = "test-topic";
        String serializedOrder = "{\"id\":123,\"username\":\"testUser\"}";
        when(serializer.writeValueAsString(mockOrder)).thenReturn(serializedOrder);

        // Act
        orderProducer.publishEvent(topic, mockOrder);

        // Assert
        verify(serializer, times(1)).writeValueAsString(mockOrder);
        verify(kafkaTemplate, times(1)).send(topic, serializedOrder);
    }
}
