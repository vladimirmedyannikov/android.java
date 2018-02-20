package ru.mos.polls.innovations.oldmodel;

/**
 * Статус городской новинки
 * @since 1.9
 */
@Deprecated
public enum Status {
    PASSED("passed"),
    ACTIVE("active"),
    OLD("old");

    private String status;

    Status(String status) {
        this.status = status;
    }

    public static Status parse(String parse) {
        Status result = null;
        if (PASSED.toString().equalsIgnoreCase(parse)) {
            result = PASSED;
        } else if (ACTIVE.toString().equalsIgnoreCase(parse)) {
            result = ACTIVE;
        } else if (OLD.toString().equalsIgnoreCase(parse)) {
            result = OLD;
        }
        return result;
    }

    @Override
    public String toString() {
        return status;
    }
}
