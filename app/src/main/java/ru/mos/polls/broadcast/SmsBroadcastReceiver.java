package ru.mos.polls.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Telephony;
import android.support.v4.content.LocalBroadcastManager;
import android.telephony.SmsMessage;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ru.mos.polls.R;

/**
 * Инкапсулирует логику обработки получения смс с кодом подтверждения<br/>
 * Полученный код отправляется интентом {@link LocalBroadcastManager} на экран
 * @since 1.0
 */
public class SmsBroadcastReceiver extends BroadcastReceiver {
    public static final String ACTION_CODE_CONFIRMATION_RECEIVED = "ACTION_CODE_CONFIRMATION_RECEIVED";
    public static final String EXTRA_CODE = "extra_code";
    private static final String PDUS = "pdus";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (Telephony.Sms.Intents.SMS_RECEIVED_ACTION.equalsIgnoreCase(intent.getAction())) {
            SmsMessage smsMessage = get(intent);
            if (checkServiceFrom(context, smsMessage)) {
                String code = parse(smsMessage.getDisplayMessageBody(),
                        context.getString(R.string.sms_code_from_body_pattern));
                notify(context, code);
            }
        }
    }

    /**
     * Отправка локальным широковещательным запросом {@link LocalBroadcastManager} кода смс
     * @param context текущий контекст
     * @param code код подтверждения из смс {@link #parse(String, String)}
     */
    private void notify(Context context, String code) {
        Intent broadcast = new Intent();
        broadcast.setAction(ACTION_CODE_CONFIRMATION_RECEIVED);
        broadcast.putExtra(EXTRA_CODE, code);
        LocalBroadcastManager.getInstance(context).sendBroadcast(broadcast);
    }

    /**
     * Проверка от кого пришло смс<br/>
     * ОБрабатываем смс отправителя
     * @param smsMessage смс
     * @return true - смс от отправителя, заданного константой
     */
    private boolean checkServiceFrom(Context context, SmsMessage smsMessage) {
        return smsMessage != null &&
                context.getString(R.string.sms_sender).equalsIgnoreCase(smsMessage.getOriginatingAddress());
    }

    /**
     * Получение объекта смс
     * @param intent интент, содержащий полученное смс
     * @return смс
     */
    private SmsMessage get(Intent intent) {
        SmsMessage result = null;
        Bundle bundle = intent.getExtras();
        if (bundle != null) {
            Object[] pdus = (Object[])bundle.get(PDUS);
            if (pdus != null) {
                final SmsMessage[] messages = new SmsMessage[pdus.length];
                for (int i = 0; i < pdus.length; i++) {
                    messages[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
                }
                if (messages.length >= 1) {
                    result = messages[0];
                }
            }
        }
        return result;
    }

    /**
     * Получение пода из тела смс
     * @param target строка текста смс
     * @param p паттерн, описывающий искомый код
     * @return код подтверждения
     */
    private String parse(String target, String p) {
        String result = "";
        Pattern pattern = Pattern.compile(p);
        Matcher matcher = pattern.matcher(target);
        if (matcher.find()) {
            result = matcher.group(0);
        }
        return result;
    }
}
