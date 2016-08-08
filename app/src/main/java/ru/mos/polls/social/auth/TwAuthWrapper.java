package ru.mos.polls.social.auth;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import oauth.signpost.OAuth;
import oauth.signpost.OAuthConsumer;
import oauth.signpost.OAuthProvider;
import oauth.signpost.commonshttp.CommonsHttpOAuthConsumer;
import oauth.signpost.commonshttp.CommonsHttpOAuthProvider;
import ru.mos.elk.BaseActivity;
import ru.mos.polls.R;


public class TwAuthWrapper {
    final String TAG = TwAuthWrapper.class.getName();

    public static final String REQUEST_URL = "https://api.twitter.com/oauth/request_token";
    public static final String ACCESS_URL = "https://api.twitter.com/oauth/access_token";
    public static final String AUTHORIZE_URL = "https://api.twitter.com/oauth/authorize";

    final public static String CALLBACK_SCHEME = "x-oauthflow-twitter";
    final public static String CALLBACK_URL = CALLBACK_SCHEME + "://callback";

    private OAuthConsumer consumer;
    private OAuthProvider provider;

    private final BaseActivity activity;
    private final TwAuthCallback callback;

    public TwAuthWrapper(BaseActivity activity, TwAuthCallback callback) {
        this.activity = activity;
        this.callback = callback;
    }

    public void onActivityCreate(Bundle savedInstanceState) {
        if (savedInstanceState == null) {
            try {
                this.consumer = new CommonsHttpOAuthConsumer(
                        activity.getString(R.string.tw_consumer_key),
                        activity.getString(R.string.tw_consumer_secret)
                );
                this.provider = new CommonsHttpOAuthProvider(REQUEST_URL, ACCESS_URL, AUTHORIZE_URL);
            } catch (Exception e) {
                Log.e(TAG, "Error creating consumer / provider", e);
            }
        }
    }

    public void requestAuth() {
        new OAuthRequestTokenTask(activity, consumer, provider).execute();
    }

    public void onActivityNewIntent(Intent intent) {
        final Uri uri = intent.getData();
        if (uri != null && CALLBACK_SCHEME.equals(uri.getScheme())) {
            Log.i(TAG, "Callback received : " + uri);
            Log.i(TAG, "Retrieving Access Token");
            new RetrieveAccessTokenTask(consumer, provider).execute(uri);
        }
    }

    public class RetrieveAccessTokenTask extends AsyncTask<Uri, Void, OAuthResult> {

        private final OAuthProvider provider;
        private final OAuthConsumer consumer;

        public RetrieveAccessTokenTask(OAuthConsumer consumer, OAuthProvider provider) {
            this.consumer = consumer;
            this.provider = provider;
        }


        /**
         * Retrieve the oauth_verifier, and store the oauth and oauth_token_secret
         * for future API calls.
         */
        @Override
        protected OAuthResult doInBackground(Uri... params) {
            final Uri uri = params[0];
            final String oauth_verifier = uri.getQueryParameter(OAuth.OAUTH_VERIFIER);

            try {
                provider.retrieveAccessToken(consumer, oauth_verifier);

                Log.i(TAG, "OAuth - Access Token Retrieved");

                return new OAuthResult(consumer.getToken(), consumer.getTokenSecret());
            } catch (Exception e) {
                Log.e(TAG, "OAuth - Access Token Retrieval Error", e);
            }

            return null;
        }

        @Override
        protected void onPostExecute(OAuthResult authResult) {
            if (authResult == null) {
                callback.onErrorHappened();
                return;
            }
            callback.onTokenReceived(authResult.token);
        }
    }

    static class OAuthResult {
        public String token;
        public String secret;

        public OAuthResult(String token, String secret) {
            this.token = token;
            this.secret = secret;
        }
    }

    public static class OAuthRequestTokenTask extends AsyncTask<Void, Void, Void> {
        final String TAG = getClass().getName();
        private Context context;
        private OAuthProvider provider;
        private OAuthConsumer consumer;

        /**
         * We pass the OAuth consumer and provider.
         *
         * @param context  Required to be able to start the intent to launch the browser.
         * @param provider The OAuthProvider object
         * @param consumer The OAuthConsumer object
         */
        public OAuthRequestTokenTask(Context context, OAuthConsumer consumer, OAuthProvider provider) {
            this.context = context;
            this.consumer = consumer;
            this.provider = provider;
        }

        /**
         * Retrieve the OAuth Request Token and present a browser to the user to authorize the token.
         */
        @Override
        protected Void doInBackground(Void... params) {

            try {
                Log.i(TAG, "Retrieving request token from Google servers");
                final String url = provider.retrieveRequestToken(consumer, CALLBACK_URL);
                Log.i(TAG, "Popping a browser with the authorize URL : " + url);
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url)).setFlags(
                        Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_NO_HISTORY | Intent.FLAG_FROM_BACKGROUND
                );
                context.startActivity(intent);
            } catch (Exception e) {
                Log.e(TAG, "Error during OAUth retrieve request token", e);
            }

            return null;
        }
    }

    public static interface TwAuthCallback {
        void onTokenReceived(String token);

        void onErrorHappened();
    }
}
