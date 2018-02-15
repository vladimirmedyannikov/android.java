package ru.mos.elk.netframework.utils;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;

import com.android.volley2.Response.ErrorListener;
import com.android.volley2.VolleyError;

import ru.mos.elk.BaseActivity;
import ru.mos.elk.netframework.request.ResponseErrorCode;

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
        if (error.getErrorCode() == ResponseErrorCode.UNAUTHORIZED || error.getErrorCode() == ResponseErrorCode.SESSION_DOWN) {
            context.sendBroadcast(new Intent(BaseActivity.INTENT_LOGOUT));
            return;
        }
        if (progressDialog != null)
            progressDialog.dismiss();
    }

    protected String buildErrorMessage(String reason) {
        return errorMessageId != 0 ? context.getString(errorMessageId, reason) : "";
    }
}
