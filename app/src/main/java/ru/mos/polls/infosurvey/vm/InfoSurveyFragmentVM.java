package ru.mos.polls.infosurvey.vm;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.CardView;

import ru.mos.polls.R;
import ru.mos.polls.base.component.ProgressableUIComponent;
import ru.mos.polls.base.component.UIComponentFragmentViewModel;
import ru.mos.polls.base.component.UIComponentHolder;
import ru.mos.polls.databinding.FragmentInfoSurveyBinding;
import ru.mos.polls.infosurvey.ui.InfoCommentFragment;
import ru.mos.polls.infosurvey.ui.InfoSurveyFragment;
import ru.mos.polls.survey.Survey;
import ru.mos.polls.survey.SurveyActivity;

/**
 * Created by Trunks on 06.12.2017.
 */

public class InfoSurveyFragmentVM  {
    CardView infoCommentCard;
    AppCompatTextView infoTitle, infoDesc, infoComment;
    Survey survey;
    private long questionId;
    private long pollId;
    AppCompatTextView likeCount, dislikeCount, likeTitle, dislikeTitle;
    AppCompatImageView likeImage, dislikeImage;


//    public InfoSurveyFragmentVM(InfoSurveyFragment fragment, FragmentInfoSurveyBinding binding) {
//        super(fragment, binding);
//    }
//
//    @Override
//    protected void initialize(FragmentInfoSurveyBinding binding) {
//        Bundle extras = getFragment().getArguments();
//        if (extras != null) {
//            survey = (Survey) extras.getSerializable(InfoSurveyFragment.ARG_SURVEY);
//            pollId = extras.getLong(InfoSurveyFragment.ARG_POLL_ID);
//        }
//        infoCommentCard = binding.infoSurveyCommentCard;
//        infoTitle = binding.infoSurveyTitle;
//        infoDesc = binding.infoSurveyDescription;
//        infoComment = binding.infoSurveyComment;
//        likeCount = binding.likeCount;
//        dislikeCount = binding.dislikeCount;
//        likeTitle = binding.likeTitle;
//        dislikeTitle = binding.dislikeTitle;
//        likeImage = binding.infoLikeImg;
//        dislikeImage = binding.infoDislikeImg;
//    }
//
//    @Override
//    protected UIComponentHolder createComponentHolder() {
//        ProgressableUIComponent progressableUIComponent = new ProgressableUIComponent();
//        return new UIComponentHolder.Builder().with(progressableUIComponent).build();
//    }
//
//    @Override
//    public void onViewCreated() {
//        super.onViewCreated();
//        setListeners();
//    }
//
//    @Override
//    public void onResume() {
//        super.onResume();
//    }
//
//    public void setView() {
//
//    }
//
//    public void setInfoTitle() {
//        infoTitle.setText(survey.getTitle());
//    }
//
//    public void setInfoDesc() {
//        infoDesc.setText(survey.getTextShortHtml());
//    }
//
//    public void setInfoComment() {
//        String comment = String.format((getActivity().getString(R.string.comment) + ":%s "), " какой то");
//        infoComment.setText(comment);
//    }
//
//    public void setListeners() {
//        infoCommentCard.setOnClickListener(v -> {
//            Fragment fragment = new InfoCommentFragment();
//            FragmentManager fragmentManager = getFragment().getFragmentManager();
//            fragmentManager
//                    .beginTransaction()
//                    .replace(R.id.container, fragment)
//                    .addToBackStack("infosurvey")
//                    .commit();
//        });
//    }
//
//    @Override
//    public void onBackPressed() {
//
//    }
//
//    @Override
//    public void onUpPressed() {
//
//    }
//
//    @Override
//    public void onLocationUpdated() {
//
//    }
}
