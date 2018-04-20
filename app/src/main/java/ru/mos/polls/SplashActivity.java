package ru.mos.polls;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;

import com.appsflyer.AppsFlyerLib;

import me.ilich.juggler.change.Add;
import ru.mos.polls.auth.state.AgAuthState;
import ru.mos.polls.base.activity.BaseActivity;
import ru.mos.polls.push.GCMHelper;
import ru.mos.polls.rxhttp.session.Session;


public class SplashActivity extends BaseActivity {

    public static void startApp(Context context) {
        Intent intent = new Intent(context, SplashActivity.class);
        if (Session.get().hasSession()) {
            intent = new Intent(context, MainActivity.class);
        }
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
        GCMHelper.registerPush(this);
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
        if (!Session.get().hasSession()) {
            navigateTo().state(Add.newActivity(new AgAuthState(), BaseActivity.class));
        } else {
            startApp(SplashActivity.this);
        }
        finish();
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
