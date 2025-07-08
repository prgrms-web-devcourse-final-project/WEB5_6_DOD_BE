package com.grepp.spring.app.model.auth.token;

import com.grepp.spring.app.model.auth.token.entity.TokenBlackList;
import org.springframework.data.repository.CrudRepository;

// 1. client 의 refresh token 과 redis 의 refresh token 이 다른 경우
// 2. logout 한 경우
// 사용자 아이디를 black list 에 추가
// 새롭게 인증을 수행하면 black list 에서 삭제
public interface AccessTokenBlackListRepository extends CrudRepository<TokenBlackList, String> {

}
