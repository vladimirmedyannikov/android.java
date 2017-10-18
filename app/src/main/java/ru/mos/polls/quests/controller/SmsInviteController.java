package ru.mos.polls.quests.controller;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.widget.Toast;

import com.android.volley2.Response;
import com.android.volley2.VolleyError;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import ru.mos.elk.BaseActivity;
import ru.mos.elk.api.API;
import ru.mos.elk.netframework.request.JsonObjectRequest;
import ru.mos.polls.GoogleStatistics;
import ru.mos.polls.R;
import ru.mos.polls.Statistics;
import ru.mos.polls.UrlManager;
import ru.mos.polls.util.SMSUtils;

public class SmsInviteController {

    private static final int REQUEST_CODE = 1234;

    private BaseActivity activity;
    private boolean isTask;

    public SmsInviteController(BaseActivity a) {
        activity = a;
    }

    public void process(boolean isTask) {
        this.isTask = isTask;
        Statistics.inviteFriends();
        GoogleStatistics.QuestsFragment.inviteFriends();
        new AlertDialog.Builder(activity).
                setMessage(R.string.quest_invite_warning).
                setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (activity.getPackageManager().hasSystemFeature(PackageManager.FEATURE_TELEPHONY)) {
                            Statistics.beforeSendInviteFriends();
                            GoogleStatistics.QuestsFragment.beforeSendInviteFriends(1);
                            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                            intent.setType(ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE);
                            activity.startActivityForResult(intent, REQUEST_CODE);
                        } else {
                            Toast.makeText(activity, R.string.sms_could_not_send, Toast.LENGTH_SHORT).show();
                        }
                    }
                }).
                setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }).show();
    }

    public boolean onActivityResult(int requestCode, int resultCode, Intent data) {
        boolean result = false;
        if (requestCode == REQUEST_CODE) {
            if (resultCode == BaseActivity.RESULT_OK) {
                if (data != null) {
                    Uri uri = data.getData();
                    if (uri != null) {
                        Cursor c = null;
                        try {
                            c = activity.getContentResolver().query(uri, new String[]{ContactsContract.CommonDataKinds.Phone.NUMBER, ContactsContract.CommonDataKinds.Phone.TYPE}, null, null, null); // на android c API 10 замечена особенность в поле ContactsContract.CommonDataKinds.Phone.NORMALIZED_NUMBER - храниться номер в обратном порядке; видимо, так в обратном порядке быстрее хранить удобнее для ускорение выборки http://stackoverflow.com/questions/4579009/android-why-number-key-return-the-number-in-reverse-order
                            if (c != null && c.moveToFirst()) {
                                String number = c.getString(0);
                                try {
                                    sendSms(number);
                                    /**
                                     * обработка ошибок и успеха реализована в SMSUtils
                                     */
                                } catch (Exception ignored) {

                                }
                            }
                        } finally {
                            if (c != null) {
                                c.close();
                            }
                        }
                    }
                    result = true;
                }
            }
        }
        return result;
    }

    private void sendSms(String phoneNumber) {
        ArrayList<String> smsBody = new ArrayList<String>(2);
        smsBody.add(activity.getString(R.string.sms_invite_text));
        smsBody.add(" " + activity.getString(R.string.sms_invate_url));
        SMSUtils.sendSMS(activity, phoneNumber, smsBody);
    }

    public void notifyBackEnd(String number) {
        number = number.replace("+", "").replace("(", "").replace(")", "").replace(" ", "").replace("-", "");
        String url = API.getURL(UrlManager.url(UrlManager.Controller.POLLTASK, UrlManager.Methods.SMS_INVITATION_NOTICE));
        JSONObject jsonRequest = new JSONObject();
        JSONArray phonesJsonArray = new JSONArray();
        phonesJsonArray.put(number);
        try {
            jsonRequest.put("phones", phonesJsonArray);
        } catch (JSONException e) {
        }
        Response.Listener<JSONObject> listener = new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                /**
                * обработка успеха реализована в SMSUtils
                */
                Statistics.afterSendInviteFriends(true);
                GoogleStatistics.QuestsFragment.afterSendInviteFriends(true);
            }
        };
        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                builder.setMessage(volleyError.getMessage());
                builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
                builder.show();
                Statistics.afterSendInviteFriends(false);
                GoogleStatistics.QuestsFragment.afterSendInviteFriends(false);
            }
        };
        JsonObjectRequest request = new JsonObjectRequest(url, jsonRequest, listener, errorListener);
        activity.addRequest(request);
    }
}
