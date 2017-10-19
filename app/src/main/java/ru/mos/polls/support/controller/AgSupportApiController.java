package ru.mos.polls.support.controller;

import android.app.ProgressDialog;
import android.os.Build;
import android.widget.Toast;

import com.android.volley2.Response;
import com.android.volley2.VolleyError;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import ru.mos.elk.BaseActivity;
import ru.mos.elk.api.API;
import ru.mos.elk.netframework.request.JsonObjectRequest;
import ru.mos.elk.netframework.request.Session;
import ru.mos.elk.netframework.utils.StandartErrorListener;
import ru.mos.elk.push.GCMHelper;
import ru.mos.polls.BuildConfig;
import ru.mos.polls.R;
import ru.mos.polls.UrlManager;
import ru.mos.polls.support.model.FeedbackBody;
import ru.mos.polls.support.model.Subject;


public abstract class AgSupportApiController {

    public static void loadSubjects(final BaseActivity elkActivity, final SubjectsListener listener) {
        String url = API.getURL(UrlManager.url(UrlManager.Controller.SUPPORT, UrlManager.Methods.GET_FEEDBACK_SUBJECTS));
        Response.Listener<JSONObject> responseListener = new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                if (jsonObject != null && listener != null) {
                    JSONArray subjectsJson = jsonObject.optJSONArray("subjects");
                    List<Subject> subjects = Subject.fromJson(subjectsJson);
                    listener.onLoad(subjects);
                }
            }
        };
        Response.ErrorListener errorListener = new StandartErrorListener(elkActivity, R.string.error_occurs){
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                super.onErrorResponse(volleyError);
                Toast.makeText(elkActivity, volleyError.getMessage(), Toast.LENGTH_SHORT).show();
                if (listener != null) {
                    listener.onError();
                }
            }
        };
        elkActivity.addRequest(new JsonObjectRequest(url, null, responseListener, errorListener));
    }

    public static void sendFeedback(final BaseActivity elkActivity, FeedbackBody fbody, Subject subject, final SendListener listener) {
        final ProgressDialog progressDialog = new ProgressDialog(elkActivity);
        progressDialog.setMessage(elkActivity.getString(R.string.support_progress_message));
        progressDialog.show();

        String url = API.getURL(UrlManager.url(UrlManager.Controller.SUPPORT, UrlManager.Methods.SEND_FEEDBACK));
        Response.Listener<JSONObject> responseListener = new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                Toast.makeText(elkActivity, R.string.succeeded_support, Toast.LENGTH_SHORT).show();
                try {
                    if (progressDialog != null) {
                        progressDialog.dismiss();
                    }
                } catch (Exception ignored) {
                }
                if (listener != null) {
                    listener.onSuccess();
                }
            }
        };
        Response.ErrorListener errorListener = new StandartErrorListener(elkActivity, R.string.error_occurs) {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                super.onErrorResponse(volleyError);
                Toast.makeText(elkActivity, volleyError.getMessage(), Toast.LENGTH_SHORT).show();
                try {
                    if (progressDialog != null) {
                        progressDialog.dismiss();
                    }
                } catch (Exception ignored) {
                }
                if (listener != null) {
                    listener.onError(volleyError);
                }
            }
        };
        JSONObject requestBody = getFeedbackParams(elkActivity, fbody, subject);
        elkActivity.addRequest(new JsonObjectRequest(url, requestBody, responseListener, errorListener));
    }

    private static JSONObject getFeedbackParams(BaseActivity elkActivity, FeedbackBody fbody, Subject subject) {
        JSONObject params = new JSONObject();
        try {
            //main
            params.put("message", fbody.getMessage());
            params.put("email", fbody.getEmail());
            if (fbody.getOrderNumber() != 0) { //если номер не задан то дефаултная инициализация равно 0
                params.put("order_number", fbody.getOrderNumber());
            }
            params.put("subject_id", subject.getId());
            //additional params
            JSONObject userInfo = new JSONObject();
            userInfo.put("app", "iSuperCitizen");
            userInfo.put("app_version", BuildConfig.VERSION_NAME);
            userInfo.put("os", "Android " + Build.VERSION.RELEASE + " (SDK " + Build.VERSION.SDK_INT + ")");
            userInfo.put("device", Build.MODEL + " (" + Build.MANUFACTURER + ")");
            params.put("user_info", userInfo);
            userInfo.put("session_id", Session.getSession(elkActivity));
            //apis
            JSONArray apiVersion = new JSONArray();
            apiVersion.put(GCMHelper.REGISTER_PATH);
            apiVersion.put(UrlManager.url(UrlManager.Controller.SUPPORT, UrlManager.Methods.SEND_FEEDBACK));
//            APIGroup[] groups = APIGroup.values();
//            for (int i = 0; i < groups.length; i++) {
//                if (groups[i] != APIGroup.OTHER)
//                    apiVersion.put(groups[i].getPath());
//            }
            params.put("api_version", apiVersion);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return params;
    }

    public interface SubjectsListener {
        void onLoad(List<Subject> subjects);

        void onError();
    }

    public interface SendListener {
        void onSuccess();

        void onError(VolleyError volleyError);
    }
}
