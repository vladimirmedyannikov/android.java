package ru.mos.polls.quests.vm.list;

import android.content.Intent;
import android.databinding.ViewDataBinding;
import android.support.v4.content.LocalBroadcastManager;
import android.view.View;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import ru.mos.polls.R;
import ru.mos.polls.base.RecyclerBaseViewModel;
import ru.mos.polls.quests.model.quest.BackQuest;
import ru.mos.polls.quests.model.quest.Quest;
import ru.mos.polls.quests.vm.QuestsFragmentVM;

/**
 * @author matek3022 (semenovmm@altarix.ru)
 *         on 07.12.17.
 */

public abstract class QuestVM<M extends Quest, VDB extends ViewDataBinding> extends RecyclerBaseViewModel<M, VDB> {
    @BindView(R.id.title)
    protected TextView questTitle;
    protected View view;
    @BindView(R.id.back)
    protected View backView;
    @BindView(R.id.front)
    protected View swipableView;
    @BindView(R.id.cancel)
    protected TextView cancel;
    @BindView(R.id.delete)
    protected TextView delete;

    public QuestVM(M model, VDB viewDataBinding) {
        super(model, viewDataBinding);
    }

    public QuestVM(M model) {
        super(model);
    }

    @Override
    public void onBind(VDB viewDataBinding) {
        super.onBind(viewDataBinding);
        ButterKnife.bind(this, viewDataBinding.getRoot());
        this.view = viewDataBinding.getRoot();
        if (((BackQuest)model).isSwiped()) {
            view.setOnClickListener(null);
            swipableView.setVisibility(View.INVISIBLE);
            backView.setVisibility(View.VISIBLE);
            cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(QuestsFragmentVM.ACTION_CANCEL_CLICK);
                    intent.putExtra(QuestsFragmentVM.ARG_QUEST,(BackQuest) model);
                    LocalBroadcastManager.getInstance(viewDataBinding.getRoot().getContext()).sendBroadcast(intent);
                }
            });
            delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(QuestsFragmentVM.ACTION_DELETE_CLICK);
                    intent.putExtra(QuestsFragmentVM.ARG_QUEST,(BackQuest) model);
                    LocalBroadcastManager.getInstance(viewDataBinding.getRoot().getContext()).sendBroadcast(intent);
                }
            });
        } else {
            swipableView.setVisibility(View.VISIBLE);
            cancel.setOnClickListener(null);
            delete.setOnClickListener(null);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(QuestsFragmentVM.ACTION_JUST_CLICK);
                    intent.putExtra(QuestsFragmentVM.ARG_QUEST,(BackQuest) model);
                    LocalBroadcastManager.getInstance(viewDataBinding.getRoot().getContext()).sendBroadcast(intent);
                }
            });
            if (swipableView != null) {
                swipableView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(QuestsFragmentVM.ACTION_JUST_CLICK);
                        intent.putExtra(QuestsFragmentVM.ARG_QUEST,(BackQuest) model);
                        LocalBroadcastManager.getInstance(viewDataBinding.getRoot().getContext()).sendBroadcast(intent);
                    }
                });
            }
        }
    }
}
