package ru.mos.polls.survey.hearing.gui.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.TextView;

import java.text.SimpleDateFormat;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import ru.mos.polls.R;
import ru.mos.polls.ToolbarAbstractActivity;
import ru.mos.polls.helpers.TitleHelper;
import ru.mos.polls.survey.hearing.controller.PguUIController;
import ru.mos.polls.survey.hearing.model.Meeting;

/**
 * Экран для отображения данных по собранию публичного слушания
 *
 * @since 2.0
 */
public class MeetingActivity extends ToolbarAbstractActivity {
    private static final int REQUEST = 1001;
    private static final String EXTRA_TITLE = "extra_title";
    private static final String EXTRA_MEETING = "extra_meeting";
    private static final String EXTRA_HEARING_ID = "extra_hearing_id";

    public static void start(Fragment fragment, long hearingId, Meeting meeting, String surveyTitle) {
        Intent start = new Intent(fragment.getActivity(), MeetingActivity.class);
        start.putExtra(EXTRA_TITLE, surveyTitle);
        start.putExtra(EXTRA_HEARING_ID, hearingId);
        start.putExtra(EXTRA_MEETING, meeting);
        fragment.startActivityForResult(start, REQUEST);
    }

    @BindView(R.id.address)
    TextView address;
    @BindView(R.id.date)
    TextView date;
    @BindView(R.id.description)
    TextView description;
    @BindView(R.id.condition)
    TextView conditions;
    @BindView(R.id.checkHearing)
    TextView checkHearing;
    @BindView(R.id.hintWithoutRegistration)
    TextView hintWithoutRegistration;

    private String surveyTitle;
    private long hearingId;
    private Meeting meeting;

    private boolean isSubscribed;

    public static boolean isSubscribe(int resultCode, int requestCode) {
        return resultCode == RESULT_OK && requestCode == REQUEST;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (PguVerifyActivity.isAuth(resultCode, requestCode, data)) {
            meeting.setStatus(Meeting.Status.REGISTERED);
            isSubscribed = true;
            refreshAction();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meeting);
        ButterKnife.bind(this);
        getParams();
        refreshUI();
    }

    @Override
    protected void findViews() {
        TitleHelper.setTitle(this, getString(R.string.title_meeting));
    }

    @OnClick(R.id.checkHearing)
    void setCheckHearing() {
        PguUIController.showHearingSubscribe(MeetingActivity.this, surveyTitle, hearingId, meeting.getId());
    }

    protected void getParams() {
        surveyTitle = getIntent().getStringExtra(EXTRA_TITLE);
        meeting = (Meeting) getIntent().getSerializableExtra(EXTRA_MEETING);
        hearingId = getIntent().getLongExtra(EXTRA_HEARING_ID, -1);
    }

    @Override
    public void onBackPressed() {
        setResult(isSubscribed ? RESULT_OK : RESULT_CANCELED);
        finish();
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
                    meeting.getPosition().goToGoogleMapsOnly(MeetingActivity.this, meeting.getAddress());
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
            hintWithoutRegistration.setVisibility(View.VISIBLE);
            hintWithoutRegistration.setText(getString(R.string.meeting_held));
            hintWithoutRegistration.setTextColor(getResources().getColor(R.color.ag_grey_color));
            checkHearing.setVisibility(View.GONE);
        } else if (meeting.isRegistered()) {
            checkHearing.setEnabled(false);
            checkHearing.setText(getString(R.string.you_singin_meeting));
        } else if (meeting.getConditions().isWithoutRegistration()) {
            hintWithoutRegistration.setVisibility(View.VISIBLE);
            hintWithoutRegistration.setText(getString(R.string.free_entrace));
            hintWithoutRegistration.setTextColor(getResources().getColor(R.color.ag_grey_color));
            checkHearing.setVisibility(View.GONE);
        } else if (meeting.getEndRegistration() < System.currentTimeMillis()) {
            hintWithoutRegistration.setVisibility(View.VISIBLE);
            hintWithoutRegistration.setText(getString(R.string.signin_closed));
            hintWithoutRegistration.setTextColor(getResources().getColor(R.color.ag_grey_color));
            checkHearing.setVisibility(View.GONE);
        } else {
            checkHearing.setEnabled(true);
            checkHearing.setText(R.string.signin_to_meeting);
        }
    }

    private void displayConditions() {
        if (meeting.getConditions().isMandatoryRegistration()) {
            conditions.setTextColor(getResources().getColor(R.color.ag_red));
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
