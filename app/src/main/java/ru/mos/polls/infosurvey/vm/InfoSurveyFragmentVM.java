package ru.mos.polls.infosurvey.vm;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.CardView;

import ru.mos.polls.R;
import ru.mos.polls.base.component.ProgressableUIComponent;
import ru.mos.polls.base.component.UIComponentFragmentViewModel;
import ru.mos.polls.base.component.UIComponentHolder;
import ru.mos.polls.databinding.FragmentInfoSurveyBinding;
import ru.mos.polls.infosurvey.ui.InfoCommentFragment;
import ru.mos.polls.infosurvey.ui.InfoSurveyFragment;

/**
 * Created by Trunks on 06.12.2017.
 */

public class InfoSurveyFragmentVM extends UIComponentFragmentViewModel<InfoSurveyFragment, FragmentInfoSurveyBinding> {
    CardView infoCommentCard;
    AppCompatTextView infoTitle, infoDesc, infoComment;


    public InfoSurveyFragmentVM(InfoSurveyFragment fragment, FragmentInfoSurveyBinding binding) {
        super(fragment, binding);
    }

    @Override
    protected void initialize(FragmentInfoSurveyBinding binding) {
        infoCommentCard = binding.infoSurveyCommentCard;
    }

    @Override
    protected UIComponentHolder createComponentHolder() {
        ProgressableUIComponent progressableUIComponent = new ProgressableUIComponent();
        return new UIComponentHolder.Builder().with(progressableUIComponent).build();
    }

    @Override
    public void onViewCreated() {
        super.onViewCreated();
        setListeners();
    }

    public void setListeners() {
        infoCommentCard.setOnClickListener(v -> {
            Fragment fragment = new InfoCommentFragment();
            FragmentManager fragmentManager = getFragment().getFragmentManager();
            fragmentManager
                    .beginTransaction()
                    .replace(R.id.container, fragment)
                    .addToBackStack("infosurvey")
                    .commit();
        });
    }
}
