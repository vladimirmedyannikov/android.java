package ru.mos.polls.survey.filter.conditions;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Условие для фильтра
 */
public abstract class Condition implements Serializable {

    public List<Boolean> values = new ArrayList<Boolean>();

    /**
     * Добавляет значение к услвоия.
     *
     * @param value
     */
    public void add(boolean value) {
        values.add(value);
    }

    protected List<Boolean> getValues() {
        return values;
    }

    /**
     * Возвращает значение булевой опации над ранее добавленными условиями.
     *
     * @return
     */
    public abstract boolean get();

    public void reset() {
        values.clear();
    }
}
