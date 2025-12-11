package by.javaguru.core.event;

import java.util.UUID;

public record ProductReservationFailedEvent(
        UUID productId,
        UUID orderId,
        Integer productQuantity
) {
}
