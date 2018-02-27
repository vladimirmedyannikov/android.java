package ru.mos.polls;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import ru.mos.polls.helpers.TextHelper;
import ru.mos.polls.rxhttp.rxapi.handle.response.HandlerApiResponseSubscriber;
import ru.mos.polls.rxhttp.rxapi.progreessable.Progressable;
import ru.mos.polls.service.PromoAddCode;

/**
 * Инкапсулирует логику работы с промо-кодами<br/>
 * Диалог для ввода промокода {@link #showInputDialog(CompositeDisposable, Context, Progressable)}<br/>
 * Добавление промокода на сервере АГ {@link #notifyServer(CompositeDisposable, Context, String, Progressable)}
 */
public class PromoControllerRX {

    private static ProgressDialog pd;
    public static PromoResponse promolistener;

    public static void setPromoResponse(PromoResponse promoResponse) {
        promolistener = promoResponse;
    }

    /**
     * Диалог для ввода промокода
     *
     */
    public static void showInputDialog(CompositeDisposable disposable, Context context, Progressable progressable) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(context);

        View innerView = View.inflate(context, R.layout.layout_promo, null);
        final EditText inputEditText = (EditText) innerView.findViewById(R.id.promocode);
        builder.setView(innerView);

        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });

        builder.setPositiveButton(R.string.survey_done_ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String promo = inputEditText.getText().toString().trim();
                if (TextUtils.isEmpty(promo)) {
                    Toast.makeText(context, R.string.promo_is_empty, Toast.LENGTH_LONG).show();
                    hideSoftInput(context, inputEditText);
                    showInputDialog(disposable, context, progressable);
                    return;
                }
                /**
                 * проверка форматат промо-кода
                 */
                if (!TextHelper.isPromoValid(promo)) {
                    Toast.makeText(context, R.string.promo_contains_wrong_symbols, Toast.LENGTH_SHORT).show();
                    hideSoftInput(context, inputEditText);
                    showInputDialog(disposable, context, progressable);
                    return;
                }
                notifyServer(disposable, context, promo, progressable);
            }
        });

        final AlertDialog dialog = builder.create();
        inputEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
                }
            }
        });

        dialog.show();
    }

    /**
     * Уведомление сервера АГ о добавлении промокода
     *
     * @param promoCode текст промокода
     */
    private static void notifyServer(CompositeDisposable disposable, Context context, final String promoCode, Progressable progressable) {
        if (progressable == null) {
            progressable = new Progressable() {
                @Override
                public void begin() {
                    showProgress(context, "Отправляем...");
                }

                @Override
                public void end() {
                    hideProgress();
                }
            };
        }
        HandlerApiResponseSubscriber<PromoAddCode.Response.Result> handler = new HandlerApiResponseSubscriber<PromoAddCode.Response.Result>(context, progressable) {
            @Override
            protected void onResult(PromoAddCode.Response.Result result) {
                if (promolistener != null) promolistener.onResponse();
                if (result.getMessage() != null && !result.getMessage().isEmpty()) {
                    result.getMessage().showDialog(context);
                } else {
                    showDefaultDialog(context, result.getStatus().getAddedPoints(), result.getStatus().getCurrentPoints(), promoCode);
                }
            }

            @Override
            public void onError(Throwable throwable) {
                super.onError(throwable);
            }
        };
        disposable.add(AGApplication
                .api
                .addCode(new PromoAddCode.Request(promoCode))
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(handler));
    }

    private static void showProgress(Context context, String message) {
        pd = new ProgressDialog(context);
        if (!TextUtils.isEmpty(message) && !"null".equalsIgnoreCase(message)) {
            pd.setMessage(message);
        }
        pd.show();
    }

    private static void hideProgress() {
        try {
            if (pd != null) {
                pd.dismiss();
            }
        } catch (Exception ignored) {
        }
    }

    /**
     * Диалог отображаемый пользователю по умолчанию
     *
     * @param context       текущий контекст
     * @param addedPoints   количество добавленных баллов за отправку промокода
     * @param currentPoints текущее количество баллов
     * @param promoCode     текст промокода
     */
    private static void showDefaultDialog(Context context, int addedPoints, int currentPoints, String promoCode) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(R.string.thanks);
        builder.setMessage(String.format(context.getString(R.string.code_accepted_toast_msg), String.valueOf(addedPoints)));
        builder.setPositiveButton(R.string.survey_done_ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        builder.show();
    }

    private static void hideSoftInput(Context context, View view) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public interface PromoResponse {
        void onResponse();

        void onError();
    }
}