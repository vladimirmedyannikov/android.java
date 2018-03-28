package ru.mos.polls.survey.hearing.vm;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.TextView;

import java.text.SimpleDateFormat;

import ru.mos.polls.R;
import ru.mos.polls.base.activity.BaseActivity;
import ru.mos.polls.base.component.UIComponentFragmentViewModel;
import ru.mos.polls.base.component.UIComponentHolder;
import ru.mos.polls.databinding.FragmentMeetingBinding;
import ru.mos.polls.profile.vm.PguAuthFragmentVM;
import ru.mos.polls.survey.hearing.controller.PguUIController;
import ru.mos.polls.survey.hearing.gui.fragment.MeetingFragment;
import ru.mos.polls.survey.hearing.model.Meeting;

public class MeetingFragmentVM extends UIComponentFragmentViewModel<MeetingFragment, FragmentMeetingBinding>{
    private static final int REQUEST = 1001;

    private TextView address;
    private TextView date;
    private TextView description;
    private TextView conditions;
    private TextView checkHearing;
    private TextView hintWithoutRegistration;

    private String surveyTitle;
    private long hearingId;
    private Meeting meeting;

    private boolean isSubscribed;

    public static boolean isSubscribe(int resultCode, int requestCode) {
        return resultCode == Activity.RESULT_OK && requestCode == REQUEST;
    }

    public MeetingFragmentVM(MeetingFragment fragment, FragmentMeetingBinding binding) {
        super(fragment, binding);
    }

    @Override
    protected UIComponentHolder createComponentHolder() {
        return new UIComponentHolder.Builder().build();
    }

    @Override
    protected void initialize(FragmentMeetingBinding binding) {
        address = binding.address;
        date = binding.date;
        description = binding.description;
        conditions = binding.condition;
        checkHearing = binding.checkHearing;
        hintWithoutRegistration = binding.hintWithoutRegistration;
        surveyTitle = getFragment().getExtraTitle();
        hearingId = getFragment().getExtraHearingId();
        meeting = getFragment().getExtraMeeting();
        checkHearing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PguUIController.showHearingSubscribe((BaseActivity) getActivity(), surveyTitle, hearingId, meeting.getId());
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (PguAuthFragmentVM.isAuth(resultCode, requestCode, data)) {
            meeting.setStatus(Meeting.Status.REGISTERED);
            isSubscribed = true;
            refreshAction();
        }
    }

    @Override
    public void onViewCreated() {
        super.onViewCreated();
        refreshUI();
    }

    public int getResult() {
        return isSubscribed ? Activity.RESULT_OK : Activity.RESULT_CANCELED;
    }

    protected void refreshUI() {
        displayAddress();
        displayConditions();
        displayDescription();
        displayDate();
        refreshAction();
    }

    protected void displayAddress() {
        String value = meeting.getAddress();
        if (value != null && !TextUtils.isEmpty(value)) {
            address.setText(meeting.getAddress());
            address.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    meeting.getPosition().goToGoogleMapsOnly(getFragment().getContext(), meeting.getAddress());
                }
            });
        }
    }

    protected void displayDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
        date.setText(String.format("%s, %s", sdf.format(meeting.getDate()), meeting.getWorkingHours()));
    }

    private void refreshAction() {
        if (meeting.getDate() < System.currentTimeMillis() || meeting.isClosed() && !meeting.getConditions().isWithoutRegistration()) {
            setButtonDisabled(getFragment().getString(R.string.meeting_held));
        } else if (meeting.isRegistered()) {
            checkHearing.setEnabled(false);
            checkHearing.setText(getFragment().getString(R.string.you_singin_meeting));
        } else if (meeting.getConditions().isWithoutRegistration()) {
            setButtonDisabled(getFragment().getString(R.string.free_entrace));
        } else if (meeting.getEndRegistration() < System.currentTimeMillis()) {
            setButtonDisabled(getFragment().getString(R.string.signin_closed));
        } else if (meeting.isPreview()) {
            setButtonDisabled(getFragment().getString(R.string.meeting_preview));
        } else {
            checkHearing.setEnabled(true);
            checkHearing.setText(R.string.signin_to_meeting);
        }
    }

    private void setButtonDisabled(String string) {
        hintWithoutRegistration.setVisibility(View.VISIBLE);
        hintWithoutRegistration.setText(string);
        hintWithoutRegistration.setTextColor(getFragment().getResources().getColor(R.color.ag_grey_color));
        checkHearing.setVisibility(View.GONE);
    }

    private void displayConditions() {
        if (meeting.getConditions().isMandatoryRegistration()) {
            conditions.setTextColor(getFragment().getResources().getColor(R.color.ag_red));
        }
        conditions.setText(meeting.getConditions().getLabel());
    }

    private void displayDescription() {
        description.setText(meeting.getDescription());
    }

    private SpannableString getFormattedSpannable(String formatted, String condition) {
        SpannableString result = new SpannableString(String.format(formatted, condition));
        result.setSpan(new ForegroundColorSpan(Color.RED), formatted.length() - 3, result.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        return result;
    }

    public boolean isLast3Days() {
        return daysBetween(System.currentTimeMillis(), meeting.getEndRegistration()) <= 3;
    }

    public int daysBetween(long begin, long end) {
        return (int) ((end - begin) / (1000 * 60 * 60 * 24));
    }
}
