package com.fijimf.deepfij.db.repo.schedule;

import com.fijimf.deepfij.db.model.schedule.Conference;
import com.fijimf.deepfij.db.model.schedule.Season;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SeasonRepo extends JpaRepository<Season, Long> {
}
