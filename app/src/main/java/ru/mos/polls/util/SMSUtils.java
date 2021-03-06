package ru.mos.polls.util;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;
import android.telephony.SmsManager;

import java.util.ArrayList;
import java.util.Collections;

import ru.mos.polls.R;

/**
 * Created by matek on 27.09.2017.
 */

public class SMSUtils {
    public static final String SUCCESS_SEND_INVITE_MESSAGE_FILTER = "ru.mos.polls.util.success_send_invite_message_filter";
    public static final String SENDING_PHONE_NUMBER = "ru.mos.polls.util.sending_phone_number";
    public static final String SENDING_MESSAGE_KEY = "ru.mos.polls.util.sending_message_key";

    private static final String DELIVER_RECEIVER_FILTER = "ru.mos.polls.util.deliver_receiver_filter";

    private static final BroadcastReceiver deliverReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String msg = intent.getStringExtra(SENDING_MESSAGE_KEY);
            switch (getResultCode()) {
                case Activity.RESULT_OK:
                    intent.setAction(SUCCESS_SEND_INVITE_MESSAGE_FILTER);
                    GuiUtils.displayOkMessage(context, String.format(context.getString(R.string.success_send_sms), msg), null);
                    LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
                    break;
                default:
                    GuiUtils.displayOkMessage(context, String.format(context.getString(R.string.error_send_sms), msg), null);
            }
            unregisterReceiver(context);
        }
    };

    public static void sendSMS(Context context, String phoneNumber, String[] text) {
        ArrayList<String> smsBody = new ArrayList<>();
        Collections.addAll(smsBody, text);
        sendSMS(context, phoneNumber, smsBody);
    }

    public static void sendSMS(Context context, String phoneNumber, ArrayList<String> text) {
        String stringText = AgTextUtil.listToString(text);
        Intent messageIntent = new Intent(DELIVER_RECEIVER_FILTER);
        messageIntent.putExtra(SENDING_MESSAGE_KEY, stringText);
        messageIntent.putExtra(SENDING_PHONE_NUMBER, phoneNumber);
        PendingIntent deliveredPI = PendingIntent.getBroadcast(context, 0, messageIntent, 0);
        ArrayList<PendingIntent> list = new ArrayList<>();
        list.add(deliveredPI);
        context.registerReceiver(deliverReceiver, new IntentFilter(DELIVER_RECEIVER_FILTER));
        SmsManager sms = SmsManager.getDefault();
        try {
            sms.sendMultipartTextMessage(phoneNumber,
                    null,
                    text,
                    new ArrayList<PendingIntent>(),
                    list);
        }catch (Exception e) {
            unregisterReceiver(context);
            GuiUtils.displayOkMessage(context, String.format(context.getString(R.string.error_send_sms), stringText), null);
        }
    }

    public static void unregisterReceiver(Context context) {
        try {
            if (deliverReceiver != null) {
                context.unregisterReceiver(deliverReceiver);
            }
        } catch (Exception ignored) {
        }
    }
}
