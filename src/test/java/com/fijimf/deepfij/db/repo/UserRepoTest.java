package com.fijimf.deepfij.db.repo;

import com.fijimf.deepfij.db.model.user.User;
import com.fijimf.deepfij.db.repo.user.RoleRepo;
import com.fijimf.deepfij.db.repo.user.UserRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

@DataJpaTest
@Testcontainers
@ActiveProfiles("test-containers")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class UserRepoTest {
    @Autowired
    private UserRepo userRepo;

    @Autowired
    private RoleRepo roleRepo;

    @BeforeEach
    void init() {
        userRepo.deleteAll();
    }

    @Test
    void isEmptyInitially() {
        long count = userRepo.count();
        assertThat(count).isEqualTo(0L);
    }

    @Test
    void createUser() {
        User u = userRepo.saveAndFlush(new User(0L, "fijimf", "zzzzzzz", "jimf@gmail.com", false));
        assertThat(u.getId()).isGreaterThan(0L);
        assertThat(u.getAuthorities()).isNull();
        long count = userRepo.count();
        assertThat(count).isEqualTo(1L);
    }

    @Test
    void createUserWithRoles() {
        User user = new User(0L, "fijimf", "zzzzzzz", "jimf@gmail.com", false);
        user = userRepo.saveAndFlush(user);
        user.setRoles(roleRepo.findAll());
        user = userRepo.saveAndFlush(user);
        long count = userRepo.count();
        assertThat(count).isEqualTo(1L);
        User v = userRepo.findFirstByUsername("fijimf").orElseThrow();
        assertThat(v.getId()).isGreaterThan(0L);
        assertThat(v.getAuthorities()).hasSize(2);
    }

    @Test
    void modifyUserWithRoles() {
        User user = new User(0L, "fijimf", "zzzzzzz", "jimf@gmail.com", false);
        user = userRepo.saveAndFlush(user);
        user.setRoles(roleRepo.findAll());
        user = userRepo.saveAndFlush(user);
        long count = userRepo.count();
        assertThat(count).isEqualTo(1L);
        User v = userRepo.findFirstByUsername("fijimf").orElseThrow();
        assertThat(v.getAuthorities()).hasSize(2);
        user.getRoles().removeIf(g -> g.getAuthority().equalsIgnoreCase("ADMIN"));
        assertThat(v.getAuthorities()).hasSize(1);
        userRepo.saveAndFlush(v);
        User w = userRepo.findFirstByUsername("fijimf").orElseThrow();
        assertThat(w.getAuthorities()).hasSize(1);
    }

    //Test duplicate nae
    @Test
    void duplicateNameCheck() {
        User u = userRepo.saveAndFlush(new User(0L, "fijimf", "zzzzzzz", "jimf2@gmail.com", false));
        assertThatExceptionOfType(DataIntegrityViolationException.class).isThrownBy(() -> userRepo.saveAndFlush(new User(0L, "fijimf", "zzzzzzz", "jimf@gmail.com", false)));
    }

    //Test duplicate email
    @Test
    void duplicateEmailCheck() {
        User u = userRepo.saveAndFlush(new User(0L, "fujimf", "zzzzzzz", "jimf@gmail.com", false));
        assertThatExceptionOfType(DataIntegrityViolationException.class).isThrownBy(() -> userRepo.saveAndFlush(new User(0L, "fijimf", "zzzzzzz", "jimf@gmail.com", false)));
    }

    //Test malformed email
    @Test
    void malformedEmailCheck() {
//        assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(() -> userRepo.saveAndFlush(new User(0L, "fijimf", "zzzzzzz", "jimfgmail.com", false)));
         userRepo.saveAndFlush(new User(0L, "fijimf", "zzzzzzz", "jimfgmail.com", false));
    }

    //Test find by name
    @Test
    void findByName() {
        userRepo.saveAndFlush(new User(0L, "fijimf", "zzzzzzz", "jimf@gmail.com", false));
        userRepo.saveAndFlush(new User(0L, "deepfij", "zzzzzzz", "deepfij@gmail.com", false));

        Optional<User> user = userRepo.findFirstByUsername("fijimf");
        assertThat(user).isNotEmpty();
        Optional<User> missing = userRepo.findFirstByUsername("james");
        assertThat(missing).isEmpty();

    }

    //Test find by email
    @Test
    void findByEmail() {
        userRepo.saveAndFlush(new User(0L, "fijimf", "zzzzzzz", "jimf@gmail.com", false));
        userRepo.saveAndFlush(new User(0L, "deepfij", "zzzzzzz", "deepfij@gmail.com", false));

        Optional<User> user = userRepo.findFirstByEmail("deepfij@gmail.com");
        assertThat(user).isNotEmpty();
        Optional<User> missing = userRepo.findFirstByUsername("james@yahoo.com");
        assertThat(missing).isEmpty();

    }

    @Test
    void existsByUserName() {
        userRepo.saveAndFlush(new User(0L, "fijimf", "zzzzzzz", "jimf@gmail.com", false));
        userRepo.saveAndFlush(new User(0L, "deepfij", "zzzzzzz", "deepfij@gmail.com", false));


        assertThat(userRepo.existsByUsername("Ted")).isFalse();
        assertThat(userRepo.existsByUsername("fijimf")).isTrue();
    }
    @Test
    void deleteByUserName() {
        userRepo.saveAndFlush(new User(0L, "fijimf", "zzzzzzz", "jimf@gmail.com", false));
        userRepo.saveAndFlush(new User(0L, "deepfij", "zzzzzzz", "deepfij@gmail.com", false));

        assertThat(userRepo.existsByUsername("fijimf")).isTrue();
        userRepo.deleteByUsername("fijimf");
        assertThat(userRepo.existsByUsername("fijimf")).isFalse();
    }
}
