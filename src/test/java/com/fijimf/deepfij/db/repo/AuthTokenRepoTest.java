package com.fijimf.deepfij.db.repo;

import com.fijimf.deepfij.db.model.user.AuthToken;
import com.fijimf.deepfij.db.repo.user.AuthTokenRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

@DataJpaTest
@Testcontainers
@ActiveProfiles("test-containers")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class AuthTokenRepoTest {

    @Autowired
    private AuthTokenRepo authTokenRepo;

    @BeforeEach
    void init() {
        authTokenRepo.deleteAll();
    }

    @Test
    void isEmptyInitially() {
        long count = authTokenRepo.count();
        assertThat(count).isEqualTo(0L);
    }

    @Test
    void createToken() {
        AuthToken t = authTokenRepo.saveAndFlush(new AuthToken(0L, UUID.randomUUID().toString(), LocalDateTime.now().plusMinutes(1)));
        assertThat(t.getId()).isGreaterThan(0L);
        assertThat(t.getToken()).isNotEmpty();
        assertThat(t.getExpiresAt()).isAfter(LocalDateTime.now());
    }


    @Test
    void duplicateTokenCheck() {
        String token = UUID.randomUUID().toString();
        authTokenRepo.saveAndFlush(new AuthToken(0L, token, LocalDateTime.now().plusMinutes(1)));
        assertThatExceptionOfType(DataIntegrityViolationException.class).isThrownBy(() -> authTokenRepo.saveAndFlush(new AuthToken(0L, token, LocalDateTime.now().plusMinutes(1))));
    }
}
