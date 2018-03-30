package ru.mos.polls.innovations.vm;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.ScrollView;
import android.widget.TextView;

import ru.mos.polls.AGApplication;
import ru.mos.polls.CustomDialogController;
import ru.mos.polls.GoogleStatistics;
import ru.mos.polls.PointsManager;
import ru.mos.polls.R;
import ru.mos.polls.Statistics;
import ru.mos.polls.base.activity.BaseActivity;
import ru.mos.polls.base.component.UIComponentFragmentViewModel;
import ru.mos.polls.base.component.UIComponentHolder;
import ru.mos.polls.base.rxjava.Events;
import ru.mos.polls.common.controller.UrlSchemeController;
import ru.mos.polls.common.model.QuestMessage;
import ru.mos.polls.common.view.HtmlTitleView;
import ru.mos.polls.databinding.FragmentInnovationBinding;
import ru.mos.polls.innovations.controller.InnovationApiControllerRX;
import ru.mos.polls.innovations.model.InnovationDetails;
import ru.mos.polls.innovations.model.Status;
import ru.mos.polls.innovations.ui.ChartsView;
import ru.mos.polls.innovations.ui.InnovationButtons;
import ru.mos.polls.innovations.ui.fragment.InnovationFragment;
import ru.mos.polls.innovations.vm.item.UIInnovationViewHelper;
import ru.mos.polls.social.controller.SocialUIController;
import ru.mos.polls.social.model.AppPostValue;
import ru.mos.polls.util.NetworkUtils;
import ru.mos.social.callback.PostCallback;
import ru.mos.social.controller.SocialController;
import ru.mos.social.model.PostValue;
import ru.mos.social.model.social.Social;

public class InnovationFragmentVM extends UIComponentFragmentViewModel<InnovationFragment, FragmentInnovationBinding> implements InnovationButtons.CallBack {

    private ProgressDialog progressDialog;

    private ProgressBar loadingProgress;
    private ScrollView container;
    private ChartsView chartsView;
    private HtmlTitleView htmlTitleView;
    private RatingBar ratingBar;
    private TextView innPointTitle;
    private InnovationButtons innovationButtons;
    private View rootConnectionError;
    private View root;
    private long innovationId;

    private InnovationDetails innovationDetails;

    private SocialController socialController;

    private PostCallback postCallback = new PostCallback() {
        @Override
        public void postSuccess(Social social, @Nullable PostValue postValue) {
            SocialUIController.sendPostingResult(getFragment().getContext(), (AppPostValue) postValue, null);
        }

        @Override
        public void postFailure(Social social, @Nullable PostValue postValue, Exception e) {
            SocialUIController.sendPostingResult(getFragment().getContext(), (AppPostValue) postValue, e);
        }
    };

    public InnovationFragmentVM(InnovationFragment fragment, FragmentInnovationBinding binding) {
        super(fragment, binding);
    }

    @Override
    protected void initialize(FragmentInnovationBinding binding) {
        socialController = new SocialController(getFragment().getActivity());
        loadingProgress = binding.loadingProgress;
        container = binding.root.findViewById(R.id.container);
        chartsView = binding.root.findViewById(R.id.chartsView);
        htmlTitleView = binding.root.findViewById(R.id.htmlTitleView);
        ratingBar = binding.root.findViewById(R.id.rating);
        innPointTitle = binding.root.findViewById(R.id.innPointTitle);
        innovationButtons = binding.buttonContainer;
        rootConnectionError = binding.layoutMain.findViewById(R.id.rootConnectionError);
        root = binding.root;

    }

    @Override
    protected UIComponentHolder createComponentHolder() {
        return new UIComponentHolder.Builder().build();
    }

    @Override
    public void onResume() {
        super.onResume();
        socialController.getEventController().registerCallback(postCallback);
        SocialUIController.registerPostingReceiver(getFragment().getContext());
    }

    @Override
    public void onViewCreated() {
        super.onViewCreated();
        innovationButtons.setFragment(this);
        setListenerToRatingbar();
        if (getShortInnovation()) {
            loadInnovation();
        }
        ScrollView sv = getFragment().getView().findViewById(R.id.container);
        htmlTitleView.setStateListener(new HtmlTitleView.StateListener() {
            @Override
            public void onExpand() {
                sv.requestLayout();
                sv.invalidate();
                sv.fullScroll(ScrollView.FOCUS_UP);
            }

            @Override
            public void onCollapse() {

            }
        });
    }

    @Override
    public void onPause() {
        super.onPause();
        socialController.getEventController().unregisterAllCallback();
        SocialUIController.unregisterPostingReceiver(getFragment().getContext());
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        socialController.onActivityResult(requestCode, resultCode, data);
    }

    public void onBackPressed() {
        if (innovationDetails != null && innovationDetails.isActive()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getFragment().getContext());
            builder.setMessage(R.string.message_innovation_has_not_mark)
                    .setPositiveButton(R.string.ag_yes, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Statistics.innovationsInterrupted();
                            GoogleStatistics.Innovation.innovationsInterrupted();
                            setResult();
                            getFragment().navigateToCloseCurrActivity();
                        }
                    })
                    .setNegativeButton(R.string.ag_no_only, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    })
                    .show();
        } else {
            setResult();
            getFragment().navigateToCloseCurrActivity();
        }
    }

    public boolean checkInternetConnection() {
        if (NetworkUtils.hasInternetConnection(getFragment().getContext())) {
            hideErrorConnectionViews();
            return true;
        } else {
            setErrorConneсtionView();
            return false;
        }
    }

    public void hideErrorConnectionViews() {
        if (rootConnectionError.getVisibility() == View.VISIBLE) {
            rootConnectionError.setVisibility(View.GONE);
        }
        if (root.getVisibility() == View.GONE) {
            root.setVisibility(View.VISIBLE);
        }
    }

    public void setErrorConneсtionView() {
        rootConnectionError.setVisibility(View.VISIBLE);
        root.setVisibility(View.GONE);
    }

    private void setListenerToRatingbar() {
        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                innovationButtons.setSendButtonEnable(rating > 0);
            }
        });
    }

    private void setResult() {
        if (innovationDetails != null && innovationDetails.isPassed()) {
            Intent result = new Intent();
            result.putExtra(InnovationFragment.EXTRA_INNOVATION, innovationDetails);
            getActivity().setResult(Activity.RESULT_OK, result);
        }
    }

    private boolean getShortInnovation() {
        innovationId = getFragment().getArguments().getLong(InnovationFragment.EXTRA_SHORT_INNOVATION, -1);
        startFromUri();
        boolean result = innovationId != -1;
        if (!result) {
            getFragment().navigateToCloseCurrActivity();
        }
        return result;
    }

    private void loadInnovation() {
        showLoading(true);

        InnovationApiControllerRX.InnovationListener listener = new InnovationApiControllerRX.InnovationListener() {
            @Override
            public void onLoaded(InnovationDetails innovationDetails) {
                InnovationFragmentVM.this.innovationDetails = innovationDetails;
                InnovationFragmentVM.this.innovationDetails.setId(innovationId);
                refreshUI();
                showLoading(false);
            }

            @Override
            public void onError() {
                loadingProgress.setVisibility(View.GONE);
                checkInternetConnection();
            }
        };
        InnovationApiControllerRX.get(disposables, getFragment().getContext(), innovationId, listener);
    }

    private void fill() {
        startProgress();
        InnovationApiControllerRX.FillNoveltyListener listener = new InnovationApiControllerRX.FillNoveltyListener() {
            @Override
            public void onSuccess(ru.mos.polls.innovations.model.Rating rating, QuestMessage message, int allPoints) {
                innovationDetails.setStatus(Status.PASSED);
                innovationDetails.setRating(rating);
                innovationDetails.setPassedDate(System.currentTimeMillis() / 1000L);
                refreshUI();
                stopProgress();
                showResults(message, allPoints);
                scrollToChart();
                AGApplication.bus().send(new Events.InnovationsEvents(innovationDetails.getId(), rating.getFullRating(), innovationDetails.getPassedDate(), Events.InnovationsEvents.PASSED_INNOVATIONS));
            }

            @Override
            public void onError() {
                stopProgress();
            }
        };
        InnovationApiControllerRX.fill(disposables, getFragment().getContext(), innovationDetails.getId(), (int) ratingBar.getRating(), listener);
    }

    protected void startProgress() {
        progressDialog = new ProgressDialog(getFragment().getContext());
        progressDialog.show();
    }

    protected void stopProgress() {
        /**
         * если активти уничтожено, возможен такой вид исключения
         */
        try {
            if (progressDialog != null) {
                progressDialog.dismiss();
            }
        } catch (WindowManager.BadTokenException ignored) {

        }
    }

    private void scrollToChart() {
        container.post(new Runnable() {
            @Override
            public void run() {
                container.scrollTo(0, chartsView.getTop());
            }
        });
    }

    private void showResults(QuestMessage message, int allPoints) {
        if (CustomDialogController.isShareEnable(getFragment().getContext())) {
            CustomDialogController.ActionListener listener = new CustomDialogController.ActionListener() {
                @Override
                public void onYes(Dialog dialog) {
                    dismiss(dialog);
                    SocialUIController.SocialClickListener listener = new SocialUIController.SocialClickListener() {
                        @Override
                        public void onClick(Context context, Dialog dialog, AppPostValue socialPostValue) {
                            socialPostValue.setId(innovationDetails.getId());
                            if (socialController != null) {
                                socialController.post(socialPostValue, socialPostValue.getSocialId());
                            }
                        }

                        @Override
                        public void onCancel() {
                            getFragment().navigateToCloseCurrActivity();
                        }
                    };
                    SocialUIController.showSocialsDialogForNovelty(disposables, (BaseActivity) getFragment().getActivity(), innovationDetails, listener, null);
                }

                @Override
                public void onCancel(Dialog dialog) {
                    dismiss(dialog);
                }

                @Override
                public void onDisable(Dialog dialog) {
                    dismiss(dialog);
                }

                private void dismiss(Dialog dialog) {
                    if (dialog != null) {
                        dialog.dismiss();
                    }
                }
            };
            String share = getDialogMessage(innovationDetails.getPoints(), allPoints);
            CustomDialogController.showShareDialog(getFragment().getContext(), share, listener);
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(getFragment().getContext());
            builder.setMessage(getDialogMessage(innovationDetails.getPoints(), allPoints));
            builder.setCancelable(false);
            builder.setPositiveButton(R.string.ag_ok, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                }
            });
            builder.show();
        }
    }

    private String getDialogMessage(int point, int allPoints) {
        String message;
        if (point > 0) {
//            String pointTxt = PointsManager.getPointUnitString(this, point);
//            message = String.format(getString(R.string.novelty_result_send_share_with_points), String.valueOf(point), pointTxt, String.valueOf(allPoints), PointsManager.getPointUnitString(this, allPoints));
            message = PointsManager.getMessage(getFragment().getContext(), point, allPoints);
        } else {
            message = getFragment().getString(R.string.novelty_result_send_share);
        }
        if (CustomDialogController.isShareEnable(getFragment().getContext())) {
            message = message + " " + getFragment().getString(R.string.share_with_friends_for_points);
        }
        return message;
    }

    private void refreshUI() {
        htmlTitleView.display(innovationDetails);
        processCharts();
        processRatingBar();
        processActions();
    }

    private void processCharts() {
        chartsView.setVisibility(!innovationDetails.isActive() ? View.VISIBLE : View.GONE);
        chartsView.display(innovationDetails);
    }

    private void processRatingBar() {
        if (innovationDetails.getRating() != null) {
            ratingBar.setRating(innovationDetails.getRating().getUserRating());
        }
        ratingBar.setIsIndicator(!innovationDetails.isActive());
        if (innovationDetails.isOld()) {
            ratingBar.setVisibility(View.GONE);
            getBinding().root.findViewById(R.id.hint).setVisibility(View.GONE);
            getBinding().root.findViewById(R.id.ratingDisable).setVisibility(View.GONE);
        }
    }

    private void processActions() {
        int visibility = View.VISIBLE;
        int innerPointsVisibility = View.GONE;
        String innPointTitleTxt = "";
        innovationButtons.setSendButtonDisabled();
        switch (innovationDetails.getStatus()) {
            case ACTIVE:
                if (innovationDetails.getPoints() > 0) {
                    String suitableString = PointsManager.getSuitableString(getFragment().getContext(), R.array.survey_points_pluse, innovationDetails.getPoints());
                    innPointTitleTxt = String.format(suitableString, innovationDetails.getPoints());
                    innerPointsVisibility = View.VISIBLE;
                }
                break;
            case PASSED:
                String pointTxt = PointsManager.getPointUnitString(getFragment().getContext(), innovationDetails.getPoints());
                innovationButtons.renderPassedButton();
                if (innovationDetails.getPoints() > 0) {
                    String pointsAdded = getFragment().getResources().getQuantityString(R.plurals.points_added, innovationDetails.getPoints());
                    innPointTitleTxt = String.format(getFragment().getString(R.string.passed_points_formatted), pointsAdded, String.valueOf(innovationDetails.getPoints()), pointTxt, UIInnovationViewHelper.getReadablePassedDate(innovationDetails.getPassedDate()));
                    innerPointsVisibility = View.VISIBLE;
                } else {
                    innPointTitleTxt = String.format(getFragment().getString(R.string.vote_date_text), UIInnovationViewHelper.getReadablePassedDate(innovationDetails.getPassedDate()));
                    innerPointsVisibility = View.VISIBLE;
                }
                break;
            case OLD:
                innPointTitleTxt = getFragment().getString(R.string.innovation_ended) + " " + innovationDetails.getReadableEndDate();
                innerPointsVisibility = View.VISIBLE;
                innPointTitle.setTextColor(getFragment().getResources().getColor(R.color.novelty_list_rate));
                visibility = View.GONE;
                break;
        }
        innPointTitle.setText(innPointTitleTxt);
        innPointTitle.setVisibility(innerPointsVisibility);
        getBinding().root.findViewById(R.id.buttonContainer).setVisibility(visibility);
    }

    private void share() {
        SocialUIController.SocialClickListener socialClickListener = new SocialUIController.SocialClickListener() {
            @Override
            public void onClick(Context context, Dialog dialog, AppPostValue socialPostValue) {
                socialController.post(socialPostValue, socialPostValue.getSocialId());
                scrollToChart();
            }

            @Override
            public void onCancel() {
                getFragment().navigateToCloseCurrActivity();
            }
        };
        SocialUIController.showSocialsDialogForNovelty(disposables, (BaseActivity) getFragment().getActivity(), innovationDetails, socialClickListener, null);
    }

    private void startFromUri() {
        UrlSchemeController.startNovelty((BaseActivity) getFragment().getActivity(), new UrlSchemeController.IdListener() {
            @Override
            public void onDetectedId(Object id) {
                innovationId = (Long) id;
            }
        });
    }

    private void showLoading(boolean show) {
        root.setVisibility(show ? View.GONE : View.VISIBLE);
        loadingProgress.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    @Override
    public void rateInnovation() {
        fill();
    }

    @Override
    public void shareSocial() {
        share();
    }
}
