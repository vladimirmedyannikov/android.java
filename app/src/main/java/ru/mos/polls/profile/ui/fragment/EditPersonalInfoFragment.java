package ru.mos.polls.profile.ui.fragment;

import android.app.Activity;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;

import ru.mos.elk.profile.AgUser;
import ru.mos.polls.R;
import ru.mos.polls.databinding.FragmentNewEditPersonalInfoBinding;
import ru.mos.polls.base.ui.MenuBindingFragment;
import ru.mos.polls.profile.vm.EditPersonalInfoFragmentVM;
import ru.mos.polls.util.GuiUtils;
import ru.mos.polls.wizardprofile.ui.fragment.WizardProfileFragment;

/**
 * Created by Trunks on 04.07.2017.
 */

public class EditPersonalInfoFragment extends MenuBindingFragment<EditPersonalInfoFragmentVM, FragmentNewEditPersonalInfoBinding> {

    public static final String ARG_PERSONAL_INFO = "arg_personal_info";
    public static final String ARG_AGUSER = "arg_aguser";

    public static EditPersonalInfoFragment newInstanceForWizard(AgUser agUser, int personalType) {
        return newInstance(agUser, personalType, true);
    }

    public static EditPersonalInfoFragment newInstance(AgUser agUser, int personalType, boolean forWizard) {
        EditPersonalInfoFragment f = new EditPersonalInfoFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_PERSONAL_INFO, personalType);
        args.putSerializable(ARG_AGUSER, agUser);
        args.putBoolean(WizardProfileFragment.ARG_FOR_WIZARD, forWizard);
        f.setArguments(args);
        return f;
    }

    public EditPersonalInfoFragment() {
    }

    @Override
    protected EditPersonalInfoFragmentVM onCreateViewModel(FragmentNewEditPersonalInfoBinding binding) {
        return new EditPersonalInfoFragmentVM(this, getBinding());
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public int getLayoutResources() {
        return R.layout.fragment_new_edit_personal_info;
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

    @Override
    public void doRequestAction() {
        getViewModel().confirmAction();
    }
}
