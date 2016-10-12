package ru.mos.polls.quests;


import android.graphics.Canvas;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;

import ru.mos.polls.quests.view.questviewholder.QuestsViewHolder;

public class SwipeItemTouchHelper extends ItemTouchHelper.Callback {
    private QuestsItemAdapter itemAdapter;
    private RecyclerView recyclerView;

    public SwipeItemTouchHelper(RecyclerView mRecyclerView, QuestsItemAdapter itemAdapter) {
        this.itemAdapter = itemAdapter;
        this.recyclerView = mRecyclerView;
    }

    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
        return false;
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {

    }

    @Override
    public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        return makeMovementFlags(0, ItemTouchHelper.START | ItemTouchHelper.END);
    }

    @Override
    public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
        if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
            QuestsViewHolder holder = (QuestsViewHolder) viewHolder;
            holder.swipableView.setTranslationX(dX);
        }
    }
}
