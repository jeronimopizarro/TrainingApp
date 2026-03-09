package com.trainingapp.trainingapp.infrastructure.repository.jpa.mapper.user;

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
}