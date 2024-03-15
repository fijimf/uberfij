package com.fijimf.deepfij.model;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class TeamLineTest {

    @Test
    void performance() {
        TeamLine teamLine = new TeamLine("Georgetown", "georgetown-hoyas", 18,2, 7, 2085,1766);
        assertThat(teamLine.performance()).isEqualTo("18 - 2, W7, PF: 104.3, PA: 88.3");
    }

    @Test
    void update1() {
        TeamLine teamLine = TeamLine.create("Georgetown", "georgetown-hpyas");
        TeamLine t2 = teamLine.update(100, 55);
        assertThat(t2).hasFieldOrPropertyWithValue("wins",1);
        assertThat(t2).hasFieldOrPropertyWithValue("losses",0);
        assertThat(t2).hasFieldOrPropertyWithValue("streak",1);
        assertThat(t2).hasFieldOrPropertyWithValue("pointsFor",100);
        assertThat(t2).hasFieldOrPropertyWithValue("pointsAgainst",55);

    }
    void update2() {
        TeamLine teamLine = TeamLine.create("Georgetown", "georgetown-hpyas");
        TeamLine t2 = teamLine
                .update(100, 55)
                .update(100, 65)
                .update(100, 75)
                .update(100, 85);
        assertThat(t2).hasFieldOrPropertyWithValue("wins",4);
        assertThat(t2).hasFieldOrPropertyWithValue("losses",0);
        assertThat(t2).hasFieldOrPropertyWithValue("streak",4);
        assertThat(t2).hasFieldOrPropertyWithValue("pointsFor",400);
        assertThat(t2).hasFieldOrPropertyWithValue("pointsAgainst",280);

    }
    @Test
    void update3() {
        TeamLine teamLine = TeamLine.create("Georgetown", "georgetown-hpyas");
        TeamLine t2 = teamLine
                .update(100, 55)
                .update(100, 65)
                .update(100, 75)
                .update(100, 85)
                .update(50,100);
        assertThat(t2).hasFieldOrPropertyWithValue("wins",4);
        assertThat(t2).hasFieldOrPropertyWithValue("losses",1);
        assertThat(t2).hasFieldOrPropertyWithValue("streak",-1);
        assertThat(t2).hasFieldOrPropertyWithValue("pointsFor",450);
        assertThat(t2).hasFieldOrPropertyWithValue("pointsAgainst",380);

    }

    @Test
    void create() {
        TeamLine teamLine = TeamLine.create("Georgetown", "georgetown-hpyas");
        assertThat(teamLine).hasFieldOrPropertyWithValue("wins",0);
        assertThat(teamLine).hasFieldOrPropertyWithValue("losses",0);
        assertThat(teamLine).hasFieldOrPropertyWithValue("streak",0);
        assertThat(teamLine).hasFieldOrPropertyWithValue("pointsFor",0);
        assertThat(teamLine).hasFieldOrPropertyWithValue("pointsAgainst",0);
    }
}