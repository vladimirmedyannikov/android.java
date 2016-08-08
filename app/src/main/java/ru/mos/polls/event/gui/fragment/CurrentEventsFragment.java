package ru.mos.polls.event.gui.fragment;

import ru.mos.polls.R;
import ru.mos.polls.event.model.Filter;


public class CurrentEventsFragment extends AbstractEventsFragment {
    public static CurrentEventsFragment newInstance() {
        CurrentEventsFragment result = new CurrentEventsFragment();
        return result;
    }

    @Override
    Filter getFilter() {
        return Filter.CURRENT;
    }

    @Override
    boolean isPagingEnable() {
        return false;
    }

    @Override
    public String getEmptyText() {
        return getString(R.string.empty_current_events);
    }
}
