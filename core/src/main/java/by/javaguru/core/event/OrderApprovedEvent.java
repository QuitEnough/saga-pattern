package by.javaguru.core.event;

import java.util.UUID;

public record OrderApprovedEvent(
        UUID orderId
) {
}
