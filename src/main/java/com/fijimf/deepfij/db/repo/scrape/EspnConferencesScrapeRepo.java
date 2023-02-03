package com.fijimf.deepfij.db.repo.scrape;

import com.fijimf.deepfij.db.model.schedule.Team;
import com.fijimf.deepfij.db.model.scrape.EspnConferencesScrape;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EspnConferencesScrapeRepo  extends JpaRepository<EspnConferencesScrape, Long> {
}
