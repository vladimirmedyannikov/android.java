package ru.mos.polls.innovation.gui.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley2.VolleyError;

import butterknife.BindView;
import butterknife.ButterKnife;
import ru.mos.polls.CustomDialogController;
import ru.mos.polls.GoogleStatistics;
import ru.mos.polls.PointsManager;
import ru.mos.polls.R;
import ru.mos.polls.Statistics;
import ru.mos.polls.ToolbarAbstractActivity;
import ru.mos.polls.common.controller.UrlSchemeController;
import ru.mos.polls.common.model.QuestMessage;
import ru.mos.polls.common.view.HtmlTitleView;
import ru.mos.polls.helpers.TitleHelper;
import ru.mos.polls.innovation.controller.InnovationApiController;
import ru.mos.polls.innovation.model.Innovation;
import ru.mos.polls.innovation.model.InnovationButtons;
import ru.mos.polls.innovation.model.Rating;
import ru.mos.polls.innovation.model.ShortInnovation;
import ru.mos.polls.innovation.model.Status;
import ru.mos.polls.innovation.view.ChartsView;
import ru.mos.polls.social.controller.SocialController;
import ru.mos.polls.social.controller.SocialUIController;
import ru.mos.polls.social.model.SocialPostValue;

/**
 * Экран для отображения городской новинки
 *
 * @since 1.9
 */
public class InnovationActivity extends ToolbarAbstractActivity implements InnovationButtons.CallBack {
    private static final int REQUEST = 100;
    private static final String EXTRA_SHORT_INNOVATION = "extra_short_innovation";
    private static final String EXTRA_INNOVATION = "extra_innovation";

    public static void startActivity(Fragment fragment, ShortInnovation shortInnovation) {
        Intent start = new Intent(fragment.getActivity(), InnovationActivity.class);
        start.putExtra(EXTRA_SHORT_INNOVATION, shortInnovation.getId());
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

    public static Innovation onResult(int requestCode, int resultCode, Intent data) {
        Innovation result = null;
        if (resultCode == Activity.RESULT_OK && requestCode == REQUEST) {
            result = (Innovation) data.getSerializableExtra(EXTRA_INNOVATION);
        }
        return result;
    }

    @BindView(R.id.content)
    View content;
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
    @BindView(R.id.reloadInnDetails)
    Button reloadInnDetails;
    @BindView(R.id.buttonContainer)
    InnovationButtons innovationButtons;


    private long innovationId;

    private Innovation innovation;

    private SocialController socialController;

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
        SocialUIController.registerPostingReceiver(this);
        if (getShortInnovation()) {
            loadInnovation();
        }
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
    protected void onDestroy() {
        try {
            SocialUIController.unregisterPostingReceiver(this);
        } catch (Exception ignored) {
        }
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        if (innovation != null && innovation.isActive()) {
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
        if (innovation != null && innovation.isPassed()) {
            Intent result = new Intent();
            result.putExtra(EXTRA_INNOVATION, innovation);
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
            public void onLoaded(Innovation loadedInnovation) {
                innovation = loadedInnovation;
                innovation.setId(innovationId);
                refreshUI();
                showLoading(false);
            }

            @Override
            public void onError(VolleyError volleyError) {
                if (volleyError != null) {
                    Toast.makeText(InnovationActivity.this, volleyError.getMessage(), Toast.LENGTH_SHORT).show();
                }
                loadingProgress.setVisibility(View.GONE);
                content.setVisibility(View.GONE);
                reloadInnDetails.setVisibility(View.VISIBLE);
            }
        };
        InnovationApiController.get(this, innovationId, innovationListener);
    }

    private void fill() {
        startProgress();
        InnovationApiController.FillNoveltyListener listener = new InnovationApiController.FillNoveltyListener() {
            @Override
            public void onSuccess(Rating rating, QuestMessage message, int allPoints) {
                innovation.setStatus(Status.PASSED);
                innovation.setRating(rating);
                innovation.setPassedDate(System.currentTimeMillis());
                refreshUI();
                stopProgress();
                showResults(message, allPoints);
                scrollToChart();
            }

            @Override
            public void onError(VolleyError volleyError) {
                if (volleyError != null) {
                    Toast.makeText(InnovationActivity.this, volleyError.getMessage(), Toast.LENGTH_SHORT).show();
                }
                stopProgress();
            }
        };
        InnovationApiController.fill(this, innovation.getId(), (int) ratingBar.getRating(), listener);
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
                        public void onClick(Context context, Dialog dialog, SocialPostValue socialPostValue) {
                            socialPostValue.setId(innovation.getId());
                            if (socialController != null) {
                                socialController.post(socialPostValue);
                            }
                        }

                        @Override
                        public void onCancel() {
                            InnovationActivity.this.finish();
                        }
                    };
                    SocialUIController.showSocialsDialogForNovelty(InnovationActivity.this, innovation, listener);
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
            String share = getDialogMessage(innovation.getPoints(), allPoints);
            CustomDialogController.showShareDialog(this, share, listener);
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(getDialogMessage(innovation.getPoints(), allPoints));
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
            String pointTxt = PointsManager.getPointUnitString(this, point);
            message = String.format(getString(R.string.novelty_result_send_share_with_points), point, pointTxt, allPoints, PointsManager.getPointUnitString(this, allPoints));
        } else {
            message = getString(R.string.novelty_result_send_share);
        }
        if (CustomDialogController.isShareEnable(this)) {
            message = message + " " + getString(R.string.share_with_friends_for_points);
        }
        return message;
    }

    private void refreshUI() {
        htmlTitleView.display(innovation);
        processCharts();
        processRatingBar();
        processActions();
    }

    private void processCharts() {
        chartsView.setVisibility(!innovation.isActive() ? View.VISIBLE : View.GONE);
        chartsView.display(innovation);
    }

    private void processRatingBar() {
        ratingBar.setRating(innovation.getRating().getUserRating());
        ratingBar.setIsIndicator(!innovation.isActive());
        if (innovation.isOld()) {
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
        switch (innovation.getStatus()) {
            case ACTIVE:
                if (innovation.getPoints() > 0) {
                    String suitableString = PointsManager.getSuitableString(this, R.array.survey_points_pluse, innovation.getPoints());
                    innPointTitleTxt = String.format(suitableString, innovation.getPoints());
                    innerPointsVisibility = View.VISIBLE;
                }
                break;
            case PASSED:
                String pointTxt = PointsManager.getPointUnitString(this, innovation.getPoints());
                innovationButtons.renderPassedButton();
                if (innovation.getPoints() > 0) {
                    String pointsAdded = getResources().getQuantityString(R.plurals.points_added, innovation.getPoints());
                    innPointTitleTxt = String.format(getString(R.string.passed_points_formatted), pointsAdded, innovation.getPoints(), pointTxt, innovation.getReadablePassedDate());
                    innerPointsVisibility = View.VISIBLE;
                } else {
                    innPointTitleTxt = String.format(getString(R.string.vote_date_text), innovation.getReadablePassedDate());
                    innerPointsVisibility = View.VISIBLE;
                }
                break;
            case OLD:
                innPointTitleTxt = getString(R.string.innovation_ended) + " " + innovation.getReadableEndDate();
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
            public void onClick(Context context, Dialog dialog, SocialPostValue socialPostValue) {
                socialController.post(socialPostValue);
                scrollToChart();
            }

            @Override
            public void onCancel() {
                InnovationActivity.this.finish();
            }
        };
        SocialUIController.showSocialsDialogForNovelty(InnovationActivity.this, innovation, socialClickListener);
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
        content.setVisibility(show ? View.GONE : View.VISIBLE);
        loadingProgress.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    public void reloadContent(View view) {
        reloadInnDetails.setVisibility(View.GONE);
        loadInnovation();
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
