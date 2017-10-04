package ru.mos.polls;

import android.app.ProgressDialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

import pub.devrel.easypermissions.EasyPermissions;
import ru.mos.elk.BaseActivity;

/**
 * Содержит общие методы для классов экранов, использвоать
 * для экранов с toolbar-ом
 */
public abstract class ToolbarAbstractActivity extends BaseActivity {
    protected Toolbar toolbar;
    protected ProgressDialog progressDialog;

    /**
     * Показывать ли в actionBar кнопку home
     *
     * @param enable - true - да, показывать
     */
    protected void setHome(boolean enable) {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            getSupportActionBar().setHomeButtonEnabled(enable);
            actionBar.setDisplayHomeAsUpEnabled(enable);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                System.out.println("onOptionsItemSelected ToolbarAbstractActivity");
                onBackPressed();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);
        init();
    }

    @Override
    public void setContentView(View view) {
        super.setContentView(view);
        init();
    }

    private void init() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        try {
            setSupportActionBar(toolbar);
            setHome(true);
            findViews();

        } catch (NullPointerException e) {

        }
    }

    protected void findViews() {
    }

    protected void startProgress() {
        progressDialog = new ProgressDialog(this);
        progressDialog.show();
    }

    protected void stopProgress() {
        /**
         * если активти уничтожено, возможен такой вид исключения
         */
        try {
            if (progressDialog != null) {
                progressDialog.dismiss();
            }
        } catch (WindowManager.BadTokenException ignored) {

        }
    }

    protected void displayFragment() {

    }

    public static void hideSoftInput(Context context, View view) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }
}
