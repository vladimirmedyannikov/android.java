package ru.mos.polls.social.auth;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import oauth.signpost.OAuth;
import oauth.signpost.OAuthConsumer;
import oauth.signpost.OAuthProvider;
import oauth.signpost.commonshttp.CommonsHttpOAuthConsumer;
import oauth.signpost.commonshttp.CommonsHttpOAuthProvider;
import ru.mos.elk.BaseActivity;
import ru.mos.polls.R;
import ru.mos.polls.social.manager.SocialManager;


public class TwAuthActivity extends BaseActivity {

    final String TAG = TwAuthWrapper.class.getName();

    public static final String REQUEST_URL = "https://api.twitter.com/oauth/request_token";
    public static final String ACCESS_URL = "https://api.twitter.com/oauth/access_token";
    public static final String AUTHORIZE_URL = "https://api.twitter.com/oauth/authorize";

    final public static String CALLBACK_SCHEME = "x-oauthflow-twitter";
    final public static String CALLBACK_URL = CALLBACK_SCHEME + "://callback";

    private OAuthConsumer consumer;
    private OAuthProvider provider;

    private boolean authRequested = false;
    private boolean finishLocked = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final FrameLayout fl = new FrameLayout(this);
        final ProgressBar pb = new ProgressBar(this);
        final FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                Gravity.CENTER
        );
        pb.setLayoutParams(params);
        fl.addView(pb);

        setContentView(fl);
        if (savedInstanceState == null) {
            try {
                this.consumer = new CommonsHttpOAuthConsumer(
                        getString(R.string.tw_consumer_key),
                        getString(R.string.tw_consumer_secret)
                );
                this.provider = new CommonsHttpOAuthProvider(REQUEST_URL, ACCESS_URL, AUTHORIZE_URL);
            } catch (Exception e) {
                Log.e(TAG, "Error creating consumer / provider", e);
            }
        }
    }

    @Override
    public void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        final Uri uri = intent.getData();
        if (uri != null && CALLBACK_SCHEME.equals(uri.getScheme())) {
            Log.i(TAG, "Callback received : " + uri);
            Log.i(TAG, "Retrieving Access Token");
            new RetrieveAccessTokenTask(consumer, provider).execute(uri);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!authRequested) {
            new OAuthRequestTokenTask(consumer, provider).execute();
            authRequested = true;
        } else if (!finishLocked) {
            finish();
        }
    }

    public class RetrieveAccessTokenTask extends AsyncTask<Uri, Void, Void> {

        private final OAuthProvider provider;
        private final OAuthConsumer consumer;

        public RetrieveAccessTokenTask(OAuthConsumer consumer, OAuthProvider provider) {
            this.consumer = consumer;
            this.provider = provider;
        }

        @Override
        protected void onPreExecute() {
            finishLocked = true;
        }

        /**
         * Retrieve the oauth_verifier, and store the oauth and oauth_token_secret
         * for future API calls.
         */
        @Override
        protected Void doInBackground(Uri... params) {
            final Uri uri = params[0];
            final String oauth_verifier = uri.getQueryParameter(OAuth.OAUTH_VERIFIER);

            try {
                provider.retrieveAccessToken(consumer, oauth_verifier);

                Log.i(TAG, "OAuth - Access Token Retrieved");

                SocialManager.setAccessToken(TwAuthActivity.this, SocialManager.SOCIAL_ID_TW, consumer.getToken());
                SocialManager.setRefreshToken(TwAuthActivity.this, SocialManager.SOCIAL_ID_TW, consumer.getTokenSecret());
                SocialManager.setExpired(TwAuthActivity.this, SocialManager.SOCIAL_ID_TW, -1L);
            } catch (Exception e) {
                Log.e(TAG, "OAuth - Access Token Retrieval Error", e);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            finishLocked = false;
            finish();
        }
    }

    public class OAuthRequestTokenTask extends AsyncTask<Void, Void, Exception> {
        final String TAG = getClass().getName();

        private OAuthProvider provider;
        private OAuthConsumer consumer;

        /**
         * We pass the OAuth consumer and provider.
         *
         * @param provider The OAuthProvider object
         * @param consumer The OAuthConsumer object
         */
        public OAuthRequestTokenTask(OAuthConsumer consumer, OAuthProvider provider) {
            this.consumer = consumer;
            this.provider = provider;
        }

        /**
         * Retrieve the OAuth Request Token and present a browser to the user to authorize the token.
         */
        @Override
        protected Exception doInBackground(Void... params) {
            Exception result = null;
            try {
                Log.i(TAG, "Retrieving request token from Google servers");
                final String url = provider.retrieveRequestToken(consumer, CALLBACK_URL);
                Log.i(TAG, "Popping a browser with the authorize URL : " + url);
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url)).setFlags(
                        Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_NO_HISTORY | Intent.FLAG_FROM_BACKGROUND
                );
                startActivity(intent);
            } catch (Exception e) {
                result = e;
            }
            return result;
        }

        @Override
        protected void onPostExecute(Exception e) {
            super.onPostExecute(e);
            if (e != null) {
                Toast.makeText(TwAuthActivity.this, R.string.twitter_error_message, Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }
}
