package ru.mos.polls.common.controller;

import android.widget.AbsListView;

/**
 * Расширяет контроллер {@link ScrollableController},
 * добавляя callback на событие прокрутки скписка {@link android.widget.ListView}
 *
 * @since 1.9.4
 */
public class ExtendScrollableController extends ScrollableController {
    private ScrollListener scrollListener;

    public ExtendScrollableController(OnLastItemVisibleListener listener, ScrollListener scrollListener) {
        super(listener);
        this.scrollListener = scrollListener;
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        super.onScroll(view, firstVisibleItem, visibleItemCount, totalItemCount);
        if (scrollListener != null) {
            scrollListener.onScrolled(view,firstVisibleItem,visibleItemCount, totalItemCount);
        }
    }

    public interface ScrollListener {
        void onScrolled(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount);
    }
}
