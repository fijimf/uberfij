package com.fijimf.deepfij.scraping;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fijimf.deepfij.db.model.schedule.Conference;
import org.apache.commons.lang3.StringUtils;

import java.time.LocalDateTime;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ConferencesConference {
    private String name;
    private String shortName;
    private String groupId;
    private String logo;
    private String parentGroupId;

    @JsonIgnore
    public Conference getConference() {
        return getConference(0L);
    }

    public Conference getConference(long scrapeId) {
        return new Conference(0L, slug(), name, shortName, logo, groupId, scrapeId, LocalDateTime.now());
    }

    private String slug() {
        return StringUtils.isBlank(shortName) ? null : shortName
                .trim()
                .replace(' ', '-')
                .toLowerCase()
                .replaceAll("[^\\w\\-]", "");
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public String getParentGroupId() {
        return parentGroupId;
    }

    public void setParentGroupId(String parentGroupId) {
        this.parentGroupId = parentGroupId;
    }
}
