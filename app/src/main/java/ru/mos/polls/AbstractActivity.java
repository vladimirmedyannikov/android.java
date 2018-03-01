package ru.mos.polls;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;


import ru.mos.polls.base.activity.BaseActivity;


/**
 * Инкапсулирует общие, нужные методы
 * <p/>
 */
public abstract class AbstractActivity extends BaseActivity {
    protected ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /**
         * По умолчанию задаем кнопку home
         */
        setHome(true);
    }

    /**
     * Показывать ли в actionBar кнопку home
     *
     * @param enable - true - да, показывать
     */
    protected void setHome(boolean enable) {
        getSupportActionBar().setHomeButtonEnabled(enable);
        getSupportActionBar().setDisplayHomeAsUpEnabled(enable);
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
        findViews();
    }

    /**
     * При переопределении можно здесь определить views
     * Хотя необязательно (используется для повышения читабельности кода)
     */
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

    public static void hideSoftInput(Context context, View view) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (view != null) {
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

}
