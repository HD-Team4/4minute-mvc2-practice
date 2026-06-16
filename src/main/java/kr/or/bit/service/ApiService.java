package kr.or.bit.service;

import kr.or.bit.config.ApiProperties;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class ApiService {
    private static final String DATA_GO_KR_SERVICE_KEY = ApiProperties.getRequired("data.go.kr.service-key");
    private static final String COMPANY_LATITUDE = "37.4947918";
    private static final String COMPANY_LONGITUDE = "127.130095";
    private static final String COMPANY_NX = "62";
    private static final String COMPANY_NY = "125";
    private static final ZoneId KOREA_ZONE = ZoneId.of("Asia/Seoul");
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyyMMdd");
    private static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("HH00");
    private final HttpClient client = HttpClient.newHttpClient();

    public String fetchWeather() {
        WeatherBase weatherBase = resolveCurrentWeatherBase();
        String url = "https://apis.data.go.kr/1360000/VilageFcstInfoService_2.0/getUltraSrtNcst"
                + "?serviceKey=" + DATA_GO_KR_SERVICE_KEY
                + "&pageNo=1"
                + "&numOfRows=100"
                + "&dataType=JSON"
                + "&base_date=" + encode(weatherBase.baseDate)
                + "&base_time=" + encode(weatherBase.baseTime)
                + "&nx=" + COMPANY_NX
                + "&ny=" + COMPANY_NY;
        return get(url);
    }

    public String fetchPension(String pageNo, String numOfRows) {
        String page = pageNo == null || pageNo.isBlank() ? "1" : pageNo.trim();
        String rows = normalizeRows(numOfRows);
        String url = "https://apis.data.go.kr/1160100/service/GetRetirementPensionInfoService/getFundInfo"
                + "?serviceKey=" + DATA_GO_KR_SERVICE_KEY
                + "&pageNo=" + encode(page)
                + "&numOfRows=" + encode(rows)
                + "&resultType=json";
        return get(url);
    }

    private WeatherBase resolveCurrentWeatherBase() {
        LocalDateTime now = LocalDateTime.now(KOREA_ZONE);
        LocalDateTime base = now.getMinute() < 40 ? now.minusHours(1) : now;
        LocalDate date = base.toLocalDate();
        return new WeatherBase(date.format(DATE_FORMAT), base.format(TIME_FORMAT));
    }

    private String normalizeRows(String numOfRows) {
        try {
            int rows = Integer.parseInt(numOfRows);
            if (rows >= 10 && rows <= 100) {
                return String.valueOf(rows);
            }
        } catch (NumberFormatException ignored) {
            return "20";
        }
        return "20";
    }

    private String get(String url) {
        try {
            HttpRequest request = HttpRequest.newBuilder(URI.create(url)).GET().build();
            return client.send(request, HttpResponse.BodyHandlers.ofString()).body();
        } catch (IOException e) {
            throw new IllegalStateException("External API request failed.", e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("External API request interrupted.", e);
        }
    }

    private String encode(String value) {
        return URLEncoder.encode(value, StandardCharsets.UTF_8);
    }

    private static class WeatherBase {
        private final String baseDate;
        private final String baseTime;

        private WeatherBase(String baseDate, String baseTime) {
            this.baseDate = baseDate;
            this.baseTime = baseTime;
        }
    }
}
