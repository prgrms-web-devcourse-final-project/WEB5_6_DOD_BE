package com.grepp.spring.app.model.mainpage.service;

import com.grepp.spring.app.controller.api.mainpage.payload.response.UpdateActivationResponse;
import com.grepp.spring.app.model.mainpage.repository.MainPageScheduleMemberRepository;
import com.grepp.spring.app.model.schedule.entity.ScheduleMember;
import com.grepp.spring.infra.error.exceptions.mypage.ScheduleMemberIdNotFoundException;
import com.grepp.spring.infra.response.MyPageErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MainPageScheduleMemberService {

  private final MainPageScheduleMemberRepository mainPageScheduleMemberRepository;

  @Transactional
  public UpdateActivationResponse updateActivation(Long scheduleMemberId) {

    ScheduleMember sm = mainPageScheduleMemberRepository.findById(scheduleMemberId)
        .orElseThrow(() -> new ScheduleMemberIdNotFoundException(MyPageErrorCode.SCHEDULE_MEMBER_NOT_FOUND_EXCEPTION));

    sm.activation();

    return UpdateActivationResponse.from(sm);
  }

}
