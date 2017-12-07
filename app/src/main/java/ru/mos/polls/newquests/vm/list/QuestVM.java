package ru.mos.polls.newquests.vm.list;

import android.databinding.ViewDataBinding;
import android.view.View;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import ru.mos.polls.R;
import ru.mos.polls.base.RecyclerBaseViewModel;
import ru.mos.polls.newquests.model.quest.Quest;

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
        ButterKnife.bind(this, viewDataBinding.getRoot());
        this.view = viewDataBinding.getRoot();
    }

    public QuestVM(M model) {
        super(model);
    }
}
