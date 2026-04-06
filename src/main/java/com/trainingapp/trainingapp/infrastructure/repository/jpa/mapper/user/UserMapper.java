package com.trainingapp.trainingapp.infrastructure.repository.jpa.mapper.user;

import com.trainingapp.trainingapp.domain.entity.user.Admin;
import com.trainingapp.trainingapp.domain.entity.user.Member;
import com.trainingapp.trainingapp.domain.entity.user.Trainer;
import com.trainingapp.trainingapp.domain.entity.user.User;
import com.trainingapp.trainingapp.infrastructure.repository.jpa.entity.user.AdminJpaEntity;
import com.trainingapp.trainingapp.infrastructure.repository.jpa.entity.user.MemberJpaEntity;
import com.trainingapp.trainingapp.infrastructure.repository.jpa.entity.user.TrainerJpaEntity;
import com.trainingapp.trainingapp.infrastructure.repository.jpa.entity.user.UserJpaEntity;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    private final MemberMapper memberMapper;
    private final TrainerMapper trainerMapper;
    private final AdminMapper adminMapper;

    public UserMapper(MemberMapper memberMapper, TrainerMapper trainerMapper, AdminMapper adminMapper) {
        this.memberMapper = memberMapper;
        this.trainerMapper = trainerMapper;
        this.adminMapper = adminMapper;
    }

    public User toDomain(UserJpaEntity entity) {
        if (entity == null) return null;

        if (entity instanceof MemberJpaEntity memberEntity) {
            return memberMapper.toDomain(memberEntity);

        } else if (entity instanceof TrainerJpaEntity trainerEntity) {
            return trainerMapper.toDomain(trainerEntity);

        } else if (entity instanceof AdminJpaEntity adminEntity) {
            return adminMapper.toDomain(adminEntity);
        }

        throw new IllegalArgumentException("Unknown user type.");
    }

    public UserJpaEntity toEntity(User user) {
        if (user == null) return null;

        // Gracias al polimorfismo, detectamos qué tipo de usuario es en realidad
        if (user instanceof Member member) {
            return memberMapper.toEntity(member);
        } else if (user instanceof Trainer trainer) {
            return trainerMapper.toEntity(trainer);
        } else if (user instanceof Admin admin) {
            return adminMapper.toEntity(admin);
        }

        throw new IllegalArgumentException("Unknown user domain type.");
    }
}