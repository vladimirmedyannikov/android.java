package ru.mos.polls.survey.filter.conditions;

/**
 * Условие "ИЛИ".
 * Истино если совпал хотя бы один из ответов.
 */
public class OrCondition extends Condition {

    @Override
    public boolean get() {
        boolean result = false;
        for (Boolean value : getValues()) {
            if (value.booleanValue()) {
                result = true;
                break;
            }
        }
        return result; //возвращает true если хотя бы одно значение true
    }

    @Override
    public String toString() {
        return "OrCondition";
    }

}
