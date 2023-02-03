package com.fijimf.deepfij.db.repo.schedule;

import com.fijimf.deepfij.db.model.schedule.Conference;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ConferenceRepo extends JpaRepository<Conference, Long> {
    Optional<Conference> findByEspnIdEquals(String espnId);
}
