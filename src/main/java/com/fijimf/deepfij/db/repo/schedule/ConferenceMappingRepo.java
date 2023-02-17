package com.fijimf.deepfij.db.repo.schedule;

import com.fijimf.deepfij.db.model.schedule.ConferenceMap;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ConferenceMappingRepo extends JpaRepository<ConferenceMap, Long> {
    Optional<ConferenceMap> findBySeasonIdAndTeamId(Long seasonId, Long teamId);

    void deleteBySeasonIdAndScrapeSrcIdNot(Long seasonId, Long srcScrapeId);

}

