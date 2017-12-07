package ru.mos.polls.infosurvey.vm;

import ru.mos.polls.base.component.UIComponentFragmentViewModel;
import ru.mos.polls.base.component.UIComponentHolder;
import ru.mos.polls.databinding.FragmentInfoCommentBinding;
import ru.mos.polls.infosurvey.ui.InfoCommentFragment;

/**
 * Created by Trunks on 06.12.2017.
 */

public class InfoCommentFragmentVM extends UIComponentFragmentViewModel<InfoCommentFragment, FragmentInfoCommentBinding> {
    public InfoCommentFragmentVM(InfoCommentFragment fragment, FragmentInfoCommentBinding binding) {
        super(fragment, binding);
    }

    @Override
    protected void initialize(FragmentInfoCommentBinding binding) {

    }

    @Override
    protected UIComponentHolder createComponentHolder() {
        return null;
    }


    public boolean isDataChanged() {
        return true;
    }

    public void confirmAction() {

    }
}
