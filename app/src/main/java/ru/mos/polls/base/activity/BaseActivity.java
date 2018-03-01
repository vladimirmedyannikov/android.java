package ru.mos.polls.base.activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;

import com.flurry.android.FlurryAgent;

import java.util.HashSet;
import java.util.Set;

import io.reactivex.disposables.CompositeDisposable;
import me.ilich.juggler.gui.JugglerActivity;

public class BaseActivity extends JugglerActivity {
	public static final String INTENT_LOGOUT = "ru.mos.elk.pages.LOGOUT";

    private static Set<Class<? extends Activity>> authRequered = new HashSet<Class<? extends Activity>>();
    private static String flurryKey = "stub";
    private BroadcastReceiver logoutReceiver = null;
    protected CompositeDisposable disposables;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        disposables = new CompositeDisposable();
        // see addAuthRequire
        if(authRequered.contains(getClass())) {
            logoutReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
//                    finish();
                }
            };
        	registerReceiver(logoutReceiver, new IntentFilter(INTENT_LOGOUT));
        }

    }

    /**
     * stub for versions before 11 API for cleanning back stack. No FLAG_ACTIVITY_CLEAR_TASK page require
     */
    public static void addAuthRequire(Class<? extends Activity> clsActivity) {
        authRequered.add(clsActivity);
    }




    public static void setFlurryKey(String key){
        flurryKey = key;
    }

    @Override
    protected void onStart() {
        super.onStart();
        FlurryAgent.onStartSession(this, flurryKey);
    }

    @Override
    protected void onStop() {
        super.onStop();
        FlurryAgent.onEndSession(this);
    }
    
    @Override
    protected void onDestroy() {
        if(authRequered.contains(getClass()))
            unregisterReceiver(logoutReceiver);
    	disposables.clear();
    	super.onDestroy();
    }

    public CompositeDisposable getDisposables() {
        return disposables;
    }
}
