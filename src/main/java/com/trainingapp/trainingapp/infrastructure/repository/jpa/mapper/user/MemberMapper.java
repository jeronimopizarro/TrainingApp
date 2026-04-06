package com.trainingapp.trainingapp.infrastructure.repository.jpa.mapper.user;

import com.trainingapp.trainingapp.domain.entity.user.Member;
import com.trainingapp.trainingapp.infrastructure.repository.jpa.entity.user.MemberJpaEntity;
import com.trainingapp.trainingapp.web.dto.user.member.MemberResponse;
import com.trainingapp.trainingapp.web.dto.user.member.RegisterMemberRequest;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class MemberMapper {

    public MemberJpaEntity toEntity(Member domain) {
        if (domain == null) return null;

        MemberJpaEntity entity = new MemberJpaEntity();
        entity.setId(domain.getId());
        entity.setFirstName(domain.getFirstName());
        entity.setLastName(domain.getLastName());
        entity.setEmail(domain.getEmail());
        entity.setPassword(domain.getPassword());
        entity.setDni(domain.getDni());
        entity.setRole(domain.getRole());
        entity.setActive(domain.isActive());
        entity.setGymId(domain.getGymId());
        entity.setBirthDate(domain.getBirthDate());
        entity.setPrimaryGoal(domain.getPrimaryGoal());

        return entity;
    }

    public Member toDomain(MemberJpaEntity entity) {
        if (entity == null) return null;

        return Member.restore(
                entity.getId(),
                entity.getFirstName(),
                entity.getLastName(),
                entity.getEmail(),
                entity.getPassword(),
                entity.getDni(),
                entity.getRole(),
                entity.isActive(),
                entity.getGymId(),
                entity.getBirthDate(),
                entity.getPrimaryGoal()
        );
    }
}