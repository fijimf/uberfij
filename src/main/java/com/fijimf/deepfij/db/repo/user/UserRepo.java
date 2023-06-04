package com.fijimf.deepfij.db.repo.user;

import com.fijimf.deepfij.db.model.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepo extends JpaRepository<User, Long> {
   Optional<User> findFirstByUsername(String username);

   boolean existsByUsername(String username);

   void deleteByUsername(String username);
   Optional<User> findFirstByEmail(String email);
}
