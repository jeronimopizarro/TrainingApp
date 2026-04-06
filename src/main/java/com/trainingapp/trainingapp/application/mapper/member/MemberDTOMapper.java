package com.trainingapp.trainingapp.application.mapper.member;

import com.trainingapp.trainingapp.domain.entity.user.Member;
import com.trainingapp.trainingapp.domain.enums.user.Role;
import com.trainingapp.trainingapp.web.dto.user.member.MemberResponse;
import com.trainingapp.trainingapp.web.dto.user.member.RegisterMemberRequest;
import org.springframework.stereotype.Component;

@Component
public class MemberDTOMapper {

    public Member toDomain(RegisterMemberRequest request, String encodedPassword) {
        if (request == null) return null;

        return Member.createNew(
                request.firstName(),
                request.lastName(),
                request.email(),
                encodedPassword,
                request.dni(),
                request.gymId(),
                request.birthDate(),
                request.primaryGoal()
        );
    }

    public MemberResponse toResponse(Member member) {
        if (member == null) return null;

        return new MemberResponse(
                member.getId(),
                member.getFirstName(),
                member.getLastName(),
                member.getEmail(),
                member.getDni(),
                member.isActive(),
                member.getGymId(),
                member.getBirthDate(),
                member.getPrimaryGoal()
        );
    }
}