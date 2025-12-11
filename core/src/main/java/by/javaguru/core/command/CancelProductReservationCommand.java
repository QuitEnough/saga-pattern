package by.javaguru.core.command;

import java.util.UUID;

public record CancelProductReservationCommand(
        UUID orderId,
        UUID productId,
        Integer productQuantity
) {
}
