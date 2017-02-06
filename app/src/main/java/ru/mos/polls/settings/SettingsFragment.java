package ru.mos.polls.settings;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import ru.mos.elk.BaseActivity;
import ru.mos.elk.profile.ProfileManager;
import ru.mos.polls.AgAuthActivity;
import ru.mos.polls.AgChangePasswordActivity;
import ru.mos.polls.GoogleStatistics;
import ru.mos.polls.MainActivity;
import ru.mos.polls.R;
import ru.mos.polls.Statistics;
import ru.mos.polls.helpers.TitleHelper;
import ru.mos.polls.subscribes.gui.SubscribeActivity;


public class SettingsFragment extends Fragment {
    public static Fragment newInstance() {
        return new SettingsFragment();
    }

    private SettingsAdapter.Callback callback = new SettingsAdapter.Callback() {
        @Override
        public void onSettingSelected(int settingId) {
            switch (settingId) {
                case SettingItem.SUBSCRIBE:
                    SubscribeActivity.startActivity(getActivity());
                    break;
                case SettingItem.USER_LOCK:
                    Statistics.blockAccount();
                    GoogleStatistics.AGNavigation.blockAccount();
                    notifyAboutBlocking();
                    break;
                case SettingItem.CHANGE_PASSWORD:
                    startActivity(new Intent(getActivity(), AgChangePasswordActivity.class));
                    break;
                case SettingItem.LOGOUT:
                    showLogoutDialog();
                    break;
            }
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TitleHelper.setTitle(getActivity(), R.string.title_settings);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_dynamic, null);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        ListView listView = (ListView) view.findViewById(android.R.id.list);
        SettingsAdapter adapter = new SettingsAdapter(getActivity(), SettingItem.SETTINGS);
        adapter.setCallback(callback);
        listView.setAdapter(adapter);
    }

    private void showLogoutDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(R.string.confirm_logout);
        builder.setPositiveButton(R.string.logout, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                ProfileManager
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
