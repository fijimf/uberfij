package com.fijimf.deepfij.util.ui;

public class NavItem {
    private final String display;
    private final String link;
    private final boolean isActive;

    private final String bootstrapIcon;

    public NavItem(String display, String link, boolean isActive, String bootstrapIcon) {
        this.display = display;
        this.link = link;
        this.isActive = isActive;
        this.bootstrapIcon = bootstrapIcon;
    }

    public String getDisplay() {
        return display;
    }

    public String getLink() {
        return link;
    }

    public boolean isActive() {
        return isActive;
    }

    public String getBootstrapIcon() {
        return bootstrapIcon;
    }

    public static NavItem[] NAVBAR_HOME = {
            new NavItem("Home", "#", true, null),
            new NavItem("Data", "#", false, null),
            new NavItem("Stats", "#", false, null),
            new NavItem("Analysis", "#", false, null),
            new NavItem("Admin", "#", false, null),
    };
    public static NavItem[] NAVBAR_DATA = {
            new NavItem("Home", "#", false, null),
            new NavItem("Data", "#", true, null),
            new NavItem("Stats", "#", false, null),
            new NavItem("Analysis", "#", false, null),
            new NavItem("Admin", "#", false, null),
    };
    public static NavItem[] NAVBAR_STATS = {
            new NavItem("Home", "#", false, null),
            new NavItem("Data", "#", false, null),
            new NavItem("Stats", "#", true, null),
            new NavItem("Analysis", "#", false, null),
            new NavItem("Admin", "#", false, null),
    };
    public static NavItem[] NAVBAR_ANALYSIS = {
            new NavItem("Home", "#", false, null),
            new NavItem("Data", "#", false, null),
            new NavItem("Stats", "#", false, null),
            new NavItem("Analysis", "#", true, null),
            new NavItem("Admin", "#", false, null),
    };
    public static NavItem[] NAVBAR_ADMIN = {
            new NavItem("Home", "#", false, null),
            new NavItem("Data", "#", false, null),
            new NavItem("Stats", "#", false, null),
            new NavItem("Analysis", "#", false, null),
            new NavItem("Admin", "#", true, null),
    };

    public static NavItem[] SIDENAV_ADMIN_EDIT = {
            new NavItem("Users", null, false, "bi-people"),
            new NavItem("Edit", "#", true, null),
            new NavItem("Scrape", null, false, null),
            new NavItem("Teams", "#", false, null),
            new NavItem("Conferences", "#", false, null),
            new NavItem("Seasons", "#", false, null),
    };
    public static NavItem[] SIDENAV_ADMIN_TEAMS = {
            new NavItem("Users", null, false, null),
            new NavItem("Edit", "#", false, null),
            new NavItem("Scrape", null, false, null),
            new NavItem("Teams", "#", true, null),
            new NavItem("Conferences", "#", false, null),
            new NavItem("Seasons", "#", false, null),
    };
    public static NavItem[] SIDENAV_ADMIN_CONFS = {
            new NavItem("Users", null, false, null),
            new NavItem("Edit", "#", false, null),
            new NavItem("Scrape", null, false, null),
            new NavItem("Teams", "#", false, null),
            new NavItem("Conferences", "#", true, null),
            new NavItem("Seasons", "#", true, null),
    };
    public static NavItem[] SIDENAV_ADMIN_SEASONS = {
            new NavItem("Users", null, false, null),
            new NavItem("Edit", "#", false, null),
            new NavItem("Scrape", null, false, null),
            new NavItem("Teams", "#", false, null),
            new NavItem("Conferences", "#", false, null),
            new NavItem("Seasons", "#", true, null),
    };
}
