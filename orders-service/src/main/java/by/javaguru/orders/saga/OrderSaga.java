package by.javaguru.orders.saga;

import by.javaguru.core.command.*;
import by.javaguru.core.event.*;
import by.javaguru.core.types.OrderStatus;
import by.javaguru.orders.service.OrderHistoryService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Component
@KafkaListener(topics = {
        "${orders.events.topic.name}",
        "${product.events.topic.name}",
        "${payments.events.topic.name}"
})
public class OrderSaga {

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final OrderHistoryService orderHistoryService;
    private final String productCommandsTopicName;
    private final String paymentCommandsTopicName;
    private final String ordersCommandsTopicName;

    public OrderSaga(KafkaTemplate<String, Object> kafkaTemplate,
                     OrderHistoryService orderHistoryService,
                     @Value("${product.command.topic.name}") String productCommandsTopicName,
                     @Value("${payment.commands.topic.name}") String paymentCommandsTopicName,
                     @Value("${orders.commands.topic.name}") String ordersCommandsTopicName) {
        this.kafkaTemplate = kafkaTemplate;
        this.productCommandsTopicName = productCommandsTopicName;
        this.orderHistoryService = orderHistoryService;
        this.paymentCommandsTopicName = paymentCommandsTopicName;
        this.ordersCommandsTopicName = ordersCommandsTopicName;
    }

    @KafkaHandler
    public void handleEvent(@Payload OrderCreatedEvent event) {
        ReservedProductCommand command = new ReservedProductCommand(
                event.productId(),
                event.productQuantity(),
                event.orderId()
        );

        kafkaTemplate.send(productCommandsTopicName, command);

        orderHistoryService.add(event.orderId(), OrderStatus.CREATED);
    }

    @KafkaHandler
    public void handleEvent(@Payload ProductReservedEvent event) {
        ProcessPaymentCommand processPaymentCommand = new ProcessPaymentCommand(
                event.orderId(),
                event.productId(),
                event.productPrice(),
                event.productQuantity()
        );

        kafkaTemplate.send(paymentCommandsTopicName, processPaymentCommand);
    }

    @KafkaHandler
    public void handleEvent(@Payload PaymentProcessedEvent event) {
        ApproveOrderCommand approveOrderCommand = new ApproveOrderCommand(event.orderId());
        kafkaTemplate.send(ordersCommandsTopicName, approveOrderCommand);
    }

    @KafkaHandler
    public void handleEvent(@Payload OrderApprovedEvent event) {
        orderHistoryService.add(event.orderId(), OrderStatus.APPROVED);
    }

    @KafkaHandler
    public void handleEvent(@Payload PaymentFailedEvent event) {
        CancelProductReservationCommand cancelProductReservationCommand =
                new CancelProductReservationCommand(
                        event.productId(),
                        event.orderId(),
                        event.productQuantity()
                );
        kafkaTemplate.send(productCommandsTopicName, cancelProductReservationCommand);
    }

    @KafkaHandler
    public void handleEvent(@Payload ProductReservationCancelledEvent event) {
        var rejectedOrderCommand = new RejectedOrderCommand(event.orderId());
        kafkaTemplate.send(ordersCommandsTopicName, rejectedOrderCommand);

        orderHistoryService.add(event.orderId(), OrderStatus.REJECTED);
    }

}
