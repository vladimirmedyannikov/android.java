package ru.mos.elk.test;

import android.test.ActivityInstrumentationTestCase2;

import com.jayway.android.robotium.solo.Solo;

/**
 * @author by Alex on 21.10.13.
 */
public class ProfileActivityTest extends ActivityInstrumentationTestCase2<ProfileActivity> {

    private Solo solo;
    private ProfileActivity activity;

    public ProfileActivityTest() {
        super(ProfileActivity.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
//        Intent intent = new Intent();
//        intent.putExtra(ProfileActivity.SELECTED_TAB,1);
//        setActivityIntent(intent);
        solo = new Solo(getInstrumentation(),getActivity());
        activity = (ProfileActivity) solo.getCurrentActivity();
    }

    @Override
    protected void tearDown() throws Exception {
        solo.finishOpenedActivities();
    }

    public void test1Init(){
//        assertEquals("must be 4 tabs",4,activity.getSupportActionBar().getTabCount());
//        assertEquals("must be 4 fragments", 4, activity.getViewingFragmets().length);
//        assertNotNull("arguments for subscriptions tab must be not null",activity.getFragmentArguments(3));
//        ViewPager pager = activity.getViewPager();
//        assertNotNull("view pager is null",pager);
//
//        assertEquals("wrong current fragment",1,pager.getCurrentItem());
//        assertEquals("wrong current tab",1,activity.getSupportActionBar().getSelectedTab());
    }

    public void test2Personal(){
//        ViewPager pager = activity.getViewPager();
//        solo.clickOnText(solo.getString(R.string.personal_data));
//        Fragment frgmt = ((FragmentPagerAdapter)pager.getAdapter()).getItem(0);
//        assertTrue("wrong fragment for personal data", solo.waitForFragmentByTag(frgmt.getTag()));
//        PersonalActivity perFrgmt = (PersonalActivity) frgmt;
//
//        Button btnSave = solo.getButton(solo.getString(R.string.save));
//        EditText etLastName = (EditText) solo.getView(R.id.etlastname);
//        EditText etFirstName = (EditText) solo.getView(R.id.etFirstname);
//        EditText etMiddlename = (EditText) solo.getView(R.id.etMiddlename);
//        EditText etEmail = (EditText) solo.getView(R.id.etEmail);
//        EditText etPhoneNumber = (EditText) solo.getView(R.id.etPhoneNumber);
//        EditText etDriveLicense = (EditText) solo.getView(R.id.etDriveLicense);
//
//        assertFalse("phone should be unmodifiable", etPhoneNumber.isEnabled());
//
//        String tmp = etLastName.getText().toString();
//        solo.enterText(etLastName,"_add");
//        assertTrue("save button should be available.etLastName",btnSave.isEnabled());
//        solo.clearEditText(etLastName);
//        solo.enterText(etLastName,tmp);
//        assertFalse("save button should be unavailable.etLastName", btnSave.isEnabled());
//
//        tmp = etFirstName.getText().toString();
//        solo.enterText(etFirstName,"_add");
//        assertTrue("save button should be available.etFirstName",btnSave.isEnabled());
//        solo.clearEditText(etFirstName);
//        solo.enterText(etFirstName,tmp);
//        assertFalse("save button should be unavailable.etFirstName",btnSave.isEnabled());
//
//        tmp = etMiddlename.getText().toString();
//        solo.enterText(etMiddlename,"_add");
//        assertTrue("save button should be available.etMiddlename",btnSave.isEnabled());
//        solo.clearEditText(etMiddlename);
//        solo.enterText(etMiddlename,tmp);
//        assertFalse("save button should be unavailable.etMiddlename",btnSave.isEnabled());
//
//        tmp = etEmail.getText().toString();
//        solo.enterText(etEmail,"_add");
//        assertTrue("save button should be available.etEmail",btnSave.isEnabled());
//        solo.clearEditText(etEmail);
//        solo.enterText(etEmail,tmp);
//        assertFalse("save button should be unavailable.etEmail",btnSave.isEnabled());
//
//        tmp = etDriveLicense.getText().toString();
//        solo.clearEditText(etDriveLicense);
//        solo.enterText(etDriveLicense,"123456789");
//        assertFalse("save button should be unavailable.etDriveLicense 9 symbols",btnSave.isEnabled());
//        solo.enterText(etDriveLicense,"0");
//        assertTrue("save button should be available.etDriveLicense", btnSave.isEnabled());
//        solo.clearEditText(etDriveLicense);
//        solo.enterText(etDriveLicense,tmp);
//        assertFalse("save button should be unavailable.etDriveLicense",btnSave.isEnabled());
//
//        boolean isConf = perFrgmt.isEmailConfirmed();
//        assertEquals("wrong email confirm availability", isConf,solo.getView(R.id.tvSentConfirmation).isEnabled());
//        assertEquals("wrong email confirm availability", isConf,solo.getView(R.id.btnSendInstruction).isEnabled());
    }
}
