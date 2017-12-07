package ru.mos.polls.infosurvey.ui;

import android.app.Activity;
import android.content.DialogInterface;
import android.os.Bundle;

import ru.mos.polls.R;
import ru.mos.polls.base.ui.MenuBindingFragment;
import ru.mos.polls.databinding.FragmentInfoCommentBinding;
import ru.mos.polls.infosurvey.vm.InfoCommentFragmentVM;
import ru.mos.polls.infosurvey.vm.InfoSurveyFragmentVM;
import ru.mos.polls.util.GuiUtils;

/**
 * Created by Trunks on 06.12.2017.
 */

public class InfoCommentFragment extends MenuBindingFragment<InfoCommentFragmentVM, FragmentInfoCommentBinding> {

    public static InfoCommentFragment newInstance() {
        InfoCommentFragment f = new InfoCommentFragment();
        Bundle args = new Bundle();
        f.setArguments(args);
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
                        getActivity().setResult(Activity.RESULT_CANCELED);
                        getActivity().finish();
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
            return false;
        }
    }
}
