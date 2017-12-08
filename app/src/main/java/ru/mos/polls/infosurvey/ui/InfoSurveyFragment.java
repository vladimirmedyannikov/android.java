package ru.mos.polls.infosurvey.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.AppCompatCheckBox;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.CardView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;


import com.appsflyer.AppsFlyerLib;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import ru.mos.polls.R;
import ru.mos.polls.helpers.AppsFlyerConstants;
import ru.mos.polls.survey.SharedPreferencesSurveyManager;
import ru.mos.polls.survey.Survey;
import ru.mos.polls.survey.SurveyActivity;
import ru.mos.polls.survey.SurveyButtons;
import ru.mos.polls.survey.SurveyFragment;
import ru.mos.polls.survey.VerificationException;
import ru.mos.polls.survey.questions.RadioboxSurveyQuestion;
import ru.mos.polls.survey.questions.SurveyQuestion;
import ru.mos.polls.survey.source.SurveyDataSource;
import ru.mos.polls.survey.variants.SurveyVariant;

/**
 * Created by Trunks on 06.12.2017.
 */

public class InfoSurveyFragment extends Fragment implements SurveyActivity.Callback {
    public static final String ARG_POLL_ID = "poll_id";
    public static final String ARG_SURVEY = "survey";


    private Unbinder unbinder;
    private Survey survey;
    private SurveyDataSource surveyDataSource;

    private SharedPreferencesSurveyManager manager;
    private long questionId;
    private long pollId;
    private int requestCode;


    @BindView(R.id.info_survey_comment_card)
    CardView infoCommentCard;
    @BindView(R.id.info_survey_title)
    AppCompatTextView infoTitle;
    @BindView(R.id.info_survey_description)
    AppCompatTextView infoDesc;
    @BindView(R.id.info_survey_comment)
    AppCompatTextView infoComment;

    @BindView(R.id.info_like_img)
    AppCompatCheckBox likeImage;

    @BindView(R.id.info_dislike_img)
    AppCompatCheckBox dislikeImage;
    @BindView(R.id.like_title)
    AppCompatTextView likeTitle;
    @BindView(R.id.dislike_title)
    AppCompatTextView dislikeTitle;

    @BindView(R.id.shareButton)
    Button mSurveyButtons;

    RadioboxSurveyQuestion surveyQuestion;

    private SurveyFragment.Callback callback = SurveyFragment.Callback.STUB;
    InfoCommentFragment commentFragment;
    boolean isCommentFrAdded;

    AppCompatTextView likeCount, dislikeCount;


    public static InfoSurveyFragment newInstance(Survey survey, long idPoll) {
        InfoSurveyFragment f = new InfoSurveyFragment();
        Bundle args = new Bundle();
        args.putLong(ARG_POLL_ID, idPoll);
        args.putSerializable(ARG_SURVEY, survey);
        f.setArguments(args);
        return f;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View result = View.inflate(getActivity(), R.layout.fragment_info_survey, null);
        unbinder = ButterKnife.bind(this, result);
        return result;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (unbinder != null) {
            unbinder.unbind();
        }
    }
    public void setCallback(SurveyFragment.Callback c) {
        if (c == null) {
            callback = SurveyFragment.Callback.STUB;
        } else {
            callback = c;
        }
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Bundle extras = getArguments();
        if (extras != null) {
            survey = (Survey) extras.getSerializable(InfoSurveyFragment.ARG_SURVEY);
            pollId = extras.getLong(InfoSurveyFragment.ARG_POLL_ID);
            surveyQuestion = (RadioboxSurveyQuestion) survey.getFirstNotCheckedQuestion();
        }
        manager = new SharedPreferencesSurveyManager(getActivity());
        setCheckboxDrawable();
        setInfoTitle();
        setInfoDesc();

        setListeners();
    }

    public void setListeners() {
        likeImage.setOnCheckedChangeListener(getCheckboxListener());
        dislikeImage.setOnCheckedChangeListener(getCheckboxListener());
    }


    public CompoundButton.OnCheckedChangeListener getCheckboxListener() {
        CompoundButton.OnCheckedChangeListener listener = (buttonView, isChecked) -> {
            SurveyVariant surveyVariant1 = surveyQuestion.getVariantsList().get(0);
            SurveyVariant surveyVariant2 = surveyQuestion.getVariantsList().get(1);
            switch (buttonView.getId()) {
                case R.id.info_like_img:
                    setLikeTitleColor(likeTitle, isChecked, R.color.green_light);
                    dislikeImage.setChecked(!isChecked);
                    surveyVariant1.setChecked(true);
                    surveyVariant2.setChecked(false);
                    break;
                case R.id.info_dislike_img:
                    setLikeTitleColor(dislikeTitle, isChecked, R.color.red);
                    likeImage.setChecked(!isChecked);
                    surveyVariant2.setChecked(true);
                    surveyVariant1.setChecked(false);
                    break;
            }
        };
        return listener;
    }

    @OnClick(R.id.shareButton)
    public void onShateButtonClick() {
        manager.fill(survey);
        callback.onSurveyDone(survey);
    }

    @OnClick(R.id.info_survey_comment_card)
    public void onCommentClick() {
        commentFragment = new InfoCommentFragment();
        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager
                .beginTransaction()
                .add(R.id.container, commentFragment)
                .addToBackStack(InfoCommentFragment.INFO_COMMENT_FRAGMENT_TAG)
                .commit();
        isCommentFrAdded = true;
    }

    public void setLikeTitleColor(AppCompatTextView view, boolean isChecked, int color) {
        view.setTextColor(isChecked ? getResources().getColor(color) : getResources().getColor(R.color.gray_light));
    }

    public void setCheckboxDrawable() {
        likeImage.setButtonDrawable(R.drawable.ic_like_selector);
        dislikeImage.setButtonDrawable(R.drawable.ic_dislike_selector);
    }

    public void setInfoTitle() {
        infoTitle.setText(survey.getTitle());
    }

    public void setInfoDesc() {
        infoDesc.setText(Html.fromHtml(survey.getTextShortHtml()));
    }

    @Override
    public void onBackPressed() {
        if (isCommentFrAdded) {
            commentFragment.onBackPressed();
        } else {
            interrupt();
        }
    }

    @Override
    public void onUpPressed() {
        interruptUp();
    }

    @Override
    public void onLocationUpdated() {

    }

    public void interrupt() {
        if (survey != null) {
            if (survey.getKind().isHearing()) {
                callback.onSurveyInterrupted(survey);
            } else {
                try {
                    survey.verify();
                    survey.getQuestion(survey.getCurrentQuestionId()).setPassed(true);
                } catch (VerificationException ignored) {
                }
                survey.endTiming();
                manager.saveCurrentPage(survey);
                callback.onSurveyInterrupted(survey);
            }
        }
    }

    public void interruptUp() {
        if (survey != null) {
            if (survey.getKind().isHearing()) {
                callback.onSurveyInterrupted(survey);
                ((SurveyActivity) getActivity()).getSummaryFragmentCallback().onSurveyInterrupted(survey);
            } else {
                try {
                    survey.verify();
                    survey.getQuestion(survey.getCurrentQuestionId()).setPassed(true);
                } catch (VerificationException ignored) {
                }
                survey.endTiming();
                manager.saveCurrentPage(survey);
                ((SurveyActivity) getActivity()).getSummaryFragmentCallback().onSurveyInterrupted(survey);
            }
        }
    }

    /**
     * Если пользователь дал ответ на вопрос, то сохраняем его
     */
    private void saveAnswer() {
        if (survey != null) {
            if (survey.isActive()) {
                try {
                    survey.verify();
                    survey.getQuestion(survey.getCurrentQuestionId()).setPassed(true);
                    AppsFlyerLib.sendTrackingWithEvent(getActivity(), AppsFlyerConstants.QUESTION_ANSWERED, String.valueOf(survey.getCurrentQuestionId()));
                    survey.endTiming();
                    manager.saveCurrentPage(survey);
                } catch (VerificationException ignored) {
                }
            }
        }
    }

    private boolean isSurveyVerifyOk() {
        List<SurveyQuestion> questionList = survey.getFilteredQuestionList();
        boolean isAllQuestionHasAnswer = true;
        for (SurveyQuestion question : questionList) {
            try {
                question.verify();
            } catch (VerificationException e) {
                isAllQuestionHasAnswer = false;
            }
        }
        return isAllQuestionHasAnswer;
    }
}
