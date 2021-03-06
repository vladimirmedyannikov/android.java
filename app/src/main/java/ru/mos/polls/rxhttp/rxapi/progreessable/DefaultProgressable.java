package ru.mos.polls.rxhttp.rxapi.progreessable;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Handler;

/**
 * Прогресс загрузки поумолчанию
 * Прогресс отображается средствами {@link ProgressDialog}
 * @since 1.0
 * Created by Sergey Elizarov (sergey.elizarov@altarix.ru)
 * on 13.10.15 13:12.
 */
public class DefaultProgressable implements Progressable {
    private final Context context;
    private ProgressDialog progressDialog;

    private String message = "Загрузка..";

    public DefaultProgressable(Context context) {
        this.context = context;
    }

    @Override
    public void begin() {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(context);
        }
        progressDialog.setMessage(message);
        progressDialog.show();
    }

    @Override
    public void end() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                dismiss();
            }
        }, 500);
    }

    public void setMessage(String message) {
        this.message = message;
    }

    private void dismiss() {
        message = "Загрузка";
        try {
            if (progressDialog != null) {
                progressDialog.dismiss();
            }
        } catch (Exception ignored) {
        }
    }
}
