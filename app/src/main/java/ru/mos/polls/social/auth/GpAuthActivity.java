package ru.mos.polls.social.auth;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.plus.Plus;

import ru.mos.polls.R;
import ru.mos.polls.social.manager.SocialManager;


public class GpAuthActivity extends Activity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private static final int REQUEST_CODE_RESOLVE_ERR = 9000;

    private GoogleApiClient plusClient;
    private ConnectionResult connectionResult;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Plus.PlusOptions plusOptions = new Plus.PlusOptions.Builder().
                setServerClientId(Scopes.PLUS_LOGIN + " " + Scopes.PROFILE).
                build();
        plusClient = new GoogleApiClient.Builder(this)
                .addApi(Plus.API, plusOptions)
                .addScope(Plus.SCOPE_PLUS_LOGIN)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(getString(R.string.sign_in_google_plus));
        progressDialog.show();
    }

    @Override
    public void onConnected(Bundle bundle) {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
        /*
          * Получение токена для подтвержденияданных на сервесайде,
          * также, получив токен, можно запросить
          * все данные https://www.googleapis.com/oauth2/v1/userinfo?access_token=полученный токен .
        */

        GpHelper.getAccessToken(this, Plus.AccountApi.getAccountName(plusClient), new GpHelper.GetGpTokenTask.OnTokenListener() {
            @Override
            public void onTokenRetrieve(String accessToken) {
                SocialManager.setAccessToken(GpAuthActivity.this, SocialManager.SOCIAL_ID_GP, accessToken);
                SocialManager.setRefreshToken(GpAuthActivity.this, SocialManager.SOCIAL_ID_GP, "");
                SocialManager.setGpAccountName(GpAuthActivity.this, Plus.AccountApi.getAccountName(plusClient));
                finish();
            }
        });
    }

    @Override
    public void onConnectionSuspended(int i) {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
        finish();
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        if (progressDialog != null && progressDialog.isShowing()) {
            // Пользователь уже нажал кнопку входа. Запустите, чтобы устранить
            // ошибки подключения. Дождитесь появления onGet(), чтобы скрыть
            // диалоговое окно подключения.
            if (result.hasResolution()) {
                try {
                    result.startResolutionForResult(this, REQUEST_CODE_RESOLVE_ERR);
                } catch (IntentSender.SendIntentException e) {
                    plusClient.connect();
                }
            } else {
                progressDialog.dismiss();
                if (GpHelper.isGooglePlusInstalled(this)) {
                    Toast.makeText(this, R.string.error_occurs_when_you_try_to_auth, Toast.LENGTH_LONG).show();
                    finish();
                } else {
                    GpHelper.setErrorDialog(this, true);
                }
            }
        }
        // Сохраните объект Intent, чтобы запускать действие, когда пользователь
        // нажимает кнопку входа.
        connectionResult = result;
    }

    @Override
    protected void onStart() {
        super.onStart();
        plusClient.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        plusClient.disconnect();
    }

    @Override
    protected void onActivityResult(int requestCode, int responseCode, Intent intent) {
        if (requestCode == REQUEST_CODE_RESOLVE_ERR && responseCode == RESULT_OK) {
            connectionResult = null;
            plusClient.connect();
        }
    }
}
