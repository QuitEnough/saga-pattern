package by.javaguru.core.command;

import java.util.UUID;

public record RejectedOrderCommand(
        UUID orderId
) {
}
