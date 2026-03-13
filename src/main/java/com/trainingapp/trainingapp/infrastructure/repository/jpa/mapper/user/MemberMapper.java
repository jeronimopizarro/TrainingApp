package com.trainingapp.trainingapp.infrastructure.repository.jpa.mapper.user;

import com.trainingapp.trainingapp.domain.entity.user.Member;
import com.trainingapp.trainingapp.infrastructure.repository.jpa.entity.user.MemberJpaEntity;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class MemberMapper {

    public MemberJpaEntity toJpaEntity(Member domain) {
        if (domain == null) return null;

        MemberJpaEntity entity = new MemberJpaEntity();

        // Datos del Padre (User)
        entity.setId(domain.getId());
        entity.setFirstName(domain.getFirstName());
        entity.setLastName(domain.getLastName());
        entity.setEmail(domain.getEmail());
        entity.setPassword(domain.getPassword());
        entity.setRole(domain.getRole());
        entity.setActive(domain.isActive());

        // Datos del Hijo (Member)
        entity.setGymId(domain.getGymId());
        entity.setBirthDate(domain.getBirthDate());
        entity.setPrimaryGoal(domain.getPrimaryGoal());
        entity.setQrAccessCode(domain.getQrAccessCode());

        // Lógica del Soft Delete (Borrado Lógico)
        if (!domain.isActive()) {
            entity.setDeletedAt(LocalDateTime.now());
        }

        return entity;
    }

    public Member toDomain(MemberJpaEntity entity) {
        if (entity == null) return null;

        Member member = new Member(
                entity.getFirstName(),
                entity.getLastName(),
                entity.getEmail(),
                entity.getPassword(),
                entity.getGymId(),
                entity.getBirthDate(),
                entity.getPrimaryGoal()
        );

        member.setId(entity.getId());
        member.setQrAccessCode(entity.getQrAccessCode());

        member.setActive(entity.isActive());

        return member;
    }
}