package ru.mos.polls.infosurvey.ui;

import android.app.Dialog;
import android.content.Context;
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
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import ru.mos.elk.BaseActivity;
import ru.mos.polls.AGApplication;
import ru.mos.polls.R;
import ru.mos.polls.base.rxjava.Events;
import ru.mos.polls.base.rxjava.RxEventDisposableSubscriber;
import ru.mos.polls.helpers.AppsFlyerConstants;
import ru.mos.polls.social.controller.SocialUIController;
import ru.mos.polls.social.model.AppPostValue;
import ru.mos.polls.survey.SharedPreferencesSurveyManager;
import ru.mos.polls.survey.Survey;
import ru.mos.polls.survey.SurveyActivity;
import ru.mos.polls.survey.SurveyFragment;
import ru.mos.polls.survey.VerificationException;
import ru.mos.polls.survey.questions.CheckboxSurveyQuestion;
import ru.mos.polls.survey.questions.RadioboxSurveyQuestion;
import ru.mos.polls.survey.questions.SurveyQuestion;
import ru.mos.polls.survey.source.SurveyDataSource;
import ru.mos.polls.survey.variants.InputSurveyVariant;
import ru.mos.polls.survey.variants.SurveyVariant;
import ru.mos.polls.survey.variants.values.CharVariantValue;

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

    @BindView(R.id.like_count)
    AppCompatTextView likeCount;

    @BindView(R.id.dislike_count)
    AppCompatTextView dislikeCount;

    RadioboxSurveyQuestion surveyQuestion;
    CheckboxSurveyQuestion checkboxSurveyQuestion;
    private SurveyFragment.Callback callback = SurveyFragment.Callback.STUB;
    InfoCommentFragment commentFragment;
    boolean isCommentFrAdded;

    Disposable disposable;

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
            checkboxSurveyQuestion = (CheckboxSurveyQuestion) survey.getQuestionsList().get(1);
        }
        manager = new SharedPreferencesSurveyManager(getActivity());
        setCheckboxDrawable();
        setInfoTitle();
        setInfoDesc();
        subscribeEventsBus();
        setShareButtonView();
        setListeners();
        setSurveyAnswer();
        setLikeButtonView();
    }

    public void setLikeButtonView() {
        if (survey.isPassed() || survey.isOld()) {
            setLikeTitleColor(likeTitle, true, R.color.green_light);
            likeImage.setChecked(true);
            likeImage.setClickable(false);
            setLikeTitleColor(dislikeTitle, true, R.color.red);
            dislikeImage.setChecked(true);
            dislikeImage.setClickable(false);
        }
    }

    public void setSurveyAnswer() {
        InputSurveyVariant commentSurveyVariant = (InputSurveyVariant) checkboxSurveyQuestion.getVariantsList().get(1);
        if (commentSurveyVariant.isChecked()) {
            CharVariantValue charVariantValue = (CharVariantValue) commentSurveyVariant.input;
            if (!charVariantValue.isEmpty()) {
                infoComment.setText(String.format(getString(R.string.your_comment), charVariantValue.getValue()));
            }
        }
    }

    public void setShareButtonView() {
        if (survey.isActive() || survey.isInterrupted()) {
            mSurveyButtons.setText(getString(R.string.result_innovation));
        } else {
            mSurveyButtons.setText(getString(R.string.share));
            mSurveyButtons.setEnabled(true);
        }
    }

    @Override
    public void onDestroy() {
        disposable.dispose();
        super.onDestroy();
    }

    private void subscribeEventsBus() {
        disposable = AGApplication.bus().toObserverable()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new RxEventDisposableSubscriber() {
                    @Override
                    public void onNext(Object o) {
                        if (o instanceof Events.InfoSurveyEvents) {
                            Events.InfoSurveyEvents events = (Events.InfoSurveyEvents) o;
                            infoComment.setText(String.format(getString(R.string.your_comment), events.getComment()));
                            InputSurveyVariant numberSurveyVariant = (InputSurveyVariant) checkboxSurveyQuestion.getVariantsList().get(0);
                            InputSurveyVariant commentSurveyVariant = (InputSurveyVariant) checkboxSurveyQuestion.getVariantsList().get(1);
                            setCharValue(numberSurveyVariant, events.getNumber());
                            setCharValue(commentSurveyVariant, events.getComment());
                            isCommentFrAdded = false;
                        }
                    }
                });
    }

    public void setCharValue(InputSurveyVariant inputSurveyVariant, String value) {
        inputSurveyVariant.setChecked(true);
        CharVariantValue charVariantValue = (CharVariantValue) inputSurveyVariant.input;
        charVariantValue.setValue(value);
    }

    public void setListeners() {
        if (survey.isActive() || survey.isInterrupted()) {
            likeImage.setOnCheckedChangeListener(getCheckboxListener());
            dislikeImage.setOnCheckedChangeListener(getCheckboxListener());
        }
    }


    public CompoundButton.OnCheckedChangeListener getCheckboxListener() {
        CompoundButton.OnCheckedChangeListener listener = (buttonView, isChecked) -> {
            SurveyVariant surveyVariant1 = surveyQuestion.getVariantsList().get(0);
            SurveyVariant surveyVariant2 = surveyQuestion.getVariantsList().get(1);
            mSurveyButtons.setEnabled(true);
            switch (buttonView.getId()) {
                case R.id.info_like_img:
                    setLikeTitleColor(likeTitle, isChecked, R.color.green_light);
                    setVariantAnswer(dislikeImage, isChecked, surveyVariant1, surveyVariant2);
                    break;
                case R.id.info_dislike_img:
                    setLikeTitleColor(dislikeTitle, isChecked, R.color.red);
                    setVariantAnswer(likeImage, isChecked, surveyVariant2, surveyVariant1);
                    break;
            }
        };
        return listener;
    }

    public void setVariantAnswer(AppCompatCheckBox view, boolean isChecked, SurveyVariant surveyVariant1, SurveyVariant surveyVariant2) {
        view.setChecked(!isChecked);
        surveyVariant1.setChecked(isChecked);
        surveyVariant2.setChecked(!isChecked);
    }

    @OnClick(R.id.shareButton)
    public void onShareButtonClick() {
        if (survey.isActive() || survey.isInterrupted()) {
            saveAnswer();
            manager.fill(survey);
            callback.onSurveyDone(survey);
        } else {
            SocialUIController.SocialClickListener listener = new SocialUIController.SocialClickListener() {
                @Override
                public void onClick(Context context, Dialog dialog, AppPostValue socialPostValue) {
                    socialPostValue.setId(survey.getId());
                    callback.onPosting(socialPostValue);
                }

                @Override
                public void onCancel() {
                    getActivity().finish();
                }
            };
            SocialUIController.showSocialsDialogForPoll((BaseActivity) getActivity(), survey, false, listener);
        }
    }

    @OnClick(R.id.info_survey_comment_card)
    public void onCommentClick() {
        commentFragment = InfoCommentFragment.newInstance(survey);
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
            isCommentFrAdded = false;
        } else {
            interrupt();
        }
    }

    @Override
    public void onUpPressed() {
        interrupt();
    }

    @Override
    public void onLocationUpdated() {

    }

    public void interrupt() {
        if (survey != null) {
            try {
                survey.verify();
                survey.getQuestion(survey.getCurrentQuestionId()).setPassed(true);
            } catch (VerificationException ignored) {
            }
            survey.endTiming();
            manager.saveCurrentPage(survey);
            ((SurveyActivity) getActivity()).doInterrupt(survey);
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
