package ru.mos.polls.util;

import android.app.PendingIntent;
import android.content.Context;
import android.telephony.SmsManager;

import java.util.ArrayList;
import java.util.Collections;

import ru.mos.polls.R;

/**
 * Created by matek on 27.09.2017.
 */

public class SMSUtils {
    public static void sendSMS(Context context, String phoneNumber, String[] text) {
        ArrayList<String> smsBody = new ArrayList<>();
        Collections.addAll(smsBody, text);
        sendSMS(context, phoneNumber, smsBody);
    }

    public static void sendSMS(Context context, String phoneNumber, ArrayList<String> text) {
        String stringText = "";
        for (int i = 0; i < text.size(); i++) {
            stringText += text.get(i);
            if (i != text.size() - 1) stringText += "\n";
        }
        try {
            SmsManager sms = SmsManager.getDefault();
            sms.sendMultipartTextMessage(phoneNumber,
                    null,
                    text,
                    new ArrayList<PendingIntent>(),
                    new ArrayList<PendingIntent>());
            GuiUtils.displayOkMessage(context, String.format(context.getString(R.string.success_send_sms), stringText), null);
        } catch (Exception e) {
            GuiUtils.displayOkMessage(context, String.format(context.getString(R.string.error_send_sms), stringText), null);
        }
    }
}
