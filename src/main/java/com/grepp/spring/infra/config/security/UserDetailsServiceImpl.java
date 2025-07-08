package com.grepp.spring.infra.config.security;

import com.grepp.spring.app.model.auth.domain.Principal;
import com.grepp.spring.app.model.member.domain.Member;
import com.grepp.spring.app.model.member.repos.MemberRepository;
import java.util.ArrayList;
import java.util.List;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserDetailsServiceImpl implements UserDetailsService {

    private final MemberRepository memberRepository;

    @Override
    public UserDetails loadUserByUsername(String userId) {
        Member member = memberRepository.findById(userId)
            .orElseThrow(() -> new UsernameNotFoundException(userId));
        List<SimpleGrantedAuthority> authorities = findUserAuthorities(userId);
        return Principal.createPrincipal(member, authorities);
    }

    public List<SimpleGrantedAuthority> findUserAuthorities(String userId){
        Member member = memberRepository.findById(userId)
            .orElseThrow(() -> new UsernameNotFoundException(userId));

        List<SimpleGrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority(member.getRole().name()));
        return authorities;
    }
}