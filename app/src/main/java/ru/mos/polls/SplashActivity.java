package ru.mos.polls;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;

import com.appsflyer.AppsFlyerLib;

import ru.mos.elk.netframework.request.Session;

public class SplashActivity extends Activity {
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

                    Intent intent;
                    if (Session.isAuthorized(SplashActivity.this)) {
                        intent = new Intent(SplashActivity.this, MainActivity.class);
                    } else
                        intent = new Intent(SplashActivity.this, AgAuthActivity.class);
                    intent.putExtra(AgAuthActivity.PASSED_ACTIVITY, MainActivity.class);
                    startActivity(intent);
                    finish();
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
