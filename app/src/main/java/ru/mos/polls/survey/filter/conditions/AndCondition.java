package ru.mos.polls.survey.filter.conditions;

/**
 * Условие "И".
 * Истино если совпали хотя все ответовы.
 */
public class AndCondition extends Condition {

    @Override
    public boolean get() {
        boolean result = true;
        for (Boolean value : getValues()) {
            if (!value.booleanValue()) {
                result = false;
                break;
            }
        }
        return result; //возвращает true только если все значения true
    }

    @Override
    public String toString() {
        return "AndCondition";
    }

}
