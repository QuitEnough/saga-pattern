package by.javaguru.core.command;

import java.util.UUID;

public record ReservedProductCommand(
        UUID productId,
        Integer productQuantity,
        UUID orderId
) {
}
