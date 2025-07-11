package com.grepp.spring.app.model.member.repository;

import com.grepp.spring.app.model.member.entity.Member;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;


public interface MemberRepository extends JpaRepository<Member, String> {

    boolean existsByIdIgnoreCase(String id);

    boolean existsByEmailIgnoreCase(String email);

    boolean existsByTelIgnoreCase(String tel);

    Optional<Member> findById(String id);

}
