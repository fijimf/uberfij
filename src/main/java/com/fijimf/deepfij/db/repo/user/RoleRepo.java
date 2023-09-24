package com.fijimf.deepfij.db.repo.user;

import com.fijimf.deepfij.db.model.user.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepo extends JpaRepository<Role, Long> {

    Optional<Role> findFirstByName(String user);
}
