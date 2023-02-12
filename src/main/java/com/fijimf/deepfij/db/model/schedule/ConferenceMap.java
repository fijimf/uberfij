package com.fijimf.deepfij.db.model.schedule;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "conference_maps")
public class ConferenceMap {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column(name = "season_id")
    private long seasonId;
    @Column(name = "conference_id")
    private long conferenceId;
    @Column(name = "team_id")
    private long teamId;
    @Column(name = "scrape_src_id")
    private long scrapeSrcId;
    @Column(name = "published_at")
    private LocalDateTime publishedAt;


}
