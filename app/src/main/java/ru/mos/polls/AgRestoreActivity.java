package ru.mos.polls;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import com.android.volley2.VolleyError;

import ru.mos.elk.auth.RestoreActivity;
import ru.mos.polls.popup.PopupController;


public class AgRestoreActivity extends RestoreActivity {
    private GoogleStatistics.Recovery statistics;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        statistics = new GoogleStatistics.Recovery();
        findViewById(R.id.cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                statistics.authClick();
                finish();
            }
        });
        final View help = findViewById(R.id.help);
        help.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                statistics.helpClick();
                AbstractActivity.hideSoftInput(AgRestoreActivity.this, help);
                PopupController.recovery(AgRestoreActivity.this);
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
    protected void onRegistrationFail(VolleyError error, ProgressDialog dialog) {
        super.onRegistrationFail(error, dialog);
        statistics.errorOccurs(error.getMessage());
        statistics.check(false);
    }

    @Override
    protected void onRegistrationSuccess() {
        super.onRegistrationSuccess();
        statistics.check(true);
        startAuth();
    }

    private void startAuth() {
        Intent authIntent = new Intent(AgRestoreActivity.this, AgAuthActivity.class);
        authIntent.putExtra(AgAuthActivity.PASSED_ACTIVITY, MainActivity.class);
        authIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        EditText phone = (EditText) findViewById(R.id.etPhoneNumber);
        authIntent.putExtra("phone", phone.getText().toString());
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

    private void displayAlert() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.certificate_loaded).setCancelable(
                false).setPositiveButton("OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    private final BroadcastReceiver certificateLoaded = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            displayAlert();
        }
    };
}
