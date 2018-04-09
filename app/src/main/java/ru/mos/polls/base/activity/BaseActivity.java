package ru.mos.polls.base.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.flurry.android.FlurryAgent;

import io.reactivex.disposables.CompositeDisposable;
import me.ilich.juggler.gui.JugglerActivity;
import ru.mos.social.controller.SocialController;

public class BaseActivity extends JugglerActivity {
	public static final String INTENT_LOGOUT = "ru.mos.elk.pages.LOGOUT";

    private static String flurryKey = "stub";
    protected CompositeDisposable disposables;
    private SocialController socialController;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        disposables = new CompositeDisposable();
        socialController = new SocialController(this);
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

    public SocialController getSocialController() {
        return socialController;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        socialController.onActivityResult(requestCode, resultCode, data);
        for (Fragment fragment : getSupportFragmentManager().getFragments()) {
            fragment.onActivityResult(requestCode, resultCode, data);
        }
    }
}
