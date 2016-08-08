package ru.mos.polls;

import android.app.ProgressDialog;
import android.content.Context;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

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
        setSupportActionBar(toolbar);
        setHome(true);
        findViews();
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
}
