package com.trainingapp.trainingapp.domain.repository.user;

import com.trainingapp.trainingapp.domain.entity.user.Admin;
import java.util.List;
import java.util.Optional;

public interface AdminRepository {
    Admin save(Admin admin);
    Optional<Admin> findById(Long id);
    List<Admin> findByGymId(Long gymId);
}