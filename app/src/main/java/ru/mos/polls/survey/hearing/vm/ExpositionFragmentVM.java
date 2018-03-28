package ru.mos.polls.survey.hearing.vm;

import android.text.Html;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;

import ru.mos.polls.R;
import ru.mos.polls.base.component.UIComponentFragmentViewModel;
import ru.mos.polls.base.component.UIComponentHolder;
import ru.mos.polls.databinding.FragmentExpositionBinding;
import ru.mos.polls.survey.hearing.gui.fragment.ExpositionFragment;
import ru.mos.polls.survey.hearing.model.Exposition;

public class ExpositionFragmentVM extends UIComponentFragmentViewModel<ExpositionFragment, FragmentExpositionBinding>{

    protected static final SimpleDateFormat SDF_DD_MM_YYYY = new SimpleDateFormat("dd.MM.yyyy");

    private TextView address;
    private TextView date;
    private TextView link;
    private TextView work;
    private LinearLayout contactContainer;

    private Exposition exposition;
    private String surveyTitle;

    public ExpositionFragmentVM(ExpositionFragment fragment, FragmentExpositionBinding binding) {
        super(fragment, binding);
    }

    @Override
    protected UIComponentHolder createComponentHolder() {
        return new UIComponentHolder.Builder().build();
    }

    @Override
    protected void initialize(FragmentExpositionBinding binding) {
        address = binding.address;
        date = binding.date;
        link = binding.link;
        work = binding.work;
        contactContainer = binding.contactContainer;
        exposition = getFragment().getExtraExposition();
        surveyTitle = getFragment().getExtraTitle();
        address.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                exposition.getPosition().goToGoogleMapsOnly(getFragment().getContext(), exposition.getAddress());
            }
        });
    }

    @Override
    public void onViewCreated() {
        super.onViewCreated();
        refreshUI();
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

    private void displayDate() {
        String value = String.format("с %s до %s",
                SDF_DD_MM_YYYY.format(exposition.getStartDate()),
                SDF_DD_MM_YYYY.format(exposition.getEndDate()));
        date.setText(value);
    }

    private void displayLink() {
        Spanned linkForHearing = Html.fromHtml(
                String.format(getFragment().getString(R.string.detail_of_exosition), exposition.getReferenceUrl()));
        if (!"".equals(exposition.getReferenceUrl())){
            link.setMovementMethod(LinkMovementMethod.getInstance());
        } else {
            link.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(getFragment().getContext(), R.string.url_not_avaible, Toast.LENGTH_SHORT).show();
                }
            });
        }
        link.setText(linkForHearing);
    }

    private void displayContacts() {
        contactContainer.removeAllViews();
        for (String contact : exposition.getContactsPhones()) {
            TextView view = (TextView) getFragment().getLayoutInflater().inflate(R.layout.layout_link_contact, null);
            contact = contact.replace("\"", "");
            view.setText(String.format("тел. %s", contact));
            final String finalContact = contact;
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Exposition.callDial(getFragment().getContext(), finalContact);
                }
            });
            contactContainer.addView(view);
        }
        for (String email : exposition.getContactsEmail()) {
            TextView view = (TextView) getFragment().getLayoutInflater().inflate(R.layout.layout_link_contact, null);
            email = String.format("e-mail <a href=\"mailto:%s\">%s</a>", email, email);
            view.setMovementMethod(LinkMovementMethod.getInstance());
            view.setText(Html.fromHtml(email));
            contactContainer.addView(view);
        }
    }
}
