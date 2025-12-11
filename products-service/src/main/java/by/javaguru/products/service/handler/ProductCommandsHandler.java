package by.javaguru.products.service.handler;

import by.javaguru.core.command.CancelProductReservationCommand;
import by.javaguru.core.command.ReservedProductCommand;
import by.javaguru.core.dto.Product;
import by.javaguru.core.event.ProductReservationCancelledEvent;
import by.javaguru.core.event.ProductReservationFailedEvent;
import by.javaguru.core.event.ProductReservedEvent;
import by.javaguru.products.service.ProductService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Component
@KafkaListener(topics = "${product.command.topic.name}")
public class ProductCommandsHandler {

    private final ProductService productService;
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private String productEventsTopicName;
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public ProductCommandsHandler(ProductService productService,
                                  KafkaTemplate<String, Object> kafkaTemplate,
                                  @Value("${product.events.topic.name}") String productEventsTopicName) {
        this.productService = productService;
        this.kafkaTemplate = kafkaTemplate;
        this.productEventsTopicName = productEventsTopicName;
    }

    @KafkaHandler
    public void handleCommand(@Payload ReservedProductCommand command) {
        try {
            var desiredProduct = new Product(command.productId(), command.productQuantity());
            var reservedProduct = productService.reserve(desiredProduct, command.orderId());

            var productReservedEvent = new ProductReservedEvent(
                    command.orderId(),
                    command.productId(),
                    reservedProduct.getPrice(),
                    command.productQuantity()
            );

            kafkaTemplate.send(productEventsTopicName, productReservedEvent);

        } catch (Exception e) {
            logger.error(e.getLocalizedMessage(), e);
            var productReservationFailedEvent = new ProductReservationFailedEvent(
                    command.productId(),
                    command.orderId(),
                    command.productQuantity()
            );

            kafkaTemplate.send(productEventsTopicName, productReservationFailedEvent);
        }
    }

    @KafkaHandler
    public void handleCommand(@Payload CancelProductReservationCommand command) {
        var productCancel = new Product(
                command.productId(),
                command.productQuantity()
        );
        productService.cancelReservation(productCancel, command.orderId());

        var productReservationCancelledEvent = new ProductReservationCancelledEvent(
                command.productId(),
                command.orderId()
        );
        kafkaTemplate.send(productEventsTopicName, productReservationCancelledEvent);
    }

}
