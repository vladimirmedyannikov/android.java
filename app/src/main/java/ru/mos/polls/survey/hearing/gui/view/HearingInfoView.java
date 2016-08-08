package ru.mos.polls.survey.hearing.gui.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.OnClick;
import ru.mos.polls.R;
import ru.mos.polls.survey.Survey;
import ru.mos.polls.survey.hearing.model.Exposition;
import ru.mos.polls.survey.hearing.model.Meeting;

/**
 * Компонент для отображения списка экспозиций и собраний публичного слушания
 * <p>
 * к одному публичному слушанию может относится массив экспозиций и собраний
 * но пока отображаем только по одному собранию и экспозиции {@link ru.mos.polls.survey.hearing.gui.view.HearingInfoView#display(ru.mos.polls.survey.Survey)}
 *
 * @since 2.0
 */
public class HearingInfoView extends LinearLayout {
    private View meetingView;
    private View expositionView;

    private Meeting meeting;
    private Exposition exposition;
    private Callback callback;

    public void setCallback(Callback callback) {
        this.callback = callback;
    }

    public HearingInfoView(Context context) {
        super(context);
        init();
    }

    public HearingInfoView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public HearingInfoView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();

    }

    private void init() {
        setOrientation(VERTICAL);
        removeAllViews();
        View v = getView();
        addView(v);
        ButterKnife.bind(this);
    }

    private View getView() {
        View result = View.inflate(getContext(), R.layout.layout_hearing_info, null);
        meetingView = ButterKnife.findById(result, R.id.meeting);
        expositionView = ButterKnife.findById(result, R.id.exposition);
        return result;
    }

    @OnClick(R.id.meeting)
    void setMeetingView() {
        if (callback != null) {
            callback.onMeeting(meeting);
        }
    }

    @OnClick(R.id.exposition)
    void setExpositionView() {
        if (callback != null) {
            callback.onExposition(exposition);
        }
    }

    /**
     * Пока отображаем только одно собрание
     * и экспозицию для публичного слушания,
     * в перспективе возможно будет отображать
     * массивы экспозиций и собраний
     *
     * @param survey текущее голосование
     */
    public void display(Survey survey) {
        if (survey != null && survey.getKind().isHearing()) {
            List<Meeting> meetings = survey.getMeetings();
            if (meetings != null && meetings.size() > 0) {
                meeting = survey.getMeetings().get(0);
            }
            List<Exposition> expositions = survey.getExpositions();
            if (expositions != null && expositions.size() > 0) {
                exposition = survey.getExpositions().get(0);
            }
        } else {
            setVisibility(View.GONE);
        }
    }

    public void setVisible(int visibility) {
        meetingView.setVisibility(visibility);
        expositionView.setVisibility(visibility);
    }

    public interface Callback {
        void onMeeting(Meeting meeting);

        void onExposition(Exposition exposition);
    }

}
