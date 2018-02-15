package ru.mos.polls.quests.ui.view;


import android.graphics.Canvas;
import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

public class HeaderItemDecoration extends RecyclerView.ItemDecoration {

    private View customView;

    public HeaderItemDecoration(View view) {
        this.customView = view;
    }

    @Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
        super.onDraw(c, parent, state);
        customView.layout(parent.getLeft(), 0, parent.getRight(), customView.getMeasuredHeight());
//        for (int i = 0; i < parent.getChildCount(); i++) {
            View view = parent.getChildAt(0);
            if (parent.getChildAdapterPosition(view) == 0) {
                c.save();
                final int height = customView.getMeasuredHeight();
                final int top = view.getTop() - height;
                c.translate(0, top);
                customView.draw(c);
                c.restore();
//                break;
            }
//        }
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        if (parent.getChildAdapterPosition(view) == 0) {
            customView.measure(View.MeasureSpec.makeMeasureSpec(parent.getMeasuredWidth(), View.MeasureSpec.AT_MOST),
                    View.MeasureSpec.makeMeasureSpec(parent.getMeasuredHeight(), View.MeasureSpec.AT_MOST));
            outRect.set(0, customView.getMeasuredHeight(), 0, 0);
        } else {
            outRect.setEmpty();
        }
    }
}
