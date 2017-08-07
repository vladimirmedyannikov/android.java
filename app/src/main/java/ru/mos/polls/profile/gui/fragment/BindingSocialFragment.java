package ru.mos.polls.profile.gui.fragment;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.SwitchCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ListView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;
import butterknife.Unbinder;
import ru.mos.elk.BaseActivity;
import ru.mos.polls.AGApplication;
import ru.mos.polls.CustomDialogController;
import ru.mos.polls.GoogleStatistics;
import ru.mos.polls.R;
import ru.mos.polls.Statistics;
import ru.mos.polls.badge.manager.BadgeManager;
import ru.mos.polls.newprofile.base.rxjava.Events;
import ru.mos.polls.social.adapter.SocialBindAdapter;
import ru.mos.polls.social.controller.AgSocialApiController;
import ru.mos.polls.social.controller.SocialController;
import ru.mos.polls.social.model.Social;
import ru.mos.polls.social.model.SocialBindItem;

/**
 * Экран привязки аккаунтов соцсетей к аккаунту аг
 *
 * @since 1.9
 */
public class BindingSocialFragment extends Fragment {
    private static final String IS_TASK = "is_task";

    public static BindingSocialFragment newInstance(boolean isTask) {
        final BindingSocialFragment fragment = new BindingSocialFragment();
        Bundle args = new Bundle(1);
        args.putBoolean(IS_TASK, isTask);
        fragment.setArguments(args);
        return fragment;
    }

    public static BindingSocialFragment newInstance() {
        return new BindingSocialFragment();
    }

    private ProgressDialog progressDialog;
    private Unbinder unbinder;
    private SocialController socialController;
    private SocialBindAdapter socialBindAdapter;
    private List<Social> changedSocials, savedSocials;
    boolean isAnySocialBinded;
    @BindView(R.id.socialShareNotify)
    SwitchCompat socialShareNotify;
    @BindView(R.id.notifyContainer)
    View notifyContainer;
    private boolean isTask;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_bind_social, null);
        unbinder = ButterKnife.bind(this, root);
        return root;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        findViews(view);
        refreshSocials();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (socialController.onActivityResult(requestCode, resultCode, data)) {
            return;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public boolean isQuestExecuted() {
        return isTask && !Social.isEquals(savedSocials, changedSocials);
    }

    private void init() {
        socialController = new SocialController((BaseActivity) getActivity());
        savedSocials = Social.getSavedSocials(getActivity());
        changedSocials = Social.getSavedSocials(getActivity());
        socialBindAdapter = new SocialBindAdapter(getActivity(), changedSocials);
        if (getArguments() != null) {
            isTask = getArguments().getBoolean(IS_TASK);
        }
    }

    private void findViews(View v) {
        notifyContainer.setVisibility(isTask ? View.GONE : View.VISIBLE);
        socialShareNotify.setChecked(CustomDialogController.isShareEnable(getActivity()));
        ListView gridView = ButterKnife.findById(v, R.id.list);
        gridView.setAdapter(socialBindAdapter);
        socialBindAdapter.setListener(new SocialBindAdapter.Listener() {
            @Override
            public void onBindClick(Social social) {
                bindSocial(social);
            }

            @Override
            public void onCloseClick(Social social) {
                showUnBindDialog(social);
            }
        });
    }

    @OnCheckedChanged(R.id.socialShareNotify)
    void socialShare(boolean isChecked) {
        socialShareNotify.setChecked(isChecked);
        CustomDialogController.setShareAbility(getActivity(), isChecked);
    }

    private void showUnBindDialog(final Social social) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        String message = String.format(getString(R.string.confirm_unbind_message),
                getString(SocialBindItem.getTitle(social.getSocialId())));
        builder.setMessage(message);
        builder.setNegativeButton(R.string.ag_no, null);
        builder.setPositiveButton(R.string.confirm_unbind_ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                unBindSocial(social);
            }
        });
        builder.show();
    }

    private void refreshSocials() {
        showProgress(getString(R.string.update_social_message));
        AgSocialApiController.LoadSocialListener listener = new AgSocialApiController.LoadSocialListener() {
            @Override
            public void onLoaded(List<Social> loadedSocials) {
                changedSocials.clear();
                changedSocials.addAll(loadedSocials);
                socialBindAdapter.notifyDataSetChanged();
                hideProgress();
            }

            @Override
            public void onError() {
                hideProgress();
            }
        };
        AgSocialApiController.loadSocials((BaseActivity) getActivity(), listener);
    }

    private void bindSocial(final Social social) {
        showProgress(getString(R.string.bind_progress_message));
        AgSocialApiController.SaveSocialListener listener = new AgSocialApiController.SaveSocialListener() {
            @Override
            public void onSaved(final Social loadedSocial, int freezedPoints, int spentPoints, int allPoints, int currentPoints, String state) {
                LocalBroadcastManager.getInstance(getActivity()).sendBroadcast(new Intent(BadgeManager.ACTION_RELOAD_BAGES_FROM_SERVER));
                if (isTask) {
                    if (!isAnySocialBinded) {
                        AGApplication.bus().send(new Events.WizardEvents(Events.WizardEvents.WIZARD_SOCIAL, 0));
                        isAnySocialBinded = true;
                    }
                    Statistics.taskSocialLogin(loadedSocial.getSocialName());
                } else {
                    Statistics.profileSocialLogin(loadedSocial.getSocialName());
                }
                GoogleStatistics.BindSocialFragment.taskSocialLogin(loadedSocial.getSocialName(), isTask);
                social.copy(loadedSocial);
                social.setIsLogin(true);
                social.save(getActivity());
                socialBindAdapter.notifyDataSetChanged();
                hideProgress();
            }

            @Override
            public void onError(Social social) {
                hideProgress();
            }
        };
        socialController.bindSocial(social, listener);
    }

    private void unBindSocial(final Social social) {
        showProgress(getString(R.string.unbind_progress_message));
        AgSocialApiController.SaveSocialListener listener = new AgSocialApiController.SaveSocialListener() {
            @Override
            public void onSaved(final Social loadedSocial, int freezedPoints, int spentPoints, int allPoints, int currentPoints, String state) {
                LocalBroadcastManager.getInstance(getActivity()).sendBroadcast(new Intent(BadgeManager.ACTION_RELOAD_BAGES_FROM_SERVER));
                if (isTask) {
                    Statistics.taskSocialLogin(loadedSocial.getSocialName());
                } else {
                    Statistics.profileSocialLogin(loadedSocial.getSocialName());
                }
                social.reset(getActivity());
                socialBindAdapter.notifyDataSetChanged();
                hideProgress();
            }

            @Override
            public void onError(Social social) {
                hideProgress();
            }
        };
        socialController.unBindSocial(social, listener);
    }

    private void showProgress(String message) {
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage(message);
        progressDialog.show();
    }

    private void hideProgress() {
        try {
            if (progressDialog != null) {
                progressDialog.dismiss();
            }
        } catch (Exception ignored) {
        }
    }
}
