package ru.mos.elk.test;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.test.ActivityInstrumentationTestCase2;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.jayway.android.robotium.solo.Solo;

import ru.mos.elk.R;
import ru.mos.elk.auth.AuthActivity;
import ru.mos.elk.auth.RegisterActivity;
import ru.mos.elk.auth.RestoreActivity;

/**
 * @author by Alex on 20.10.13.
 */
public class AuthActivityTest extends ActivityInstrumentationTestCase2<AuthActivity> {

    private Solo solo;
    private EditText etLogin;
    private EditText etPassword;

    public AuthActivityTest() {
        super(AuthActivity.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        Intent intent = new Intent();
        intent.putExtra(AuthActivity.SKIP_ACTIVITY, ProfileActivity.class);
        intent.putExtra(AuthActivity.PASSED_ACTIVITY, ProfileActivity.class);
        setActivityIntent(intent);
        solo = new Solo(getInstrumentation(),getActivity());
        etLogin = (EditText) solo.getView(R.id.etLogin);
        etPassword = (EditText) solo.getView(R.id.etPassword);
    }

    @Override
    protected void tearDown() throws Exception {
        solo.finishOpenedActivities();
    }

    public void test1ButtonsAvailability(){
        Button btnLogin = (Button) solo.getView(R.id.btnAction);
        Button skip = (Button) solo.getView(R.id.btnSkip);

        solo.clearEditText(etLogin);
        solo.enterText(etPassword, "werty");
        assertFalse("Login button should be unavailable:login is less then 10 numbers", btnLogin.isEnabled());
        solo.enterText(etLogin, "9264955957");
        solo.clearEditText(etPassword);
        assertFalse("Login button should be unavailable:password is empty", btnLogin.isEnabled());
        solo.enterText(etLogin,"9264955957");
        solo.enterText(etPassword, "werty");
        assertTrue("Login button should be available", btnLogin.isEnabled());

        Activity activity = solo.getCurrentActivity();
        Bundle extras = activity.getIntent().getExtras();
        if(extras.containsKey(AuthActivity.SKIP_ACTIVITY))
            assertTrue("Skip buuton should be available", skip.isEnabled());
        else
            assertFalse("Skip buuton should be unavailable", skip.isEnabled());

        assertTrue(solo.getView(R.id.btnRegister).isEnabled());
        assertTrue(solo.getView(R.id.btnRestorePassword).isEnabled());
    }

    public void test2Transitions(){
        solo.clickOnButton(solo.getString(R.string.restorePassword));
        assertTrue("wrong restore activity",solo.waitForActivity(RestoreActivity.class));
        solo.goBack(); //hide keyboard
        solo.goBack();
        assertTrue("wrong auth activity", solo.waitForActivity(AuthActivity.class));

        solo.clickOnButton(solo.getString(R.string.register));
        assertTrue("wrong register activity",solo.waitForActivity(RegisterActivity.class));
        solo.goBack(); //hide keyboard
        solo.goBack();
        assertTrue("wrong auth activity",solo.waitForActivity(AuthActivity.class));

        solo.clickOnButton(solo.getString(R.string.skip));
        assertTrue("wrong skip activity", solo.waitForActivity(ProfileActivity.class));

    }

    public void test3Authorize(){
        solo.enterText(etLogin,"9264955957");
        solo.enterText(etPassword, "-----");
        solo.clickOnButton(solo.getString(R.string.authorize));
        solo.waitForDialogToClose(20000L);
        assertEquals("wrong password: error text should be visible", View.VISIBLE, solo.getView(R.id.tvError).getVisibility());

        solo.clearEditText(etPassword);
        solo.enterText(etPassword, "werty");
        solo.clickOnButton(solo.getString(R.string.authorize));
        assertTrue("wrong passed activity", solo.waitForActivity(ProfileActivity.class));
    }
}
