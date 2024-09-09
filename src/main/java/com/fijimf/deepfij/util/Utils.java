package com.fijimf.deepfij.util;

import java.time.LocalDate;
import java.util.stream.Stream;

public class Utils {
    public static Stream<LocalDate> dateStream(LocalDate start, LocalDate end) {
        return Stream.iterate(start, d -> d.isBefore(end), d -> d.plusDays(1));
    }
}