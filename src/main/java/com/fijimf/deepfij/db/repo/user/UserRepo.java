package com.fijimf.deepfij.db.repo.user;

import com.fijimf.deepfij.db.model.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface UserRepo extends JpaRepository<User, Long> {
    Optional<User> findFirstByUsername(String username);

    boolean existsByUsername(String username);

    void deleteByUsername(String username);

    Optional<User> findFirstByEmail(String email);


    @Transactional
    @Modifying
    @Query("UPDATE User u SET u.locked=true where u.id=:id")
    void lock(Long id);

    @Transactional
    @Modifying
    @Query("UPDATE User u SET u.locked=false where u.id=:id")
    void unlock(Long id);

    @Transactional
    @Modifying
    @Query("UPDATE User u SET u.expireCredentialsAt=null where u.id=:id")
    void persistCreds(Long id);

    @Override
    void deleteById(Long id);

    @Transactional
    @Modifying
    @Query("UPDATE User u SET u.activated=true where u.id=:id")
    void activate(Long id);

    @Transactional
    @Modifying
    @Query("UPDATE User u SET u.expireCredentialsAt=:expireAt where u.id=:id")
    void expireCreds(Long id, LocalDateTime expireAt);
}
