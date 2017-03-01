package ru.mos.polls;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;

import com.appsflyer.AppsFlyerLib;

import ru.mos.elk.netframework.request.Session;
import ru.mos.polls.tutorial.TutorialActivity;
import ru.mos.polls.tutorial.TutorialFragment;

public class SplashActivity extends Activity {

    public static void startApp(Context context) {
        Intent intent = new Intent(context, AgAuthActivity.class);
        if (Session.isAuthorized(context)) {
            intent = new Intent(context, MainActivity.class);
        }
        intent.putExtra(AgAuthActivity.PASSED_ACTIVITY, MainActivity.class);
        context.startActivity(intent);
    }

    protected boolean active = true;
    protected int splashTime = 2500;
    private Thread splashTread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        AppsFlyerLib.sendTracking(getApplicationContext());

        AppsFlyerLib.sendTracking(getApplicationContext());

        splashTread = new Thread() {
            @Override
            public void run() {
                try {
                    int waited = 0;
                    while (active && (waited < splashTime)) {
                        sleep(100);
                        waited += 100;
                    }
                    onSplashShowingComplete();
                } catch (InterruptedException ignored) {
                }
            }
        };
        splashTread.setName("SplashThread");
        splashTread.start();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN)
            active = false;
        return true;
    }

    private void onSplashShowingComplete() {
        if (!TutorialFragment.Manager.wasShow(this)) {
            TutorialActivity.start(this);
        } else {
            startApp(SplashActivity.this);
            finish();
        }
    }

    @Override
    public void onBackPressed() {
        if (splashTread != null) {
            splashTread.interrupt();
            splashTread = null;
            active = false;
        }
        super.onBackPressed();
    }
}
