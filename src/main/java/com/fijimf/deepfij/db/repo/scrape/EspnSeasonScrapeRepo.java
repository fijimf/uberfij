package com.fijimf.deepfij.db.repo.scrape;

import com.fijimf.deepfij.db.model.scrape.EspnSeasonScrape;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EspnSeasonScrapeRepo extends JpaRepository<EspnSeasonScrape, Long> {
}
