package com.trainingapp.trainingapp.web.controller.user;

import com.trainingapp.trainingapp.application.usecase.user.member.*;
import com.trainingapp.trainingapp.web.dto.user.member.MemberResponse;
import com.trainingapp.trainingapp.web.dto.user.member.RegisterMemberRequest;
import com.trainingapp.trainingapp.web.dto.user.member.UpdateMemberRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/members")
public class MemberController {

    private final RegisterMemberUseCase registerMemberUseCase;
    private final GetMemberByIdUseCase getMemberByIdUseCase;
    private final GetAllMembersByGymIdUseCase getAllMembersByGymIdUseCase;
    private final UpdateMemberUseCase updateMemberUseCase;
    private final DeleteMemberUseCase deleteMemberUseCase;

    public MemberController(RegisterMemberUseCase registerMemberUseCase,
                            GetMemberByIdUseCase getMemberByIdUseCase,
                            GetAllMembersByGymIdUseCase getAllMembersByGymIdUseCase,
                            UpdateMemberUseCase updateMemberUseCase,
                            DeleteMemberUseCase deleteMemberUseCase) {
        this.registerMemberUseCase = registerMemberUseCase;
        this.getMemberByIdUseCase = getMemberByIdUseCase;
        this.getAllMembersByGymIdUseCase = getAllMembersByGymIdUseCase;
        this.updateMemberUseCase = updateMemberUseCase;
        this.deleteMemberUseCase = deleteMemberUseCase;
    }

    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'GYM_ADMIN')")
    @PostMapping
    public ResponseEntity<MemberResponse> register(@RequestBody RegisterMemberRequest request) {
        MemberResponse response = registerMemberUseCase.execute(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'GYM_ADMIN', 'TRAINER', 'MEMBER')")
    @GetMapping("/{id}")
    public ResponseEntity<MemberResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(getMemberByIdUseCase.execute(id));
    }

    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'GYM_ADMIN', 'TRAINER')")
    @GetMapping
    public ResponseEntity<List<MemberResponse>> getAllByGymId(@RequestParam Long gymId) {
        return ResponseEntity.ok(getAllMembersByGymIdUseCase.execute(gymId));
    }

    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'GYM_ADMIN', 'MEMBER')")
    @PutMapping("/{id}")
    public ResponseEntity<MemberResponse> update(@PathVariable Long id,
                                                 @RequestBody UpdateMemberRequest request) {
        return ResponseEntity.ok(updateMemberUseCase.execute(id, request));
    }

    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'GYM_ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        deleteMemberUseCase.execute(id);
        return ResponseEntity.noContent().build();
    }
}