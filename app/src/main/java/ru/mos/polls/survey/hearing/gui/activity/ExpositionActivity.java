package ru.mos.polls.survey.hearing.gui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import ru.mos.polls.R;
import ru.mos.polls.ToolbarAbstractActivity;
import ru.mos.polls.helpers.TitleHelper;
import ru.mos.polls.survey.hearing.model.Exposition;

/**
 * Экран для отображения данных по экспозиции публичного слушания
 *
 * @since 2.0
 */
public class ExpositionActivity extends ToolbarAbstractActivity {
    public static final String EXTRA_MODEL = "extra_model";
    public static final String EXTRA_TITLE = "extra_title";

    protected static final SimpleDateFormat SDF_DD_MM_YYYY = new SimpleDateFormat("dd.MM.yyyy");

    public static void start(Context context, Exposition exposition, String surveyTitle) {
        Intent start = new Intent(context, ExpositionActivity.class);
        start.putExtra(EXTRA_MODEL, exposition);
        start.putExtra(EXTRA_TITLE, surveyTitle);
        context.startActivity(start);
    }

    @BindView(R.id.address)
    TextView address;
    @BindView(R.id.date)
    TextView date;
    @BindView(R.id.link)
    TextView link;
    @BindView(R.id.work)
    TextView work;
    @BindView(R.id.contactContainer)
    LinearLayout contactContainer;

    private Exposition exposition;
    private String surveyTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exposition);
        ButterKnife.bind(this);
        exposition = (Exposition) getIntent().getSerializableExtra(EXTRA_MODEL);
        refreshUI();
    }

    @Override
    protected void findViews() {
        TitleHelper.setTitle(this, getString(R.string.title_exposition));
    }

    protected void refreshUI() {
        displayTitleAndAddress();
        displayDate();
        displayContacts();
        displayLink();
        displayWork();
    }

    private void displayWork() {
        work.setText(exposition.getWorkingHours());
    }

    private void displayTitleAndAddress() {
        address.setText(exposition.getAddress());
    }

    @OnClick(R.id.address)
    void goToPosition() {
        exposition.getPosition().goToGoogleMapsOnly(ExpositionActivity.this, exposition.getAddress());
    }

    private void displayDate() {
        String value = String.format("с %s до %s",
                SDF_DD_MM_YYYY.format(exposition.getStartDate()),
                SDF_DD_MM_YYYY.format(exposition.getEndDate()));
        date.setText(value);
    }

    private void displayLink() {
        Spanned linkForHearing = Html.fromHtml(
                String.format(getString(R.string.detail_of_exosition), exposition.getReferenceUrl()));
        if (!"".equals(exposition.getReferenceUrl())){
        link.setMovementMethod(LinkMovementMethod.getInstance());
        } else {
            link.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(getApplicationContext(), R.string.url_not_avaible, Toast.LENGTH_SHORT).show();
                }
            });
        }
        link.setText(linkForHearing);
    }

    private void displayContacts() {
        contactContainer.removeAllViews();
        for (String contact : exposition.getContactsPhones()) {
            TextView view = (TextView) getLayoutInflater().inflate(R.layout.layout_link_contact, null);
            contact = contact.replace("\"", "");
            view.setText(String.format("тел. %s", contact));
            final String finalContact = contact;
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Exposition.callDial(ExpositionActivity.this, finalContact);
                }
            });
            contactContainer.addView(view);
        }
        for (String email : exposition.getContactsEmail()) {
            TextView view = (TextView) getLayoutInflater().inflate(R.layout.layout_link_contact, null);
            email = String.format("e-mail <a href=\"mailto:%s\">%s</a>", email, email);
            view.setMovementMethod(LinkMovementMethod.getInstance());
            view.setText(Html.fromHtml(email));
            contactContainer.addView(view);
        }
    }
}
