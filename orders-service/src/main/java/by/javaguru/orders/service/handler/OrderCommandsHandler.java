package by.javaguru.orders.service.handler;

import by.javaguru.core.command.ApproveOrderCommand;
import by.javaguru.core.command.RejectedOrderCommand;
import by.javaguru.orders.service.OrderService;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Component
@KafkaListener(topics = "${orders.commands.topic.name}")
public class OrderCommandsHandler {

    private final OrderService orderService;

    public OrderCommandsHandler(OrderService orderService) {
        this.orderService = orderService;
    }

    @KafkaHandler
    public void handleCommand(@Payload ApproveOrderCommand approveOrderCommand) {
        orderService.approveOrder(approveOrderCommand.orderId());
    }

    @KafkaHandler
    public void handleCommand(@Payload RejectedOrderCommand rejectedOrderCommand) {
        orderService.rejectOrder(rejectedOrderCommand.orderId());
    }
}
