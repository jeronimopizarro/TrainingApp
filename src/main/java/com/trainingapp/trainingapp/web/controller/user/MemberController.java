package com.trainingapp.trainingapp.web.controller.user;

import com.trainingapp.trainingapp.application.usecase.user.RegisterMemberUseCase;
import com.trainingapp.trainingapp.web.dto.user.MemberResponse;
import com.trainingapp.trainingapp.web.dto.user.RegisterMemberRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/members")
public class MemberController {

    private final RegisterMemberUseCase registerMemberUseCase;

    public MemberController(RegisterMemberUseCase registerMemberUseCase) {
        this.registerMemberUseCase = registerMemberUseCase;
    }

    @PostMapping
    public ResponseEntity<MemberResponse> register(@RequestBody RegisterMemberRequest request) {
        MemberResponse response = registerMemberUseCase.execute(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}