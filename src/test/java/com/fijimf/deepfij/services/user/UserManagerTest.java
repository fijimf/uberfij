package com.fijimf.deepfij.services.user;

import com.fijimf.deepfij.db.repo.user.UserRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.assertj.core.api.Assertions.assertThat;

@Testcontainers
@ActiveProfiles("test-containers")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@SpringBootTest
public class UserManagerTest {

    @Autowired
    private UserManager userManager;
    @Autowired
    private UserRepo userRepo;

    @BeforeEach
    void init() {
        userRepo.deleteAll();
    }

    @Test
    void setAdminPasswordNew() {
        assertThat(userRepo.count()).isEqualTo(0);
        userManager.setAdminPassword("test123");
        assertThat(userRepo.findFirstByUsername("admin").orElseThrow().getPassword()).isNotBlank();
    }

    @Test
    void setAdminPasswordExisting() {
        assertThat(userRepo.count()).isEqualTo(0);
        userManager.setAdminPassword("test123");
        String oldPW = userRepo.findFirstByUsername("admin").orElseThrow().getPassword();
        assertThat(userRepo.count()).isEqualTo(1);
        userManager.setAdminPassword("test987");
        assertThat(userRepo.count()).isEqualTo(1);
        String newPW = userRepo.findFirstByUsername("admin").orElseThrow().getPassword();
        assertThat(newPW).isNotEqualTo(oldPW);
    }
}
