package com.trainingapp.trainingapp.application.usecase.user.member;

import com.trainingapp.trainingapp.application.validator.UserRegistrationValidator;
import com.trainingapp.trainingapp.domain.entity.user.Member;
import com.trainingapp.trainingapp.domain.repository.user.MemberRepository;
import com.trainingapp.trainingapp.domain.repository.user.UserRepository;
import com.trainingapp.trainingapp.infrastructure.repository.jpa.config.security.SecurityUtils;
import com.trainingapp.trainingapp.infrastructure.repository.jpa.mapper.user.MemberMapper;
import com.trainingapp.trainingapp.web.dto.user.member.MemberResponse;
import com.trainingapp.trainingapp.web.dto.user.member.RegisterMemberRequest;
import jakarta.transaction.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class RegisterMemberUseCase {
    private final MemberRepository memberRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final SecurityUtils securityUtils;
    private final MemberMapper memberMapper;
    private final UserRegistrationValidator registrationValidator;

    public RegisterMemberUseCase(MemberRepository memberRepository, UserRepository userRepository, PasswordEncoder passwordEncoder,
                                 SecurityUtils securityUtils, MemberMapper memberMapper,
                                 UserRegistrationValidator registrationValidator) {
        this.memberRepository = memberRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.securityUtils = securityUtils;
        this.memberMapper = memberMapper;
        this.registrationValidator = registrationValidator;
    }

    @Transactional
    public MemberResponse execute(RegisterMemberRequest request) {
        securityUtils.validateSameGym(request.gymId());
        registrationValidator.validateEmailIsUnique(request.email());

        String encodedPassword = passwordEncoder.encode(request.password());

        Member member = memberMapper.toDomain(request, encodedPassword);

        Member savedMember = memberRepository.save(member);

        return memberMapper.toResponse(savedMember);
    }

}
