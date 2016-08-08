package ru.mos.polls.social.auth;

import android.content.Context;

import ru.mos.polls.R;
import ru.ok.android.sdk.Odnoklassniki;
import ru.ok.android.sdk.OkListener;
import ru.ok.android.sdk.util.OkScope;


public class OkAuthWrapper {

    private final Context context;
    private final OkListener listener;

    public OkAuthWrapper(Context context, OkListener listener) {
        this.context = context;
        this.listener = listener;
    }

    public void requestAuth(boolean forPosting) {
        final String id = context.getString(R.string.ok_app_id);
        final String secret = context.getString(R.string.ok_app_secret);
        final String key = context.getString(R.string.ok_app_key);
        final Odnoklassniki ok = Odnoklassniki.createInstanceForTokens(context.getApplicationContext(), id, key, secret);
        ok.setOkListener(listener);
        if (!forPosting) {
            ok.requestAccessAndRefreshTokens(context, false, OkScope.VALUABLE_ACCESS);
        } else {
            ok.requestAuthorization(context, false, OkScope.VALUABLE_ACCESS);
        }
    }
}
