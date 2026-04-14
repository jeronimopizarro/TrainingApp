package com.trainingapp.trainingapp.infrastructure.repository.jpa.impl.user;

import com.trainingapp.trainingapp.domain.entity.user.Member;
import com.trainingapp.trainingapp.domain.enums.subscription.SubscriptionStatus;
import com.trainingapp.trainingapp.domain.repository.user.MemberRepository;
import com.trainingapp.trainingapp.infrastructure.repository.jpa.entity.user.MemberJpaEntity;
import com.trainingapp.trainingapp.infrastructure.repository.jpa.mapper.user.MemberMapper;
import com.trainingapp.trainingapp.infrastructure.repository.jpa.repository.user.MemberJpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public class MemberRepositoryImpl implements MemberRepository {

    private final MemberJpaRepository jpaRepository;
    private final MemberMapper mapper;

    public MemberRepositoryImpl(MemberJpaRepository jpaRepository, MemberMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    @Override
    public Member save(Member member){
        MemberJpaEntity entity = mapper.toEntity(member);
        MemberJpaEntity savedEntity = jpaRepository.save(entity);
        return mapper.toDomain(savedEntity);
    }

    @Override
    public Optional<Member> findById(Long id){
        return jpaRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public List<Member> findByGymId(Long gymId) {
        return jpaRepository.findByGymIdAndActiveTrue(gymId).stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public List<Member> findByGymIdAndStatus(Long gymId, String status) {
        LocalDate today = LocalDate.now();
        List<MemberJpaEntity> entities;

        if ("ACTIVE".equalsIgnoreCase(status)) {
            entities = jpaRepository.findByGymIdAndSubscriptionStatus(gymId, SubscriptionStatus.ACTIVE, today);
        } else {
            entities = jpaRepository.findByGymIdAndSubscriptionInactive(gymId, today);
        }

        return entities.stream().map(mapper::toDomain).toList();
    }

    @Override
    public Optional<Member> findByDni(String dni) {
        return jpaRepository.findByDni(dni)
                .map(mapper::toDomain);
    }
}