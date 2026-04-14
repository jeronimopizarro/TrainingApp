package com.trainingapp.trainingapp.web.dto.user.member;

import java.time.LocalDate;
import java.util.List;

public record MemberSummaryResponse(
        MemberStats stats,
        List<MemberListItem> members
) {
    public record MemberStats(
            long total,
            long active,
            long inactive
    ) {}

    public record MemberListItem(
            Long id,
            String firstName,
            String lastName,
            String email,
            String dni,
            String planName,
            String subscriptionStatus,
            LocalDate endDate
    ) {}
}
