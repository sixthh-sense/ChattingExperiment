package com.example.demo.repository;

import com.example.demo.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByKakaoId(Long kakaoId);
    Optional<User> findByUsername(String username);
    Optional<User> findByNickname(String nickname);
    // 카카오 username이 실질적으론 email인데 이걸 프로그램에서 헷갈리진 않을까 약간 불안.
}
