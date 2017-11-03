package ru.mos.polls.about;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import me.ilich.juggler.change.Add;
import me.ilich.juggler.gui.JugglerFragment;
import ru.mos.elk.BaseActivity;
import ru.mos.elk.api.API;
import ru.mos.polls.BuildConfig;
import ru.mos.polls.GoogleStatistics;
import ru.mos.polls.R;
import ru.mos.polls.Statistics;
import ru.mos.polls.ToolbarDynamicActivity;
import ru.mos.polls.UrlManager;
import ru.mos.polls.WebViewActivity;
import ru.mos.polls.fragments.DynamicFragment;
import ru.mos.polls.helpers.FunctionalHelper;
import ru.mos.polls.helpers.TitleHelper;
import ru.mos.polls.instruction.InstructionActivity;
import ru.mos.polls.support.state.SupportState;
import ru.mos.polls.social.controller.SocialUIController;
import ru.mos.polls.social.model.AppPostValue;

/**
 * Экран "О приложении", содержит список пукнтов {@link AboutItem}, описывающих общие положения о приложении
 *
 * @since 1.0
 */
public class AboutAppFragment extends JugglerFragment {
    private SocialListener socialListener;
    @BindView(R.id.app_version)
    TextView versionTextView;
    @BindView(R.id.list)
    ListView list;
    private Unbinder unbinder;

    public static AboutAppFragment newInstance(SocialListener socialListener) {
        final AboutAppFragment fragment = new AboutAppFragment();
        fragment.setSocialListener(socialListener);
        return fragment;
    }

    public AboutAppFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TitleHelper.setTitle(getActivity(), R.string.title_help);
        Statistics.appsDescription();
        GoogleStatistics.AGNavigation.appsDescription();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_about_app, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        versionTextView.setText(getString(R.string.app_version_template, BuildConfig.VERSION_NAME));
        list.setAdapter(AboutItem.getAdapter(getActivity()));
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = null;
                switch ((int) id) {
                    case AboutItem.ABOUT_PROJECT:
                        WebViewActivity.startActivity(getActivity(),
                                getString(R.string.title_about_project),
                                getString(R.string.url_about_app),
                                null,
                                false,
                                false);
                        break;
                    case AboutItem.USER_GUIDE:
                        WebViewActivity.startActivity(getActivity(),
                                getString(R.string.title_user_guide),
                                getString(R.string.url_user_guide),
                                null,
                                false,
                                false);
                        break;
                    case AboutItem.INSTRUCTION:
                        InstructionActivity.startActivity(getActivity());
                        break;
                    case AboutItem.OFFER:
                        WebViewActivity.startActivity(getActivity(),
                                getString(R.string.title_offer),
                                getString(R.string.url_offer),
                                null,
                                false,
                                false);
                        break;
                    case AboutItem.OUR_APPS:
                        Statistics.ourApps();
                        GoogleStatistics.AGNavigation.ourApps();
                        intent = new Intent(getActivity(), ToolbarDynamicActivity.class);
                        intent.putExtra(DynamicFragment.BASE_URL, API.getURL(UrlManager.url(UrlManager.Controller.INFORAMTION, UrlManager.Methods.GET_OUR_APPS)));
                        intent.putExtra(DynamicFragment.PARAMS, "{\"user_agent\": \"android\"}");
                        intent.putExtra(DynamicFragment.DEF_TITLE, getString(R.string.our_apps));
                        startActivity(intent);
                        break;
                    case AboutItem.SHARE_SOCIAL:
                        SocialUIController.showSocialsDialog((BaseActivity) getActivity(), new SocialUIController.SocialClickListener() {
                            @Override
                            public void onClick(Context context, Dialog dialog, AppPostValue appPostValue) {
                                socialListener.onSocialPost(appPostValue);
                            }

                            @Override
                            public void onCancel() {
                                getActivity().finish();
                            }
                        });
                        break;
                    case AboutItem.RATE_APP:
                        FunctionalHelper.startGooglePlay(getActivity());
                        break;
                    case AboutItem.FEEDBACK:
//                        AgSupportActivity.startActivity(getActivity());
                        navigateTo().state(Add.newActivity(new SupportState(true), ru.mos.polls.base.ui.BaseActivity.class));
                        break;
                }
            }
        });
    }

    public void setSocialListener(SocialListener socialListener) {
        this.socialListener = socialListener;
    }

    public static interface SocialListener {
        void onSocialPost(AppPostValue appPostValue);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
