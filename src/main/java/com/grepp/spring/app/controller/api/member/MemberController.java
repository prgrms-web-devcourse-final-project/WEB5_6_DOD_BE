package com.grepp.spring.app.controller.api.member;

import com.grepp.spring.app.controller.api.member.payload.MemberInfoResponse;
import com.grepp.spring.app.controller.api.member.payload.ModifyMemberInfoResponse;
import com.grepp.spring.app.model.member.service.MemberService;
import com.grepp.spring.infra.auth.CurrentUser;
import com.grepp.spring.infra.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/member")
public class MemberController {

    private final MemberService memberService;

    @Operation(summary = "사용자 정보 조회", description = "현재 로그인 중인 사용자의 정보를 가져올 수 있습니다.")
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<MemberInfoResponse>> getMemberInfo(@CurrentUser String userId) {
        // Service 로 모든 비즈니스 로직 위임
        MemberInfoResponse response = memberService.getMemberInfoResponse(userId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @Operation(summary = "회원 탈퇴", description = "서비스 탈퇴를 진행합니다.")
    @DeleteMapping("/withdraw")
    public ResponseEntity<ApiResponse<Void>> withdraw(HttpServletResponse response, HttpServletRequest request,
        @CurrentUser String userId) {

        memberService.withdraw(userId, response, request);
        return ResponseEntity.ok(ApiResponse.noContent());
    }

    @Operation(summary = "회원 이름 수정", description = "사용자의 이름을 수정합니다.")
    @PatchMapping("/me")
    public ResponseEntity<ApiResponse<ModifyMemberInfoResponse>> modifyMemberInfo(@CurrentUser String userId, String username){

        ModifyMemberInfoResponse response = memberService.modifyMemberName(userId, username);
        return ResponseEntity.ok(ApiResponse.success("이름이 정상적으로 변경되었습니다.", response));
    }

    @Operation(summary = "회원 프로필 변경", description = "사용자의 프로필 캐릭터를 랜덤으로 변경합니다.")
    @PatchMapping("/profile")
    public ResponseEntity<ApiResponse<ModifyMemberInfoResponse>> modifyProfileImage(@CurrentUser String userId){

        ModifyMemberInfoResponse response = memberService.modifyProfileImage(userId);
        return ResponseEntity.ok(ApiResponse.success("프로필이 임의의 캐릭터로 변경되었습니다.", response));
    }
}
