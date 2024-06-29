package com.fijimf.deepfij.db.repo.schedule;

import com.fijimf.deepfij.db.model.schedule.Game;
import com.fijimf.deepfij.db.model.schedule.Team;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

@Repository
@Transactional
public interface GameRepo extends JpaRepository<Game, Long> {
    List<Game> findAllByScoreboardKey(LocalDate scoreboardKey);

    int deleteAllByEspnIdIn(Collection<String> espnIds);

    @Query(nativeQuery = true, value = "select g.* from games g left join season s on g.season_id=s.id where s.season=:year and (g.home_team_id = :teamId or g.away_team_id = :teamId) order by g.date")

    List<Game> findAllByTeamSeason(long teamId, int year);
}
