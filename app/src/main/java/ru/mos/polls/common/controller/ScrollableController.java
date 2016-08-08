package ru.mos.polls.common.controller;

import android.widget.AbsListView;

/**
 * Обработка окончания скроллинга списка
 * @since 1.7
 */
public class ScrollableController implements AbsListView.OnScrollListener {
    private boolean isLastItemVisible;
    private AbsListView currentListView;
    /**
     * Поле требуется для предотвращения срабатывания onLastItemVisible() несколько раз,
     * устанавливается вручную, сбрасывается тоже вручную
     */
    private boolean isAllowed;
    private OnLastItemVisibleListener listener;

    public ScrollableController(OnLastItemVisibleListener listener) {
        isAllowed = true;
        if (listener != null) {
            this.listener = listener;
        } else {
            this.listener = OnLastItemVisibleListener.STUB;
        }
    }

    public void setAllowed(boolean isAllowed) {
        this.isAllowed = isAllowed;
    }

    /**
     * метод вызывать после обновления списка,
     * список проскролится, показывая пользователю,
     * что добавлены новые элементы, если элементы не были
     * добавлены, то тогда ничего не произойдет
     */
    public void showNewItems() {
        if (currentListView != null) {
            currentListView.setSmoothScrollbarEnabled(true);
            currentListView.smoothScrollBy(currentListView.getScrollX() + 150, 500);
        }
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE
                && isLastItemVisible) {
            if (isAllowed) {
                currentListView = view;
                listener.onLastItemVisible();
            }
        }
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        isLastItemVisible = (totalItemCount > 0) && (firstVisibleItem + visibleItemCount >= totalItemCount - 1);
    }

    public interface OnLastItemVisibleListener {
        OnLastItemVisibleListener STUB = new OnLastItemVisibleListener() {
            @Override
            public void onLastItemVisible() {
            }
        };

        void onLastItemVisible();
    }

}