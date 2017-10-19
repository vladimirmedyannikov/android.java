package ru.mos.elk.netframework.utils;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;
import android.widget.Toast;

import com.android.volley2.Response.ErrorListener;
import com.android.volley2.VolleyError;

import ru.mos.elk.BaseActivity;
import ru.mos.elk.Dialogs;

public class StandartErrorListener implements ErrorListener {

    private int errorMessageId;
    private Context context;
    protected ProgressDialog progressDialog;

    public StandartErrorListener(Context context, int errorMessageId) {
        this(context, errorMessageId, null);
    }

    public StandartErrorListener(Context context, int errorMessageId, ProgressDialog progressDialog) {
        this.errorMessageId = errorMessageId;
        this.context = context;
        this.progressDialog = progressDialog;
    }

    @Override
    @SuppressLint("NewApi")
    public void onErrorResponse(VolleyError error) {
        if (error.getErrorCode() == 401 || error.getErrorCode() == 403) {
            context.sendBroadcast(new Intent(BaseActivity.INTENT_LOGOUT));
            return;
        }
//        if (context instanceof AppCompatActivity) {
//            AppCompatActivity act = (AppCompatActivity) context;
//            if (act.isFinishing())
//                Toast.makeText(context, buildErrorMessage(error.getMessage()), Toast.LENGTH_LONG).show();
//            else {
//                if (Build.VERSION.SDK_INT > Build.VERSION_CODES.GINGERBREAD_MR1 && act.getWindow().hasFeature(Window.FEATURE_INDETERMINATE_PROGRESS)) //FIXME API LEVEL 11 REQUIRED!
//                    act.setSupportProgressBarIndeterminateVisibility(false);
//                Dialogs.showAlertMessage(context, buildErrorMessage(error.getMessage()));
//            }
//        } else
//            Toast.makeText(context, buildErrorMessage(error.getMessage()), Toast.LENGTH_LONG).show();
        if (progressDialog != null)
            progressDialog.dismiss();
    }

    protected String buildErrorMessage(String reason) {
        return errorMessageId != 0 ? context.getString(errorMessageId, reason) : "";
    }
}
