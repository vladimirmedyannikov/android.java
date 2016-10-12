package ru.mos.polls.quests.view.questviewholder;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import butterknife.ButterKnife;
import ru.mos.polls.R;
import ru.mos.polls.common.controller.UrlSchemeController;
import ru.mos.polls.quests.QuestsFragment;
import ru.mos.polls.quests.quest.BackQuest;


public abstract class QuestsViewHolder extends RecyclerView.ViewHolder {
    protected TextView questTitle;
    protected View view;
    public View swipableView;
    public TextView cancel;
    public TextView delete;
    protected String urlScheme;
    public QuestsViewHolder(View itemView) {
        super(itemView);
        this.view = itemView;
        questTitle = ButterKnife.findById(itemView, R.id.title);
        swipableView = ButterKnife.findById(itemView, R.id.front);
        cancel = ButterKnife.findById(itemView, R.id.cancel);
        delete = ButterKnife.findById(itemView, R.id.delete);
    }

    public abstract void setDataOnView(BackQuest quest);

    public void onClick(Context context, QuestsFragment.Listener listener) {
        UrlSchemeController.start(context, urlScheme);
    }
}
