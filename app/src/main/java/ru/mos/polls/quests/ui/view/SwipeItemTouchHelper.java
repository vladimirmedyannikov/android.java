package ru.mos.polls.quests.ui.view;


import android.graphics.Canvas;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;

import ru.mos.polls.R;
import ru.mos.polls.quests.adapter.QuestsItemAdapter;

public class SwipeItemTouchHelper extends ItemTouchHelper.Callback {
    private QuestsItemAdapter itemAdapter;

    public SwipeItemTouchHelper(QuestsItemAdapter itemAdapter) {
        this.itemAdapter = itemAdapter;
    }

    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
        return false;
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
        itemAdapter.redrawViewHolder(viewHolder.getAdapterPosition());
    }

    @Override
    public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        View swipableView = viewHolder.itemView.findViewById(R.id.front);
        if (swipableView.getVisibility() == View.INVISIBLE) return 0;
        return makeMovementFlags(0, ItemTouchHelper.START | ItemTouchHelper.END);
    }

    @Override
    public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
        if (viewHolder.getAdapterPosition() == -1) {
            return;
        }
        if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
            View swipableView = viewHolder.itemView.findViewById(R.id.front);
            getDefaultUIUtil().onDraw(c, recyclerView, swipableView, dX, dY, actionState, isCurrentlyActive);
        }
    }

    @Override
    public void clearView(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        super.clearView(recyclerView, viewHolder);
        View swipableView = viewHolder.itemView.findViewById(R.id.front);
        getDefaultUIUtil().clearView(swipableView);
    }
}
