package com.fijimf.deepfij.db.repo.scrape;

import com.fijimf.deepfij.db.model.scrape.EspnSeasonScrape;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EspnSeasonScrapeRepo extends JpaRepository<EspnSeasonScrape, Long> {

    List<EspnSeasonScrape> findAllBySeason(int season);
}
