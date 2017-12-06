package ru.mos.polls.infosurvey.vm;

import ru.mos.polls.base.component.ProgressableUIComponent;
import ru.mos.polls.base.component.UIComponentFragmentViewModel;
import ru.mos.polls.base.component.UIComponentHolder;
import ru.mos.polls.databinding.FragmentInfoSurveyBinding;
import ru.mos.polls.infosurvey.ui.InfoSurveyFragment;

/**
 * Created by Trunks on 06.12.2017.
 */

public class InfoSurveyFragmentVM extends UIComponentFragmentViewModel<InfoSurveyFragment, FragmentInfoSurveyBinding> {
    public InfoSurveyFragmentVM(InfoSurveyFragment fragment, FragmentInfoSurveyBinding binding) {
        super(fragment, binding);
    }

    @Override
    protected void initialize(FragmentInfoSurveyBinding binding) {

    }

    @Override
    protected UIComponentHolder createComponentHolder() {
        ProgressableUIComponent progressableUIComponent = new ProgressableUIComponent();
        return new UIComponentHolder.Builder().with(progressableUIComponent).build();
    }

    public boolean isDataChanged() {
        return true;
    }

    public void confirmAction() {

    }
}
