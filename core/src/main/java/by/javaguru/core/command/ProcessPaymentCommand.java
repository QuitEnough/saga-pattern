package by.javaguru.core.command;

import java.math.BigDecimal;
import java.util.UUID;

public record ProcessPaymentCommand(
        UUID orderId,
        UUID productId,
        BigDecimal productPrice,
        Integer productQuantity
) {
}
