package com.trainingapp.trainingapp.domain.repository.user;

import com.trainingapp.trainingapp.domain.entity.user.Member;
import java.util.List;
import java.util.Optional;

public interface MemberRepository {
    Member save(Member member);
    Optional<Member> findById(Long id);
    List<Member> findByGymId(Long gymId);
}