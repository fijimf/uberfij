package com.fijimf.deepfij.model;

import com.fijimf.deepfij.db.model.schedule.Game;

public record GameLine(Game game, TeamLine home, TeamLine away) {
    public Double getSpreadErr(){
        if (game.isComplete() && game.getSpread()!=null){
            return (game.getAwayScore()-game.getHomeScore()-game.getSpread());
        } else {
            return null;
        }
    }

    public Double getOUErr(){
        if (game.isComplete() && game.getSpread()!=null){
            return (game.getHomeScore()+game.getAwayScore())-game.getOverUnder();
        } else {
            return null;
        }
    }
}

