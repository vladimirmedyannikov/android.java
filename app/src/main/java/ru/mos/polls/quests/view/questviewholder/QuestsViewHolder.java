package ru.mos.polls.quests.view.questviewholder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import ru.mos.polls.R;
import ru.mos.polls.quests.quest.BackQuest;


public abstract class QuestsViewHolder extends RecyclerView.ViewHolder {
    @BindView(R.id.title)
    public TextView questTitle;
    protected View view;
    @BindView(R.id.back)
    public View backView;
    @BindView(R.id.front)
    public View swipableView;
    @BindView(R.id.cancel)
    public TextView cancel;
    @BindView(R.id.delete)
    public TextView delete;

    public QuestsViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
        this.view = itemView;
    }

    public abstract void setDataOnView(BackQuest quest);
}
