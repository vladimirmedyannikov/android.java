package ru.mos.polls.profile.vm;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import ru.mos.polls.R;
import ru.mos.polls.base.vm.FragmentViewModel;
import ru.mos.polls.databinding.FragmentPguAuthBinding;
import ru.mos.polls.popup.PopupController;
import ru.mos.polls.profile.model.AgUser;
import ru.mos.polls.profile.state.PguAuthState;
import ru.mos.polls.profile.ui.fragment.PguAuthFragment;
import ru.mos.polls.wizardprofile.ui.fragment.WizardProfileFragment;

public class PguAuthFragmentVM extends FragmentViewModel<PguAuthFragment, FragmentPguAuthBinding> {
    public static final int PGU_AUTH = 13245;

    TextView help;
    TextView pguTitle;
    TextView pguState;
    Button pguConnBtn;
    TextView pguRebind;
    boolean forWizard;

    public PguAuthFragmentVM(PguAuthFragment fragment, FragmentPguAuthBinding binding) {
        super(fragment, binding);
    }

    @Override
    protected void initialize(FragmentPguAuthBinding binding) {
        Bundle extras = getFragment().getArguments();
        if (extras != null) {
            forWizard = extras.getBoolean(WizardProfileFragment.ARG_FOR_WIZARD);
        }
        help = binding.help.help;
        pguTitle = binding.pguTitle;
        pguState = binding.pguStateLayer.state;
        pguConnBtn = binding.pguConnectBtn;
        pguRebind = binding.rebind;
    }

    @Override
    public void onViewCreated() {
        super.onViewCreated();
        setListener();
    }

    public void setListener() {
        help.setOnClickListener(v -> PopupController.pgu(getActivity()));
        pguConnBtn.setOnClickListener(v -> getFragment().navigateToActivityForResult(new PguAuthState(PguAuthState.PGU_AUTH), PguAuthFragmentVM.PGU_AUTH));
        pguRebind.setOnClickListener(v -> getFragment().navigateToActivityForResult(new PguAuthState(PguAuthState.PGU_AUTH), PguAuthFragmentVM.PGU_AUTH));
    }

    @Override
    public void onResume() {
        super.onResume();
        setView();
    }

    public void setView() {
        if (AgUser.isPguConnected(getActivity())) {
            int stateValue = R.string.state_pgu_connected;
            int stateColor = R.color.greenText;
            pguState.setText(stateValue);
            pguState.setTextColor(getFragment().getResources().getColor(stateColor));
            pguTitle.setText(getFragment().getString(R.string.pgu_rebind_message));
            pguConnBtn.setVisibility(View.INVISIBLE);
            pguRebind.setVisibility(View.VISIBLE);
        }
        if (forWizard) {
            help.setVisibility(View.INVISIBLE);
        }
    }
}
