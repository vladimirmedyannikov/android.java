package ru.mos.polls;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.text.Spanned;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;

import ru.mos.elk.auth.RegisterActivity;
import ru.mos.polls.popup.PopupController;

public class AgRegisterActivity extends RegisterActivity {
    private GoogleStatistics.Registry statistics = new GoogleStatistics.Registry();

    private EditText phone;
    private CheckBox cbAgreeOffer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Spanned spanned = Html.fromHtml(getString(R.string.ag_agree_offer));
        TextView offer = (TextView) findViewById(R.id.tvOffer);
        AgAuthActivity.OfferLinkMovementMethod movementMethod = AgAuthActivity.OfferLinkMovementMethod.getInstance();
        movementMethod.setLinkListener(new AgAuthActivity.OfferLinkMovementMethod.LinkListener() {
            @Override
            public void onClicked() {
                statistics.offerClick(AgRegisterActivity.this);
            }
        });
        offer.setMovementMethod(movementMethod);
        offer.setText(spanned);

        final Button btnRegistry = (Button) findViewById(R.id.btnAction);
        phone = (EditText) findViewById(R.id.etPhoneNumber);

        cbAgreeOffer = (CheckBox) findViewById(R.id.cbAgreeOffer);
        cbAgreeOffer.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                btnRegistry.setEnabled(cbAgreeOffer.isChecked() && phone.getText().length() > 0);
            }
        });

        TextWatcher watcher = new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {
                btnRegistry.setEnabled(cbAgreeOffer.isChecked() && phone.getText().length() > 0);
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
        };
        phone.addTextChangedListener(watcher);


        findViewById(R.id.auth).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        final View help = findViewById(R.id.help);
        help.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AbstractActivity.hideSoftInput(AgRegisterActivity.this, help);
                PopupController.authOrRegistry(AgRegisterActivity.this, phone.getText().toString());
                statistics.helpClick(AgRegisterActivity.this);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void register() {
        if (cbAgreeOffer.isChecked() && phone.getText().length() > 0) {
            super.register();
        }
    }

    @Override
    protected void onRegistrationSuccess() {
        super.onRegistrationSuccess();
        startAuth();
        statistics.check(this, true);
    }

    private void startAuth() {
        Intent authIntent = new Intent(AgRegisterActivity.this, AgAuthActivity.class);
        authIntent.putExtra(AgAuthActivity.PASSED_ACTIVITY, MainActivity.class);
        EditText phone = (EditText) findViewById(R.id.etPhoneNumber);
        authIntent.putExtra("phone", phone.getText().toString());
        authIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(authIntent);
        finish();
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
