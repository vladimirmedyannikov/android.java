package ru.mos.polls.innovations.vm.item;

import ru.mos.polls.R;
import ru.mos.polls.databinding.ItemInnovationOldBinding;
import ru.mos.polls.innovations.model.Innovation;
import ru.mos.polls.innovations.ui.adapter.InnovationsAdapter;

public class InnovationsItemOldVM extends InnovationsItemBaseVM<Innovation, ItemInnovationOldBinding> {
    public InnovationsItemOldVM(Innovation model) {
        super(model);
    }

    @Override
    public int getLayoutId() {
        return R.layout.item_innovation_old;
    }

    @Override
    public int getViewType() {
        return InnovationsAdapter.Type.ITEM_OLD;
    }

    @Override
    public void onBind(ItemInnovationOldBinding viewDataBinding) {
        super.onBind(viewDataBinding);
        viewDataBinding.innEndedDate.setText(UIInnovationViewHelper.getInnEndedDateTxt(getModel(), viewDataBinding.innEndedDate.getContext()));
        viewDataBinding.title.setText(getModel().getTitle());
    }
}
