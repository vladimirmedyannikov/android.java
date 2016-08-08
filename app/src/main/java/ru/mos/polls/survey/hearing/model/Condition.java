package ru.mos.polls.survey.hearing.model;

/**
 * Описывает особые условия проведения публичного слушания
 *
 * @since 2.0
 */
public enum Condition {
    MANDATORY_REGISTRATION("mandatory_registration", "Вход на собрание строго по записи"),
    PRIORITY_REGISTRATION("priority_registration", "Приоритетный вход по записи"),
    WITHOUT_REGISTRATION("without_registration", "Вход свободный");

    public String value, label;

    Condition(String value, String label) {
        this.value = value;
        this.label = label;
    }

    public static Condition parse(String value) {
        Condition result = WITHOUT_REGISTRATION;
        if (MANDATORY_REGISTRATION.value.equalsIgnoreCase(value)) {
            result = MANDATORY_REGISTRATION;
        } else if (PRIORITY_REGISTRATION.value.equalsIgnoreCase(value)) {
            result = PRIORITY_REGISTRATION;
        }
        return result;
    }

    public boolean isMandatoryRegistration() {
        return MANDATORY_REGISTRATION == this;
    }

    public boolean isWithoutRegistration() {
        return WITHOUT_REGISTRATION == this;
    }

    public boolean isPriorityRegistration() {
        return PRIORITY_REGISTRATION == this;
    }

    public String getLabel() {
        return label;
    }
}
