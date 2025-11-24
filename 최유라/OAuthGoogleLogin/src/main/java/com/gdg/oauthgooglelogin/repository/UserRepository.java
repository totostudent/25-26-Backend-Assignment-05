package com.gdg.oauthgooglelogin.repository;

import com.gdg.oauthgooglelogin.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> { //Spring JPA는 레포지토리 자동 구현해 줌, 인터페이스 정의만 해주면 됨
    Optional<User> findByEmail(String userEmail); //커스텀 메서드?
    boolean existEmail(String email);
}
