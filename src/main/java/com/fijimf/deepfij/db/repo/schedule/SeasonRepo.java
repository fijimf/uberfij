package com.fijimf.deepfij.db.repo.schedule;

import com.fijimf.deepfij.db.model.schedule.Season;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SeasonRepo extends JpaRepository<Season, Long> {
    Optional<Season> findBySeason(int season);
    Optional<Season> findFirstByOrderBySeasonDesc();
}
