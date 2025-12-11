package by.javaguru.core.event;

import java.util.UUID;

public record OrderCreatedEvent(
        UUID orderId,
        UUID customerId,
        UUID productId,
        Integer productQuantity
) {
}
