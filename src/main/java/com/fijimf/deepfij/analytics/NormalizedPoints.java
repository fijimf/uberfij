package com.fijimf.deepfij.analytics;

import com.fijimf.deepfij.db.model.schedule.Game;
import com.fijimf.deepfij.db.model.schedule.Season;
import com.fijimf.deepfij.db.model.schedule.Team;
import com.fijimf.deepfij.db.model.statistic.DailyTeamStatistic;
import com.fijimf.deepfij.db.model.statistic.DailyTeamStatisticSummary;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class NormalizedPoints {

    private static final Logger logger = LoggerFactory.getLogger(NormalizedPoints.class);
    public static SortedMap<LocalDate, Map<DailyTeamStatisticSummary, List<DailyTeamStatistic>>> calculate(Season season) {
        return Collections.emptySortedMap();
    }

}
