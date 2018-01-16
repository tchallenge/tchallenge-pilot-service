package ru.tchallenge.participant.service.security.voucher;

import lombok.Builder;
import lombok.Data;
import ru.tchallenge.participant.service.utility.data.IdAware;

import java.time.OffsetDateTime;

@Data
@Builder
public final class SecurityVoucher implements IdAware {

    private final String id;
    private final String backlink;
    private final String accountEmail;
    private final String payload;
    private final OffsetDateTime createdAt;
    private final OffsetDateTime validUntil;

    public boolean isExpired() {
        return validUntil.isBefore(OffsetDateTime.now());
    }
}
