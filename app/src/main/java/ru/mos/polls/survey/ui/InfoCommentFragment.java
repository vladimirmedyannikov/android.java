package ru.mos.polls.survey.ui;

import android.content.DialogInterface;

import ru.mos.polls.R;
import ru.mos.polls.base.ui.MenuBindingFragment;
import ru.mos.polls.databinding.FragmentInfoCommentBinding;
import ru.mos.polls.survey.vm.InfoCommentFragmentVM;
import ru.mos.polls.survey.Survey;
import ru.mos.polls.util.GuiUtils;

/**
 * Created by Trunks on 06.12.2017.
 */

public class InfoCommentFragment extends MenuBindingFragment<InfoCommentFragmentVM, FragmentInfoCommentBinding> {

    public static String INFO_COMMENT_FRAGMENT_TAG = "infosurvey";
    public Survey survey;

    public static InfoCommentFragment newInstance(Survey survey) {
        InfoCommentFragment f = new InfoCommentFragment();
        f.setSurvey(survey);
        return f;
    }

    @Override
    protected InfoCommentFragmentVM onCreateViewModel(FragmentInfoCommentBinding binding) {
        return new InfoCommentFragmentVM(this, binding);
    }

    @Override
    public int getLayoutResources() {
        return R.layout.fragment_info_comment;
    }

    @Override
    public int getMenuResource() {
        return R.menu.confirm;
    }


    @Override
    public boolean onBackPressed() {
        return onUpPressed();
    }

    @Override
    public boolean onUpPressed() {
        if (getViewModel().isDataChanged()) {
            DialogInterface.OnClickListener okListener = (dialogInterface, i) -> {
                switch (i) {
                    case DialogInterface.BUTTON_POSITIVE:
                        getViewModel().confirmAction();
                        break;
                    case DialogInterface.BUTTON_NEGATIVE:
                        getFragmentManager().popBackStackImmediate();
                        break;
                }
            };
            GuiUtils.displayAreYouSureDialogTitle(getContext(),
                    "Вы хотите сохранить введенные данные?",
                    null,
                    okListener
            );
            return true;
        } else {
            getFragmentManager().popBackStackImmediate();
            return true;
        }
    }

    public void setSurvey(Survey survey) {
        this.survey = survey;
    }

    public Survey getSurvey() {
        return survey;
    }
}
