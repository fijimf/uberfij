package com.fijimf.deepfij.db.repo.scrape;

import com.fijimf.deepfij.db.model.scrape.EspnSeasonScrape;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface EspnSeasonScrapeRepo extends JpaRepository<EspnSeasonScrape, Long> {

    List<EspnSeasonScrape> findAllBySeasonOrderByStartedAt(int season);

    List<EspnSeasonScrape> findByCompletedAtIsNull();

    List<EspnSeasonScrape> findByCompletedAtIsNotNullAndStatus(String status);


    @Transactional
    @Modifying(clearAutomatically = true)
    @Query(value = "update EspnSeasonScrape s set s.status = :status where s.id = :id and s.completedAt is null")
    int updateStatusById(@Param("id") long id, @Param("status") String status);
}
