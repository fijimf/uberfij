package com.fijimf.deepfij.db.model.schedule;

import jakarta.persistence.*;

@Entity
@Table(name = "season")
public class Season {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column(unique = true)
    private int season;

    public Season() {
    }

    public Season(long id, int season) {
        this.id = id;
        this.season = season;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getSeason() {
        return season;
    }

    public void setSeason(int season) {
        this.season = season;
    }
}
