package by.javaguru.core.event;

import java.util.UUID;

public record PaymentProcessedEvent(
        UUID orderId,
        UUID paymentId
) {
}
