package com.fijimf.deepfij.scraping;

import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static java.time.temporal.ChronoUnit.SECONDS;

@Component
public class ScoreboardHttpClient {
    public static final String ODDS_FLAVOR = "ODDS";
    private final static String ODDS_URL_FORMAT = "https://site.web.api.espn.com/apis/v2/scoreboard/header?sport=basketball&league=mens-college-basketball&limit=200&groups=50&dates=%s";
    private final static int TIMEOUT_SECONDS = 10;
    private final static HttpClient client = HttpClient.newBuilder().build();

    public HttpResponse<byte[]> retrieveScoreboardData(LocalDate date, String flavor) throws URISyntaxException, IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI(getUrl(date,flavor)))
                .timeout(Duration.of(TIMEOUT_SECONDS, SECONDS))
                .GET()
                .build();
        return client.send(request, HttpResponse.BodyHandlers.ofByteArray());
    }

    public String getUrl(LocalDate date, String flavor) {
        if (flavor.equals(ODDS_FLAVOR)) {
            return String.format(ODDS_URL_FORMAT, date.format(DateTimeFormatter.ofPattern("yyyyMMdd")));
        } else {
            throw new RuntimeException("Unknown flavor of scoreboardScrape");
        }
    }
}
