package ru.mos.polls.survey.filter;

import ru.mos.polls.survey.Survey;

/**
 * Фильтр ничего не делает и всегда подходит.
 * Нужен на случай когда у вопроса нет фильтра.
 */
public class EmptyFilter extends Filter {

    public EmptyFilter(long id) {
        super(id);
    }

    @Override
    public boolean isSuitable(Survey survey) {
        return true;
    }

}
