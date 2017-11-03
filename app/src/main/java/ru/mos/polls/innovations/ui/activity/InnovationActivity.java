package ru.mos.polls.innovations.ui.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley2.VolleyError;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import ru.mos.polls.AGApplication;
import ru.mos.polls.CustomDialogController;
import ru.mos.polls.GoogleStatistics;
import ru.mos.polls.PointsManager;
import ru.mos.polls.R;
import ru.mos.polls.Statistics;
import ru.mos.polls.ToolbarAbstractActivity;
import ru.mos.polls.base.rxjava.Events;
import ru.mos.polls.common.controller.UrlSchemeController;
import ru.mos.polls.common.model.QuestMessage;
import ru.mos.polls.common.view.HtmlTitleView;
import ru.mos.polls.helpers.TitleHelper;
import ru.mos.polls.innovations.controller.InnovationApiController;
import ru.mos.polls.innovations.oldmodel.InnovationActiviti;
import ru.mos.polls.innovations.ui.InnovationButtons;
import ru.mos.polls.innovations.oldmodel.Rating;
import ru.mos.polls.innovations.model.ShortInnovation;
import ru.mos.polls.innovations.oldmodel.Status;
import ru.mos.polls.innovations.ui.ChartsView;
import ru.mos.polls.social.controller.SocialUIController;
import ru.mos.polls.social.model.AppPostValue;
import ru.mos.polls.util.NetworkUtils;
import ru.mos.social.callback.PostCallback;
import ru.mos.social.controller.SocialController;
import ru.mos.social.model.PostValue;
import ru.mos.social.model.social.Social;

/**
 * Экран для отображения городской новинки
 *
 * @since 1.9
 */
public class InnovationActivity extends ToolbarAbstractActivity implements InnovationButtons.CallBack {
    private static final int REQUEST = 100;
    private static final String EXTRA_SHORT_INNOVATION = "extra_short_innovation";
    private static final String EXTRA_INNOVATION = "extra_innovation";

    public static void startActivity(Fragment fragment, long shortInnovationId) {
        Intent start = new Intent(fragment.getActivity(), InnovationActivity.class);
        start.putExtra(EXTRA_SHORT_INNOVATION, shortInnovationId);
        fragment.startActivityForResult(start, REQUEST);
    }

    public static void startActivity(Context context, ShortInnovation shortInnovation) {
        startActivity(context, shortInnovation.getId());
    }

    public static void startActivity(Context context, long id) {
        Intent intent = new Intent(context, InnovationActivity.class);
        intent.putExtra(EXTRA_SHORT_INNOVATION, id);
        context.startActivity(intent);
    }

    public static Intent getStartIntent(Context context, long id) {
        Intent intent = new Intent(context, InnovationActivity.class);
        intent.putExtra(EXTRA_SHORT_INNOVATION, id);
        return intent;
    }

    public static InnovationActiviti onResult(int requestCode, int resultCode, Intent data) {
        InnovationActiviti result = null;
        if (resultCode == Activity.RESULT_OK && requestCode == REQUEST) {
            result = (InnovationActiviti) data.getSerializableExtra(EXTRA_INNOVATION);
        }
        return result;
    }

    @BindView(R.id.loadingProgress)
    ProgressBar loadingProgress;
    @BindView(R.id.container)
    ScrollView container;
    @BindView(R.id.chartsView)
    ChartsView chartsView;
    @BindView(R.id.htmlTitleView)
    HtmlTitleView htmlTitleView;
    @BindView(R.id.rating)
    RatingBar ratingBar;
    @BindView(R.id.innPointTitle)
    TextView innPointTitle;
    @BindView(R.id.buttonContainer)
    InnovationButtons innovationButtons;
    @BindView(R.id.rootConnectionError)
    View rootConnectionError;
    @BindView(R.id.root)
    View root;
    private long innovationId;

    private InnovationActiviti mInnovationActiviti;

    private SocialController socialController;

    private PostCallback postCallback = new PostCallback() {
        @Override
        public void postSuccess(Social social, @Nullable PostValue postValue) {
            SocialUIController.sendPostingResult(InnovationActivity.this, (AppPostValue) postValue, null);
        }

        @Override
        public void postFailure(Social social, @Nullable PostValue postValue, Exception e) {
            SocialUIController.sendPostingResult(InnovationActivity.this, (AppPostValue) postValue, e);
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        socialController.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_innovation);
        ButterKnife.bind(this);
        innovationButtons.setActivity(InnovationActivity.this);
        TitleHelper.setTitle(this, R.string.title_innovation);
        setListenerToRatingbar();
        socialController = new SocialController(this);
        if (getShortInnovation()) {
            loadInnovation();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        socialController.getEventController().registerCallback(postCallback);
        SocialUIController.registerPostingReceiver(this);
    }

    @OnClick(R.id.internet_lost_reload)
    public void refresh() {
        if (checkInternetConnection()) loadInnovation();
    }

    public boolean checkInternetConnection() {
        if (NetworkUtils.hasInternetConnection(this)) {
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

    @Override
    protected void onPause() {
        super.onPause();
        socialController.getEventController().unregisterAllCallback();
        SocialUIController.unregisterPostingReceiver(this);
    }

    private void setListenerToRatingbar() {
        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                innovationButtons.setSendButtonEnable(rating > 0);
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (mInnovationActiviti != null && mInnovationActiviti.isActive()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(R.string.message_innovation_has_not_mark)
                    .setPositiveButton(R.string.ag_yes, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Statistics.innovationsInterrupted();
                            GoogleStatistics.Innovation.innovationsInterrupted();
                            setResult();
                            finish();
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
            finish();
        }
    }

    private void setResult() {
        if (mInnovationActiviti != null && mInnovationActiviti.isPassed()) {
            Intent result = new Intent();
            result.putExtra(EXTRA_INNOVATION, mInnovationActiviti);
            setResult(RESULT_OK, result);
        }
    }

    private boolean getShortInnovation() {
        innovationId = getIntent().getLongExtra(EXTRA_SHORT_INNOVATION, -1);
        startFromUri();
        boolean result = innovationId != -1;
        if (!result) {
            finish();
        }
        return result;
    }

    private void loadInnovation() {
        showLoading(true);
        InnovationApiController.InnovationListener innovationListener = new InnovationApiController.InnovationListener() {
            @Override
            public void onLoaded(InnovationActiviti loadedInnovation) {
                mInnovationActiviti = loadedInnovation;
                mInnovationActiviti.setId(innovationId);
                refreshUI();
                showLoading(false);
            }

            @Override
            public void onError(VolleyError volleyError) {
                if (volleyError != null) {
                    Toast.makeText(InnovationActivity.this, volleyError.getMessage(), Toast.LENGTH_SHORT).show();
                }
                loadingProgress.setVisibility(View.GONE);
                checkInternetConnection();
            }
        };
        InnovationApiController.get(this, innovationId, innovationListener);
    }

    private void fill() {
        startProgress();
        InnovationApiController.FillNoveltyListener listener = new InnovationApiController.FillNoveltyListener() {
            @Override
            public void onSuccess(Rating rating, QuestMessage message, int allPoints) {
                mInnovationActiviti.setStatus(Status.PASSED);
                mInnovationActiviti.setRating(rating);
                mInnovationActiviti.setPassedDate(System.currentTimeMillis() / 1000L);
                refreshUI();
                stopProgress();
                showResults(message, allPoints);
                scrollToChart();
                AGApplication.bus().send(new Events.InnovationsEvents(mInnovationActiviti.getId(), rating.getFullRating(), mInnovationActiviti.getPassedDate(), Events.InnovationsEvents.PASSED_INNOVATIONS));
            }

            @Override
            public void onError(VolleyError volleyError) {
                if (volleyError != null) {
                    Toast.makeText(InnovationActivity.this, volleyError.getMessage(), Toast.LENGTH_SHORT).show();
                }
                stopProgress();
            }
        };
        InnovationApiController.fill(this, mInnovationActiviti.getId(), (int) ratingBar.getRating(), listener);
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
        if (CustomDialogController.isShareEnable(this)) {
            CustomDialogController.ActionListener listener = new CustomDialogController.ActionListener() {
                @Override
                public void onYes(Dialog dialog) {
                    dismiss(dialog);
                    SocialUIController.SocialClickListener listener = new SocialUIController.SocialClickListener() {
                        @Override
                        public void onClick(Context context, Dialog dialog, AppPostValue socialPostValue) {
                            socialPostValue.setId(mInnovationActiviti.getId());
                            if (socialController != null) {
                                socialController.post(socialPostValue, socialPostValue.getSocialId());
                            }
                        }

                        @Override
                        public void onCancel() {
                            InnovationActivity.this.finish();
                        }
                    };
                    SocialUIController.showSocialsDialogForNovelty(InnovationActivity.this, mInnovationActiviti, listener);
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
            String share = getDialogMessage(mInnovationActiviti.getPoints(), allPoints);
            CustomDialogController.showShareDialog(this, share, listener);
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(getDialogMessage(mInnovationActiviti.getPoints(), allPoints));
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
            message = PointsManager.getMessage(this, point, allPoints);
        } else {
            message = getString(R.string.novelty_result_send_share);
        }
        if (CustomDialogController.isShareEnable(this)) {
            message = message + " " + getString(R.string.share_with_friends_for_points);
        }
        return message;
    }

    private void refreshUI() {
        htmlTitleView.display(mInnovationActiviti);
        processCharts();
        processRatingBar();
        processActions();
    }

    private void processCharts() {
        chartsView.setVisibility(!mInnovationActiviti.isActive() ? View.VISIBLE : View.GONE);
        chartsView.display(mInnovationActiviti);
    }

    private void processRatingBar() {
        ratingBar.setRating(mInnovationActiviti.getRating().getUserRating());
        ratingBar.setIsIndicator(!mInnovationActiviti.isActive());
        if (mInnovationActiviti.isOld()) {
            ratingBar.setVisibility(View.GONE);
            findViewById(R.id.hint).setVisibility(View.GONE);
            findViewById(R.id.ratingDisable).setVisibility(View.GONE);
        }
    }

    private void processActions() {
        int visibility = View.VISIBLE;
        int innerPointsVisibility = View.GONE;
        String innPointTitleTxt = "";
        innovationButtons.setSendButtonDisabled();
        switch (mInnovationActiviti.getStatus()) {
            case ACTIVE:
                if (mInnovationActiviti.getPoints() > 0) {
                    String suitableString = PointsManager.getSuitableString(this, R.array.survey_points_pluse, mInnovationActiviti.getPoints());
                    innPointTitleTxt = String.format(suitableString, mInnovationActiviti.getPoints());
                    innerPointsVisibility = View.VISIBLE;
                }
                break;
            case PASSED:
                String pointTxt = PointsManager.getPointUnitString(this, mInnovationActiviti.getPoints());
                innovationButtons.renderPassedButton();
                if (mInnovationActiviti.getPoints() > 0) {
                    String pointsAdded = getResources().getQuantityString(R.plurals.points_added, mInnovationActiviti.getPoints());
                    innPointTitleTxt = String.format(getString(R.string.passed_points_formatted), pointsAdded, String.valueOf(mInnovationActiviti.getPoints()), pointTxt, mInnovationActiviti.getReadablePassedDate());
                    innerPointsVisibility = View.VISIBLE;
                } else {
                    innPointTitleTxt = String.format(getString(R.string.vote_date_text), mInnovationActiviti.getReadablePassedDate());
                    innerPointsVisibility = View.VISIBLE;
                }
                break;
            case OLD:
                innPointTitleTxt = getString(R.string.innovation_ended) + " " + mInnovationActiviti.getReadableEndDate();
                innerPointsVisibility = View.VISIBLE;
                innPointTitle.setTextColor(getResources().getColor(R.color.novelty_list_rate));
                visibility = View.GONE;
                break;
        }
        innPointTitle.setText(innPointTitleTxt);
        innPointTitle.setVisibility(innerPointsVisibility);
        findViewById(R.id.buttonContainer).setVisibility(visibility);
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
                InnovationActivity.this.finish();
            }
        };
        SocialUIController.showSocialsDialogForNovelty(InnovationActivity.this, mInnovationActiviti, socialClickListener);
    }

    private void startFromUri() {
        UrlSchemeController.startNovelty(this, new UrlSchemeController.IdListener() {
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
