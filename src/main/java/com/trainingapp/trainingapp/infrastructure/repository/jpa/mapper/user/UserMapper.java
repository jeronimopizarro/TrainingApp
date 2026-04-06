package com.trainingapp.trainingapp.infrastructure.repository.jpa.mapper.user;

import com.trainingapp.trainingapp.domain.entity.user.*;
import com.trainingapp.trainingapp.infrastructure.repository.jpa.entity.user.*;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    private final MemberMapper memberMapper;
    private final TrainerMapper trainerMapper;
    private final AdminMapper adminMapper;
    private final ReceptionistMapper receptionistMapper;

    public UserMapper(MemberMapper memberMapper, TrainerMapper trainerMapper, AdminMapper adminMapper,
                      ReceptionistMapper receptionistMapper) {
        this.memberMapper = memberMapper;
        this.trainerMapper = trainerMapper;
        this.adminMapper = adminMapper;
        this.receptionistMapper = receptionistMapper;
    }

    public User toDomain(UserJpaEntity entity) {
        if (entity == null) return null;

        if (entity instanceof MemberJpaEntity memberEntity) {
            return memberMapper.toDomain(memberEntity);

        } else if (entity instanceof TrainerJpaEntity trainerEntity) {
            return trainerMapper.toDomain(trainerEntity);

        } else if (entity instanceof AdminJpaEntity adminEntity) {
            return adminMapper.toDomain(adminEntity);

        }else if (entity instanceof ReceptionistJpaEntity receptionistEntity){
            return receptionistMapper.toDomain(receptionistEntity);
        }

        throw new IllegalArgumentException("Unknown user type.");
    }

    public UserJpaEntity toEntity(User user) {
        if (user == null) return null;

        if (user instanceof Member member) {
            return memberMapper.toEntity(member);

        } else if (user instanceof Trainer trainer) {
            return trainerMapper.toEntity(trainer);

        } else if (user instanceof Admin admin) {
            return adminMapper.toEntity(admin);

        }else if (user instanceof  Receptionist receptionist) {
            return receptionistMapper.toEntity(receptionist);
        }

        throw new IllegalArgumentException("Unknown user domain type.");
    }
}