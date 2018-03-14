package ru.mos.polls.settings.vm;

import android.app.AlertDialog;
import android.content.DialogInterface;

import ru.mos.polls.AgAuthActivity;
import ru.mos.polls.GoogleStatistics;
import ru.mos.polls.MainActivity;
import ru.mos.polls.R;
import ru.mos.polls.Statistics;
import ru.mos.polls.base.activity.BaseActivity;
import ru.mos.polls.base.component.RecyclerUIComponent;
import ru.mos.polls.base.component.UIComponentFragmentViewModel;
import ru.mos.polls.base.component.UIComponentHolder;
import ru.mos.polls.changepassword.state.ChangePasswordState;
import ru.mos.polls.databinding.LayoutSettingsBinding;
import ru.mos.polls.geotarget.manager.GeotargetManager;
import ru.mos.polls.profile.ProfileManagerRX;
import ru.mos.polls.settings.model.Item;
import ru.mos.polls.settings.ui.adapter.ItemsAdapter;
import ru.mos.polls.settings.ui.fragment.SettingsFragment;
import ru.mos.polls.sourcesvoting.state.SourcesVotingState;
import ru.mos.polls.subscribes.state.SubscribeState;

public class SettingsFragmentVM extends UIComponentFragmentViewModel<SettingsFragment, LayoutSettingsBinding> {

    public SettingsFragmentVM(SettingsFragment fragment, LayoutSettingsBinding binding) {
        super(fragment, binding);
    }

    @Override
    protected void initialize(LayoutSettingsBinding binding) {
        getActivity().setTitle(R.string.title_settings);
    }

    @Override
    protected UIComponentHolder createComponentHolder() {
        return new UIComponentHolder.Builder()
                .with(new RecyclerUIComponent(new ItemsAdapter(Item.SETTINGS)))
                .build();
    }

    @Override
    public void onViewCreated() {
        super.onViewCreated();
        ((ItemsAdapter) getComponent(RecyclerUIComponent.class).getAdapter()).setOnItemClickListener(item -> {
            switch (item.getId()) {
                case Item.SUBSCRIBE:
                    getFragment().navigateTo(new SubscribeState(), ru.mos.polls.base.ui.BaseActivity.class);
                    break;
                case Item.USER_LOCK:
                    Statistics.blockAccount();
                    GoogleStatistics.AGNavigation.blockAccount();
                    notifyAboutBlocking();
                    break;
                case Item.CHANGE_PASSWORD:
                    getFragment().navigateTo(new ChangePasswordState(), ru.mos.polls.base.ui.BaseActivity.class);
                    break;
                case Item.LOGOUT:
                    showLogoutDialog();
                    break;
                case Item.SOURCES_POLL:
                    getFragment().navigateTo(new SourcesVotingState(), ru.mos.polls.base.ui.BaseActivity.class);
                    break;
            }
        });
    }

    private void showLogoutDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(R.string.confirm_logout);
        builder.setPositiveButton(R.string.logout, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                GeotargetManager.stop(getFragment().getContext());
                ProfileManagerRX
                        .logOut((BaseActivity) getActivity(), AgAuthActivity.class, MainActivity.class);
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        builder.show();
    }

    private void notifyAboutBlocking() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(R.string.notify_about_blocking);
        builder.setNegativeButton(R.string.ag_ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        builder.show();
    }
}
