package com.fijimf.deepfij.model;

import com.fijimf.deepfij.db.model.schedule.Team;

public class SimpleTeam {

    private final Long id;
    private final String key;
    private final String name;

    private final String conference;
    private final String nickname;
    private final String color;
    private final String logo;

    public SimpleTeam(Long id, String key, String name, String conference, String nickname, String color, String logo) {
        this.id = id;
        this.key = key;
        this.name = name;
        this.conference = conference;
        this.nickname = nickname;
        this.color = color;
        this.logo = logo;
    }

    public SimpleTeam(Team team, String conference) {
        this(team.getId(), team.getKey(), team.getName(), conference, team.getNickname(),team.getColor(), team.getLogoUrl());
    }

    public Long getId() {
        return id;
    }

    public String getKey() {
        return key;
    }

    public String getName() {
        return name;
    }

    public String getNickname() {
        return nickname;
    }

    public String getColor() {
        return color;
    }

    public String getLogo() {
        return logo;
    }

    public String getConference() {
        return conference;
    }
}
