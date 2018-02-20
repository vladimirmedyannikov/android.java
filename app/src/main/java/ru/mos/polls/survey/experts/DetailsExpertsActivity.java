package ru.mos.polls.survey.experts;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.StateListDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.List;

import butterknife.ButterKnife;
import ru.mos.polls.R;
import ru.mos.polls.ToolbarAbstractActivity;
import ru.mos.polls.helpers.TitleHelper;
import ru.mos.polls.survey.summary.ExpertsView;

/**
 * Экран списка мнений экспертов<br/>
 * Каждое мнение эксперта описывается сущностью {@link DetailsExpert}<br/>
 * Получение экспертов осуществляется посредством {@link ExpertsApiControllerRX#loadDetailExperts(Context, long, long, boolean, ExpertsApiControllerRX.DetailsExpertListener)}
 *
 * @since 1.8
 */
public class DetailsExpertsActivity extends ToolbarAbstractActivity {
    private static final String EXTRA_EXPERT = "extra_expert";
    private static final String EXTRA_IS_HEARING = "extra_is_hearing";
    private static final String EXTRA_POLL_ID = "extra_poll_id";
    private static final String EXTRA_QUESTION_ID = "extra_question_id";

    public static void startActivityByPollId(Context context, DetailsExpert detailsExpert, long pollId, boolean isHearing) {
        startActivity(context, detailsExpert, pollId, 0, isHearing);
    }

    public static void startActivityByQuestionId(Context context, DetailsExpert detailsExpert, long questionId, boolean isHearing) {
        startActivity(context, detailsExpert, 0, questionId, isHearing);
    }

    public static void startActivity(Context context, DetailsExpert detailsExpert, long pollId, long questionId, boolean isHearing) {
        Intent intent = new Intent(context, DetailsExpertsActivity.class);
        intent.putExtra(EXTRA_EXPERT, detailsExpert);
        intent.putExtra(EXTRA_POLL_ID, pollId);
        intent.putExtra(EXTRA_QUESTION_ID, questionId);
        intent.putExtra(EXTRA_IS_HEARING, isHearing);
        context.startActivity(intent);
    }

    private ListView listView;
    private long pollId, questionId;
    private boolean isHearing;
    private DetailsExpert detailsExpert;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dynamic_toolbar);
        ButterKnife.bind(this);
        TitleHelper.setTitle(this, R.string.title_experts);
        getParams();
        loadDetailsExperts();
    }

    @Override
    protected void findViews() {
        listView = ButterKnife.findById(this, android.R.id.list);
        /**
         * Устанавливаем прозраный фон селектора,
         * убираем разделитель
         */
        listView.setDivider(getResources().getDrawable(android.R.color.transparent));
        listView.setSelector(new StateListDrawable());
    }

    private void getParams() {
        pollId = getIntent().getLongExtra(EXTRA_POLL_ID, -1);
        questionId = getIntent().getLongExtra(EXTRA_QUESTION_ID, -1);
        isHearing = getIntent().getBooleanExtra(EXTRA_IS_HEARING, false);
        detailsExpert = (DetailsExpert) getIntent().getSerializableExtra(EXTRA_EXPERT);
    }

    private void loadDetailsExperts() {
        ExpertsApiControllerRX.DetailsExpertListener listener = new ExpertsApiControllerRX.DetailsExpertListener() {
            @Override
            public void onLoaded(List<DetailsExpert> detailsExperts) {
                if (detailsExperts != null) {
                    ExpertsAdapter expertsAdapter = new ExpertsAdapter(DetailsExpertsActivity.this, detailsExperts);
                    listView.setAdapter(expertsAdapter);
                    listView.setSelection(getScrollPosition(detailsExperts));
                }
            }

            @Override
            public void onError() {

            }
        };
        ExpertsApiControllerRX.loadDetailExperts(this, pollId, questionId, isHearing, listener);
    }

    private int getScrollPosition(List<DetailsExpert> detailsExperts) {
        int result = 0;
        for (int i = 0; i < detailsExperts.size(); ++i) {
            if (detailsExpert.compare(detailsExperts.get(i))) {
                result = i;
                break;
            }
        }
        return result;
    }

    private class ExpertsAdapter extends ArrayAdapter<DetailsExpert> {

        public ExpertsAdapter(Context context, List<DetailsExpert> objects) {
            super(context, R.layout.item_expert, objects);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            DetailsExpert detailsExpert = getItem(position);
            convertView = View.inflate(getContext(), R.layout.item_expert, null);
            displayExpertAvatar(convertView, detailsExpert);
            displayExpertFio(convertView, detailsExpert);
            displayExpertDescription(convertView, detailsExpert);
            displayExpertBody(convertView, detailsExpert);
            return convertView;
        }

        private void displayExpertFio(View view, DetailsExpert detailsExpert) {
            final TextView expertFio = ButterKnife.findById(view, R.id.expertFio);
            expertFio.setText(detailsExpert.getTitle());
        }

        private void displayExpertDescription(View view, DetailsExpert detailsExpert) {
            final TextView expertDescription = ButterKnife.findById(view, R.id.expertDescription);
            expertDescription.setText(detailsExpert.getDescription());
        }

        private void displayExpertBody(View view, DetailsExpert detailsExpert) {
            final TextView expertBody = ButterKnife.findById(view, R.id.body);
            expertBody.setText(detailsExpert.getBody());
        }

        private void displayExpertAvatar(final View view, DetailsExpert detailsExpert) {
            final ImageView imageView = ButterKnife.findById(view, R.id.expertAvatar);
            final ProgressBar loadingProgress = ButterKnife.findById(view, R.id.loadingProgress);
            ExpertsView.loadAvatar(DetailsExpertsActivity.this, imageView, loadingProgress, detailsExpert);
        }
    }
}
