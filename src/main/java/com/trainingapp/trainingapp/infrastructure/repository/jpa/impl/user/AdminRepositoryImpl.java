package com.trainingapp.trainingapp.infrastructure.repository.jpa.impl.user;

import com.trainingapp.trainingapp.domain.entity.user.Admin;
import com.trainingapp.trainingapp.domain.repository.user.AdminRepository;
import com.trainingapp.trainingapp.infrastructure.repository.jpa.entity.user.AdminJpaEntity;
import com.trainingapp.trainingapp.infrastructure.repository.jpa.mapper.user.AdminMapper;
import com.trainingapp.trainingapp.infrastructure.repository.jpa.repository.user.AdminJpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class AdminRepositoryImpl implements AdminRepository {

    private final AdminJpaRepository jpaRepository;
    private final AdminMapper mapper;


    public AdminRepositoryImpl(AdminJpaRepository jpaRepository, AdminMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    @Override
    public Admin save(Admin admin) {
        AdminJpaEntity entity = mapper.toEntity(admin);
        AdminJpaEntity savedEntity = jpaRepository.save(entity);
        return mapper.toDomain(savedEntity);
    }

    @Override
    public Optional<Admin> findById(Long id) {
        return jpaRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public List<Admin> findByGymId(Long gymId) {
        return jpaRepository.findByGymIdAndActiveTrue(gymId).stream()
                .map(mapper::toDomain)
                .toList();
    }
}
