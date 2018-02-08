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

import com.android.volley2.Response;
import com.android.volley2.VolleyError;

import org.json.JSONException;
import org.json.JSONObject;

import ru.mos.elk.netframework.request.JsonObjectRequest;
import ru.mos.elk.netframework.request.Session;
import ru.mos.elk.netframework.utils.StandartErrorListener;
import ru.mos.polls.api.API;
import ru.mos.polls.base.activity.BaseActivity;
import ru.mos.polls.common.model.Message;
import ru.mos.polls.helpers.TextHelper;

/**
 * Инкапсулирует логику работы с промо-кодами<br/>
 * Диалог для ввода промокода {@link #showInputDialog(BaseActivity)}<br/>
 * Добавление промокода на сервере АГ {@link #notifyServer(BaseActivity, String)}
 */
public abstract class PromoController {

    private static ProgressDialog progressDialog;
    public static PromoResponse promolistener;

    public static void setPromoResponse(PromoResponse promoResponse) {
        promolistener = promoResponse;
    }

    /**
     * Диалог для ввода промокода
     *
     * @param activity текущий контекст {@link BaseActivity}
     */
    public static void showInputDialog(final BaseActivity activity) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(activity);

        View innerView = View.inflate(activity, R.layout.layout_promo, null);
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
                    Toast.makeText(activity, R.string.promo_is_empty, Toast.LENGTH_LONG).show();
                    hideSoftInput(activity, inputEditText);
                    showInputDialog(activity);
                    return;
                }
                /**
                 * проверка форматат промо-кода
                 */
                if (!TextHelper.isPromoValid(promo)) {
                    Toast.makeText(activity, R.string.promo_contains_wrong_symbols, Toast.LENGTH_SHORT).show();
                    hideSoftInput(activity, inputEditText);
                    showInputDialog(activity);
                    return;
                }
                notifyServer(activity, promo);
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
     * @param activity  текущий контекст {@link BaseActivity}
     * @param promoCode текст промокода
     */
    private static void notifyServer(final BaseActivity activity, final String promoCode) {
        startProgress(activity);
        String url = API.getURL(UrlManager.url(UrlManager.Controller.PROMOCODE, UrlManager.Methods.ADD_CODE));
        JSONObject requestJson = getRequestJson(promoCode);
        Response.Listener<JSONObject> responseListener = new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                dismissProgress();
                parseResult(activity, promoCode, jsonObject);
                if (promolistener != null) promolistener.onResponse();
            }
        };
        Response.ErrorListener errorListener = new StandartErrorListener(activity, R.string.error_occurs) {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                super.onErrorResponse(volleyError);
                dismissProgress();
                String error = String.format(activity.getString(R.string.error_occurs), volleyError.getMessage());
                Toast.makeText(activity, error, Toast.LENGTH_LONG).show();
                if (promolistener != null) promolistener.onError();
            }
        };
        JsonObjectRequest jsonArrayRequest = new JsonObjectRequest(url, requestJson, responseListener, errorListener);
        activity.addRequest(jsonArrayRequest);
    }

    private static void startProgress(Context context) {
        progressDialog = new ProgressDialog(context);
        progressDialog.show();
    }

    private static void dismissProgress() {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }

    private static JSONObject getRequestJson(String promoCode) {
        JSONObject requestJson = new JSONObject();
        Session.addSession(requestJson);
        try {
            requestJson.put("user_agent", "android");
            requestJson.put("code_name", promoCode);
        } catch (JSONException ignored) {
        }
        return requestJson;
    }

    /**
     * Обработка ответа сервера при успешном выполнении {@link #notifyServer(BaseActivity, String)}
     *
     * @param context   текущий контекст
     * @param promoCode строка промокода
     * @param response  json ответ сервера
     */
    private static void parseResult(Context context, String promoCode, JSONObject response) {
        if (response == null) {
            Toast.makeText(context, R.string.error_general, Toast.LENGTH_SHORT).show();
        } else {
            JSONObject jsonObject = response.optJSONObject("message");
            Message message = null;
            if (jsonObject != null) {
                message = new Message(jsonObject.optString("title"), jsonObject.optString("text"));
            }
            if (message != null && !message.isEmpty()) {
                message.showDialog(context);
            } else {
                JSONObject status = response.optJSONObject("status");
                int addedPoints = status.optInt("added_points");
                int currentPoints = status.optInt("current_points");
                showDefaultDialog(context, addedPoints, currentPoints, promoCode);
            }
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
        builder.setMessage(String.format(context.getString(R.string.code_accepted_toast_msg), addedPoints));
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
