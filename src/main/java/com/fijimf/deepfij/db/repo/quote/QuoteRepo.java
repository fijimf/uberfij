package com.fijimf.deepfij.db.repo.quote;

import com.fijimf.deepfij.db.model.quote.Quote;
import com.fijimf.deepfij.db.model.schedule.Game;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
@Transactional
public interface QuoteRepo extends JpaRepository<Quote, Long> {
    @Query(nativeQuery = true, value = "select * from quote where tag = :tag order by random() limit 1")
    Optional<Quote> getRandomQuote(@Param("tag") String tag);

    @Query(nativeQuery = true, value = "select * from quote order by random() limit 1")
    Optional<Quote> getRandomQuote();
}
