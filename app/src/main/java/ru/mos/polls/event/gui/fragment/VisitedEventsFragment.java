package ru.mos.polls.event.gui.fragment;

import ru.mos.polls.R;
import ru.mos.polls.event.model.Filter;


public class VisitedEventsFragment extends AbstractEventsFragment {
    public static VisitedEventsFragment newInstance() {
        VisitedEventsFragment result = new VisitedEventsFragment();
        return result;
    }

    @Override
    Filter getFilter() {
        return Filter.VISITED;
    }

    @Override
    boolean isPagingEnable() {
        return true;
    }

    @Override
    public String getEmptyText() {
        return getString(R.string.empty_visited_events);
    }
}
