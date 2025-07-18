package com.grepp.spring.app.controller.api.member;

import com.grepp.spring.app.controller.api.member.payload.MemberInfoResponse;
import com.grepp.spring.app.controller.api.member.payload.ModifyMemberInfoResponse;
import com.grepp.spring.app.model.member.entity.Member;
import com.grepp.spring.app.model.member.service.MemberService;
import com.grepp.spring.infra.response.ApiResponse;
import com.grepp.spring.infra.response.ResponseCode;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.NoSuchElementException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
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
    public ResponseEntity<ApiResponse<MemberInfoResponse>> getMemberInfo(Authentication authentication) {

        try {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            String username = userDetails.getUsername();

            Member member = memberService.findById(username).orElseThrow();

            MemberInfoResponse response = new MemberInfoResponse();
            response.setId(member.getId());
            response.setName(member.getName());
            response.setEmail(member.getEmail());
            response.setRole(member.getRole().name());
            response.setProfileImageNumber(member.getProfileImageNumber());
            response.setProvider(member.getProvider().name());

            return ResponseEntity.ok(ApiResponse.success(response));
            // 데이터베이스에 등록되지 않은 사용자인 경우
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error(ResponseCode.NOT_FOUND,"유저를 찾을 수 없습니다."));
            // 그 외 예상치 못한 예외 처리
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error(ResponseCode.INTERNAL_SERVER_ERROR, ResponseCode.INTERNAL_SERVER_ERROR.message()));
        }
        // 위 예외들은 추후 GlobalAdvice로 처리하는 방법을 생각중입니다.
    }

    @Operation(summary = "회원 탈퇴", description = "서비스 탈퇴를 진행합니다.")
    @DeleteMapping("/withdraw")
    public ResponseEntity<ApiResponse<Void>> withdraw(HttpServletResponse response, HttpServletRequest request) {

        // SecurityContextHolder 에서 사용자 정보 가져오기
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userId = authentication.getName();
        memberService.withdraw(userId, response, request);
        return ResponseEntity.ok(ApiResponse.noContent());
    }

    @Operation(summary = "회원 이름 수정", description = "사용자의 이름을 수정합니다.")
    @PatchMapping("/me")
    public ResponseEntity<ApiResponse<ModifyMemberInfoResponse>> modifyMemberInfo(String username){

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userId = authentication.getName();

        ModifyMemberInfoResponse response = memberService.modifyMemberInfo(userId, username);
        return ResponseEntity.ok(ApiResponse.success("이름이 정상적으로 변경되었습니다.", response));
    }

    @Operation(summary = "회원 프로필 변경", description = "사용자의 프로필 캐릭터를 랜덤으로 변경합니다.")
    @PatchMapping("/profile")
    public ResponseEntity<ApiResponse<ModifyMemberInfoResponse>> modifyProfileImage(){

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userId = authentication.getName();

        ModifyMemberInfoResponse response = memberService.modifyProfileImage(userId);
        return ResponseEntity.ok(ApiResponse.success("프로필이 임의의 캐릭터로 변경되었습니다.", response));
    }
}
