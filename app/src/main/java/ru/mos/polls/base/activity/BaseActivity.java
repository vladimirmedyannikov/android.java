package ru.mos.polls.base.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.flurry.android.FlurryAgent;


import io.reactivex.disposables.CompositeDisposable;
import me.ilich.juggler.gui.JugglerActivity;

public class BaseActivity extends JugglerActivity {
	public static final String INTENT_LOGOUT = "ru.mos.elk.pages.LOGOUT";

    private static String flurryKey = "stub";
    protected CompositeDisposable disposables;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        disposables = new CompositeDisposable();
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
    	disposables.clear();
    	super.onDestroy();
    }

    public CompositeDisposable getDisposables() {
        return disposables;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        for (Fragment fragment : getSupportFragmentManager().getFragments()) {
            fragment.onActivityResult(requestCode, resultCode, data);
        }
    }
}
