package ru.mos.elk;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnClickListener;


public class Dialogs {

    public static AlertDialog showAlertMessage(Context context, int errorStringId) {
        return showAlertMessage(context, null, context.getString(errorStringId), null);
    }

    public static AlertDialog showAlertMessage(Context context, String errorString) {
        return showAlertMessage(context, null, errorString, null);
    }

    public static AlertDialog showAlertMessage(Context context, int titleId, int errorStringId, OnClickListener okListener) {
        return showAlertMessage(context, context.getString(titleId), context.getString(errorStringId), okListener);
    }

    public static AlertDialog showAlertMessage(Context context, String title, String errorString, OnClickListener okListener) {
        return showAlertMessage(context, title, errorString, context.getString(android.R.string.ok), okListener);
    }

    public static AlertDialog showAlertMessage(Context context, int titleId, int errorStringId, int okId, OnClickListener okListener) {
        return showAlertMessage(context, context.getString(titleId), context.getString(errorStringId), context.getString(okId), okListener);
    }

    public static AlertDialog showAlertMessage(Context context, String title, String errorString, String okString, OnClickListener okListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        if (title != null)
            builder.setTitle(title);
        builder.setMessage(errorString);
        builder.setPositiveButton(okString, okListener);
        return builder.show();
    }

    public static AlertDialog showYesNoDialog(Context context, int titleId, int messageId, int yesStringId, int noStringId, OnClickListener listener, OnCancelListener cancelListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        if(titleId!=-1)
        	builder.setTitle(titleId);
       	builder.setMessage(messageId);
        builder.setPositiveButton(yesStringId, listener);
        builder.setNegativeButton(noStringId, listener);
        builder.setOnCancelListener(cancelListener);
        return builder.show();
    }
    
    public static AlertDialog showYesNoDialog(Context context, String title, String message, String yesString, String noString, OnClickListener listener, OnCancelListener cancelListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setPositiveButton(yesString, listener);
        builder.setNegativeButton(noString, listener);
        builder.setOnCancelListener(cancelListener);
        return builder.show();
    }    


    public static ProgressDialog showProgressDialog(Context context, int messageId) {
        return showProgressDialog(context,context.getString(messageId));
    }
    
	public static ProgressDialog showProgressDialog(Context context, String message) {
        ProgressDialog d = new ProgressDialog(context);
        d.setCancelable(true);
        d.setTitle(null);
        d.setMessage(message);
        d.setIndeterminate(true);
        d.show();
        return d;
	}
}
