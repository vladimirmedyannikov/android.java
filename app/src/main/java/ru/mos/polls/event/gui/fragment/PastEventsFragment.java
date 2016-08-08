package ru.mos.polls.event.gui.fragment;

import ru.mos.polls.event.model.Filter;


public class PastEventsFragment extends AbstractEventsFragment {
    public static PastEventsFragment newInstance() {
        PastEventsFragment result = new PastEventsFragment();
        return result;
    }

    @Override
    Filter getFilter() {
        return Filter.PAST;
    }

    @Override
    boolean isPagingEnable() {
        return true;
    }

}
