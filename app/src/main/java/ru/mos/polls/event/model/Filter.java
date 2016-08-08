package ru.mos.polls.event.model;

/**
 * Структура для хранения типа фильтра по мероприятиям
 */
public enum Filter {
    CURRENT("current"),
    VISITED("visited"),
    PAST("past");

    private String filter;

    Filter(String filter) {
        this.filter = filter;
    }

    public static Filter fromFilter(String filter) {
        Filter result = null;
        if (VISITED.toString().equalsIgnoreCase(filter)) {
            result = VISITED;
        } else if (CURRENT.toString().equalsIgnoreCase(filter)) {
            result = CURRENT;
        } else if (PAST.toString().equalsIgnoreCase(filter)) {
            result = PAST;
        }
        return result;
    }

    @Override
    public String toString() {
        return filter;
    }
}
