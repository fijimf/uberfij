package com.fijimf.deepfij.db.repo.scrape;

import com.fijimf.deepfij.db.model.scrape.EspnConferencesScrape;
import com.fijimf.deepfij.db.model.scrape.EspnTeamsScrape;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EspnTeamsScrapeRepo extends JpaRepository<EspnTeamsScrape, Long> {
}
