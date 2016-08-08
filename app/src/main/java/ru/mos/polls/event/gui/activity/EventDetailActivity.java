package ru.mos.polls.event.gui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

import butterknife.ButterKnife;
import ru.mos.polls.R;
import ru.mos.polls.ToolbarAbstractActivity;
import ru.mos.polls.event.model.EventDetail;
import ru.mos.polls.helpers.TextHelper;
import ru.mos.polls.helpers.TitleHelper;

public class EventDetailActivity extends ToolbarAbstractActivity {
    private static final String EXTRA_TITLE = "extra_title";
    private static final String EXTRA_BODY = "extra_body";

    public static void startActivity(Context context, EventDetail eventDetail) {
        Intent intent = new Intent(context, EventDetailActivity.class);
        intent.putExtra(EXTRA_TITLE, eventDetail.getTitle());
        intent.putExtra(EXTRA_BODY, eventDetail.getBody());
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_detail);
        ButterKnife.bind(this);
        String title = getIntent().getStringExtra(EXTRA_TITLE);
        String body = getIntent().getStringExtra(EXTRA_BODY);
        TitleHelper.setTitle(this, TextHelper.capitalizeFirstLatter(title.toLowerCase()));
        TextView description = ButterKnife.findById(this, R.id.description);
        description.setText(body);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
