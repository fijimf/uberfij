package com.fijimf.deepfij.db.repo.user;

import com.fijimf.deepfij.db.model.user.AuthToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AuthTokenRepo extends JpaRepository<AuthToken, Long> {
    Optional<AuthToken> findByToken(String token);
    List<AuthToken> findAllByUserId(long userId);
}
