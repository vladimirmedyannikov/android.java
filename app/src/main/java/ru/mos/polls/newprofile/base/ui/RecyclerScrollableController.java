package ru.mos.polls.newprofile.base.ui;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

/**
 * Инкапсуляция логики проверки достижения конца списка {@link RecyclerView}
 *
 * @since 1.0
 */
public class RecyclerScrollableController extends RecyclerView.OnScrollListener {
    public static final int VISIBLE_THRESHOLD = 1;

    private int previousTotal = 0;
    private boolean loading = true;

    private OnLastItemVisibleListener listener;

    public RecyclerScrollableController(OnLastItemVisibleListener listener) {
        if (listener == null) {
            listener = OnLastItemVisibleListener.STUB;
        }
        this.listener = listener;
    }

    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);
        if (dy > 0) {
            int visibleItemCount = recyclerView.getChildCount();
            LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
            int totalItemCount = layoutManager.getItemCount();
            int firstVisibleItem = layoutManager.findFirstVisibleItemPosition();
            if (loading) {
                if (totalItemCount > previousTotal) {
                    loading = false;
                    previousTotal = totalItemCount;
                }
            }
            boolean isLastItemVisible = totalItemCount - visibleItemCount <= firstVisibleItem + VISIBLE_THRESHOLD;
            if (isLastItemVisible) {
                if (listener != null) {
                    listener.onLastItemVisible();
                }
                loading = true;
            }
        }
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
