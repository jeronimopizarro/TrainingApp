package com.trainingapp.trainingapp.web.dto.user.staff;

import java.util.List;

public record StaffSummaryResponse(
    List<StaffMemberResponse> staffMembers,
    StaffStats stats
) {
    public record StaffStats(
        long total,
        long trainers,
        long receptionists
    ) {}
}
