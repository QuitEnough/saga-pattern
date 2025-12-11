package by.javaguru.core.event;

import java.util.UUID;

public record PaymentFailedEvent(
        UUID orderId,
        UUID paymentId,
        Integer productQuantity
) {
}
