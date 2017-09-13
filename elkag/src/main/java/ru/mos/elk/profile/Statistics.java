package ru.mos.elk.profile;

/**
 * Класс стастистики временный
 */

public class Statistics {
    public static final String STATISTICS_PARAMS = "params";
    String title;
    String value;

    public Statistics(String title, String value) {
        this.title = title;
        this.value = value;
    }

    public String getTitle() {
        return title;
    }

    public String getValue() {
        return value;
    }
}