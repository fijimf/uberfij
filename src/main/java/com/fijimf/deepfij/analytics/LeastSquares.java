package com.fijimf.deepfij.analytics;

import com.fijimf.deepfij.db.model.schedule.Season;
import com.fijimf.deepfij.db.model.statistic.DailyTeamStatistic;
import com.fijimf.deepfij.db.model.statistic.DailyTeamStatisticSummary;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;

public class LeastSquares {

    private static final Logger logger = LoggerFactory.getLogger(LeastSquares.class);

    public static SortedMap<LocalDate, Map<DailyTeamStatisticSummary, List<DailyTeamStatistic>>> calculate(Season season) {
        return Collections.emptySortedMap();
    }
}
