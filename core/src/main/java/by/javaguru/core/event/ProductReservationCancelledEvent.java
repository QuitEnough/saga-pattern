package by.javaguru.core.event;

import java.util.UUID;

public record ProductReservationCancelledEvent(
        UUID productId,
        UUID orderId
) {
}
