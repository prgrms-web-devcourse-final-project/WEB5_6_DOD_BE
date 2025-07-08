package com.grepp.spring.app.model.auth.token;

import com.grepp.spring.app.model.auth.token.entity.UserBlackList;
import org.springframework.data.repository.CrudRepository;

public interface UserBlackListRepository extends CrudRepository<UserBlackList, String> {
    void deleteByEmail(String email);
}
