package com.trainingapp.trainingapp.application.mapper.access;

import com.trainingapp.trainingapp.domain.entity.Access.AccessLog;
import com.trainingapp.trainingapp.domain.entity.user.Member;
import com.trainingapp.trainingapp.domain.repository.user.MemberRepository;
import com.trainingapp.trainingapp.web.dto.access.AccessLogResponse;
import com.trainingapp.trainingapp.web.dto.access.GymAccessSummaryResponse;
import com.trainingapp.trainingapp.web.dto.access.MemberAccessSummaryResponse;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class AccessLogDTOMapper {

    private final MemberRepository memberRepository;

    public AccessLogDTOMapper(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    public AccessLogResponse toResponse(AccessLog domain) {
        if (domain == null) return null;

        String firstName = "Socio";
        String lastName = "Desconocido";

        if (domain.getMemberId() != null) {
            Member member = memberRepository.findById(domain.getMemberId()).orElse(null);
            if (member != null) {
                firstName = member.getFirstName();
                lastName = member.getLastName();
            }
        }

        return new AccessLogResponse(
                domain.getId(),
                domain.getMemberId(),
                firstName,
                lastName,
                domain.getTimestamp(),
                domain.isAccessGranted(),
                domain.getMessage()
        );
    }

    public List<AccessLogResponse> toResponseList(List<AccessLog> domains) {
        if (domains == null || domains.isEmpty()) return List.of();

        return domains.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }


    public GymAccessSummaryResponse toGymSummaryResponse(long successfulToday, long failedToday, List<AccessLog> logs) {
        return new GymAccessSummaryResponse(
                successfulToday,
                failedToday,
                toResponseList(logs)
        );
    }

    public MemberAccessSummaryResponse toMemberSummaryResponse(long totalVisits, List<AccessLog> logs) {
        return new MemberAccessSummaryResponse(
                totalVisits,
                toResponseList(logs)
        );
    }
}
