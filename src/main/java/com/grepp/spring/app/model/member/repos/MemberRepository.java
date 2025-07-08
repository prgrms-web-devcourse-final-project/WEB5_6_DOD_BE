package com.grepp.spring.app.model.member.repos;

import com.grepp.spring.app.model.member.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;


public interface MemberRepository extends JpaRepository<Member, String> {

    boolean existsByIdIgnoreCase(String id);

    boolean existsByEmailIgnoreCase(String email);

    boolean existsByTelIgnoreCase(String tel);

}
