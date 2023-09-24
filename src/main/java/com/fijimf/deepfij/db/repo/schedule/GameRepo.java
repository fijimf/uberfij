package com.fijimf.deepfij.db.repo.schedule;

import com.fijimf.deepfij.db.model.schedule.Game;
import org.springframework.data.jpa.repository.JpaRepository;
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
}
