package com.fijimf.deepfij.db.repo.scrape;

import com.fijimf.deepfij.db.model.scrape.EspnScoreboardScrape;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EspnScoreboardScrapeRepo extends JpaRepository<EspnScoreboardScrape, Long> {
}
