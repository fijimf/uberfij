package com.fijimf.deepfij.db.repo.scrape;

import com.fijimf.deepfij.db.model.scrape.EspnStandingsScrape;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EspnStandingsScrapeRepo extends JpaRepository<EspnStandingsScrape, Long> {
    List<EspnStandingsScrape> findAllBySeason(int year);
}
