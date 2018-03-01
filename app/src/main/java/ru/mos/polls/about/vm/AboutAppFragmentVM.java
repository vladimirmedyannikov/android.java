package ru.mos.polls.about.vm;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;

import me.ilich.juggler.change.Add;
import ru.mos.polls.BuildConfig;
import ru.mos.polls.GoogleStatistics;
import ru.mos.polls.R;
import ru.mos.polls.Statistics;
import ru.mos.polls.WebViewActivity;
import ru.mos.polls.about.model.AboutItem;
import ru.mos.polls.about.ui.fragment.AboutAppFragment;
import ru.mos.polls.base.activity.BaseActivity;
import ru.mos.polls.base.component.RecyclerUIComponent;
import ru.mos.polls.base.component.UIComponentFragmentViewModel;
import ru.mos.polls.base.component.UIComponentHolder;
import ru.mos.polls.databinding.LayoutAboutAppBinding;
import ru.mos.polls.helpers.FunctionalHelper;
import ru.mos.polls.instruction.InstructionActivity;
import ru.mos.polls.ourapps.state.OurAppsState;
import ru.mos.polls.social.controller.SocialUIController;
import ru.mos.polls.social.model.AppPostValue;
import ru.mos.polls.support.state.SupportState;


public class AboutAppFragmentVM extends UIComponentFragmentViewModel<AboutAppFragment, LayoutAboutAppBinding> {
    private SocialListener socialListener;

    public AboutAppFragmentVM(AboutAppFragment fragment, LayoutAboutAppBinding binding, SocialListener socialListener) {
        super(fragment, binding);
        this.socialListener = socialListener;
    }

    @Override
    protected void initialize(LayoutAboutAppBinding binding) {
        Statistics.appsDescription();
        GoogleStatistics.AGNavigation.appsDescription();
        getActivity().setTitle(R.string.title_help);
    }

    @Override
    protected UIComponentHolder createComponentHolder() {
        return new UIComponentHolder.Builder()
                .with(new RecyclerUIComponent<RecyclerView.Adapter>(AboutItem.getAdapter((id, position) -> {
                    Intent intent = null;
                    switch (id) {
                        case AboutItem.ABOUT_PROJECT:
                            WebViewActivity.startActivity(getActivity(),
                                    getActivity().getString(R.string.title_about_project),
                                    getActivity().getString(R.string.url_about_app),
                                    null,
                                    false,
                                    false);
                            break;
                        case AboutItem.USER_GUIDE:
                            WebViewActivity.startActivity(getActivity(),
                                    getActivity().getString(R.string.title_user_guide),
                                    getActivity().getString(R.string.url_user_guide),
                                    null,
                                    false,
                                    false);
                            break;
                        case AboutItem.INSTRUCTION:
                            InstructionActivity.startActivity(getActivity());
                            break;
                        case AboutItem.OFFER:
                            WebViewActivity.startActivity(getActivity(),
                                    getActivity().getString(R.string.title_offer),
                                    getActivity().getString(R.string.url_offer),
                                    null,
                                    false,
                                    false);
                            break;
                        case AboutItem.OUR_APPS:
                            Statistics.ourApps();
                            GoogleStatistics.AGNavigation.ourApps();
                            getFragment().navigateTo().state(Add.newActivity(new OurAppsState(), ru.mos.polls.base.ui.BaseActivity.class));
                            break;
                        case AboutItem.SHARE_SOCIAL:
                            SocialUIController.showSocialsDialog(disposables, (BaseActivity) getActivity(), new SocialUIController.SocialClickListener() {
                                @Override
                                public void onClick(Context context, Dialog dialog, AppPostValue appPostValue) {
                                    socialListener.onSocialPost(appPostValue);
                                }

                                @Override
                                public void onCancel() {
                                    getActivity().finish();
                                }
                            }, null);
                            break;
                        case AboutItem.RATE_APP:
                            FunctionalHelper.startGooglePlay(getActivity());
                            break;
                        case AboutItem.FEEDBACK:
                            getFragment().navigateTo().state(Add.newActivity(new SupportState(true), ru.mos.polls.base.ui.BaseActivity.class));
                            break;
                    }
                })))
                .build();
    }

    @Override
    public void onViewCreated() {
        super.onViewCreated();
        getBinding().appVersion.setText(getActivity().getString(R.string.app_version_template, BuildConfig.VERSION_NAME));
    }

    public void setSocialListener(SocialListener socialListener) {
        this.socialListener = socialListener;
    }

    public static interface SocialListener {
        void onSocialPost(AppPostValue appPostValue);
    }
}
