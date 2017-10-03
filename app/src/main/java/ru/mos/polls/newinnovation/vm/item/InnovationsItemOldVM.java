package ru.mos.polls.newinnovation.vm.item;

import ru.mos.polls.R;
import ru.mos.polls.databinding.ItemInnovationOldBinding;
import ru.mos.polls.newinnovation.model.Innovation;
import ru.mos.polls.newinnovation.ui.adapter.InnovationAdapter;

/**
 * Created by Trunks on 03.10.2017.
 */

public class InnovationsItemOldVM extends InnovationsItemBaseVM<Innovation, ItemInnovationOldBinding> {
    public InnovationsItemOldVM(Innovation model) {
        super(model);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.item_innovation_old;
    }

    @Override
    public int getViewType() {
        return InnovationAdapter.Type.ITEM_OLD;
    }

    @Override
    public void onBind(ItemInnovationOldBinding viewDataBinding) {
        super.onBind(viewDataBinding);
        viewDataBinding.innEndedDate.setText(UIInnovationViewHelper.getInnEndedDateTxt(getModel(), viewDataBinding.innEndedDate.getContext()));
        viewDataBinding.title.setText(getModel().getTitle());
    }
}
