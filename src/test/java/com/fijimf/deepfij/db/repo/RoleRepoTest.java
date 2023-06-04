package com.fijimf.deepfij.db.repo;

import com.fijimf.deepfij.db.model.user.Role;
import com.fijimf.deepfij.db.repo.user.RoleRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

@DataJpaTest
@Testcontainers
@ActiveProfiles("test-containers")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class RoleRepoTest {

    @Autowired
    private RoleRepo roleRepo;

    @BeforeEach
    void init() {

    }

    @Test
    void isNotEmptyInitially() {
        long count = roleRepo.count();
        assertThat(count).isEqualTo(2L);
    }

    @Test
    void createRole() {
        Role r = roleRepo.saveAndFlush(new Role(0L, "GUEST"));
        assertThat(r.getId()).isGreaterThan(0L);
        assertThat(r.getName()).isEqualTo("GUEST");
        assertThat(r.getAuthority()).isEqualTo("GUEST");
    }


    @Test
    void duplicateNameCheck() {
        Role r = roleRepo.saveAndFlush(new Role(0L, "GUEST"));
        assertThatExceptionOfType(DataIntegrityViolationException.class).isThrownBy(() -> roleRepo.saveAndFlush(new Role(0L, "GUEST")));
    }
}
