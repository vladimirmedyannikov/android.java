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

    @Override
    public boolean equals(Object o) {
        if (o instanceof Statistics) {
            return ((Statistics) o).getTitle().equals(getTitle()) && ((Statistics) o).getValue().equals(getValue());
        } else {
            return super.equals(o);
        }
    }
}