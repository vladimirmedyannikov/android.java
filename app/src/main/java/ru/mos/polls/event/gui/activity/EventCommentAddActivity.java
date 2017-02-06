package ru.mos.polls.event.gui.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.Toast;

import com.android.volley2.VolleyError;

import butterknife.BindView;
import butterknife.ButterKnife;
import ru.mos.elk.BaseActivity;
import ru.mos.polls.GoogleStatistics;
import ru.mos.polls.R;
import ru.mos.polls.Statistics;
import ru.mos.polls.event.controller.EventAPIController;
import ru.mos.polls.event.model.EventComment;
import ru.mos.polls.helpers.TitleHelper;

public class EventCommentAddActivity extends BaseActivity {

    private static final String EXTRA_EVENT_ID = "event_id";
    private static final String EXTRA_TITLE = "title";
    private static final String EXTRA_BODY = "body";
    private static final String EXTRA_RATING = "rating";
    private static final String EXTRA_TYPE = "type";

    public static Intent createIntent(Context context, long eventId, EventComment myComment, String type) {
        Intent result = new Intent(context, EventCommentAddActivity.class);
        result.putExtra(EXTRA_EVENT_ID, eventId);
        result.putExtra(EXTRA_TITLE, myComment.getTitle());
        result.putExtra(EXTRA_BODY, myComment.getBody());
        result.putExtra(EXTRA_RATING, myComment.getRating());
        result.putExtra(EXTRA_TYPE, type);
        return result;
    }

    public static Intent createIntent(Context context, long eventId, String type) {
        Intent result = new Intent(context, EventCommentAddActivity.class);
        result.putExtra(EXTRA_EVENT_ID, eventId);
        result.putExtra(EXTRA_TYPE, type);
        return result;
    }

    @BindView(R.id.rating)
    RatingBar ratingBar;
    @BindView(R.id.title)
    EditText titleEditText;
    @BindView(R.id.body)
    EditText bodyEditText;

    private boolean isNewComment;
    private long eventId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_comment_add);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ButterKnife.bind(this);
        final long notSet = -1;
        eventId = getIntent().getLongExtra(EXTRA_EVENT_ID, notSet);
        if (eventId == notSet) {
            finish();
            return;
        }
        String title = getIntent().getStringExtra(EXTRA_TITLE);
        String body = getIntent().getStringExtra(EXTRA_BODY);
        int rating = (int) getIntent().getDoubleExtra(EXTRA_RATING, 0);
        isNewComment = TextUtils.isEmpty(title) && TextUtils.isEmpty(body) && rating == 0;
        if (isNewComment) {
            TitleHelper.setTitle(this, getString(R.string.add_comment));
        } else {
            TitleHelper.setTitle(this, getString(R.string.update_comment));
        }

        titleEditText.setText(title);
        bodyEditText.setText(body);
        ratingBar.setRating(rating);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.event_comment_add, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        boolean result;
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                result = true;
                break;
            case R.id.action_comment_add:
                hideSoftInput(titleEditText);
                sendComment();
                result = true;
                break;
            default:
                result = super.onOptionsItemSelected(item);
        }
        return result;
    }

    private void hideSoftInput(View view) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    private void sendComment() {
        String title = titleEditText.getText().toString();
        String body = bodyEditText.getText().toString();
        int rating = ratingBar.getProgress();
        /**
         * Из задачи "Делаем рейтинг обязательным, а сам кооментарий может быть пустым. При открытии этого окна по умолчанию звездочки не стоят (пустые звездочки)"
         */
        if (rating == 0) {
            String type = getIntent().getStringExtra(EXTRA_TYPE);
            String message
                    = String.format(getString(R.string.add_rating), !TextUtils.isEmpty(type) ? type.toLowerCase() : getString(R.string.event));
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
            return;
        }
        /**
         * Отправка оценки без комментария
         */
        if (TextUtils.isEmpty(title) && TextUtils.isEmpty(body)) {
            Statistics.eventRatingSend(eventId);
            GoogleStatistics.Events.eventRatingSend(eventId);
        }
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.show();
        EventAPIController.UpdateEventCommentListener updateEventCommentListener = new EventAPIController.UpdateEventCommentListener() {

            @Override
            public void onUpdated(boolean isUpdated) {
                Statistics.eventSendComment(eventId);
                GoogleStatistics.Events.eventSendComment(eventId);
                dismissProgress();
                if (isNewComment) {
                    Toast.makeText(EventCommentAddActivity.this, getString(R.string.Comment_added_success), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(EventCommentAddActivity.this, getString(R.string.cooment_updated_success), Toast.LENGTH_SHORT).show();
                }
                finish();
            }

            @Override
            public void onError(VolleyError volleyError) {
                dismissProgress();
                Toast.makeText(EventCommentAddActivity.this, volleyError.getMessage(), Toast.LENGTH_SHORT).show();
            }

            private void dismissProgress() {
                if (progressDialog != null) {
                    progressDialog.dismiss();
                }
            }

        };
        EventAPIController.updateComment(this, eventId, title, body, rating, updateEventCommentListener);
    }

}
