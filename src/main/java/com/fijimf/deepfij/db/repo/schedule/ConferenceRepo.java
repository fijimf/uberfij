package com.fijimf.deepfij.db.repo.schedule;

import com.fijimf.deepfij.db.model.schedule.Conference;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ConferenceRepo extends JpaRepository<Conference, Long> {
    Optional<Conference> findByEspnIdEquals(String espnId);

    void deleteByScrapeSrcIdNot(long scrapeSrcId);

    @Query(nativeQuery = true, value = "select c.* from conference c " +
            "left join conference_maps cm on c.id=cm.conference_id " +
            "left join season s on s.id = cm.season_id " +
            "where s.season = :year and cm.team_id = :teamId")

    Optional<Conference> findConferenceForTeamYear(long teamId, int year);
}
