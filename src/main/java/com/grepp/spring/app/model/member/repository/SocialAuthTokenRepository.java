package com.grepp.spring.app.model.member.repository;

import com.grepp.spring.app.controller.api.auth.Provider;
import com.grepp.spring.app.model.member.entity.Member;
import com.grepp.spring.app.model.member.entity.SocialAuthToken;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SocialAuthTokenRepository extends JpaRepository<SocialAuthToken, Long> {


  Optional<SocialAuthToken> findByMember(Member member);
}
