package ru.mos.polls.friend;

import android.app.Activity;
import android.app.Fragment;
import android.app.PendingIntent;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.telephony.SmsManager;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Работа с контактами устройства {@link ContactsContract}
 *
 * @author Sergey Elizarov (elizarov1988@gmail.com)
 *         on 01.08.17 22:14.
 */

public class ContactsController {
    private static final int REQUEST_CODE = 1234;

    private Fragment fragment;

    public ContactsController(Fragment fragment) {
        this.fragment = fragment;
    }

    /**
     * Выбор одного контакта из телефонной книги, используется
     * в сочетании с {@link #onActivityResult(int, int, Intent)}
     */
    public void chooseOneContact() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType(ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE);
        fragment.startActivityForResult(intent, REQUEST_CODE);
    }

    /**
     * Отправка {@link android.provider.Telephony.Sms}
     *
     * @param phoneNumber {@link String} телефонный номер
     * @param text массив строк сообщения
     */
    private void sms(String phoneNumber, String[] text) {
        SmsManager sms = SmsManager.getDefault();
        ArrayList<String> smsBody = new ArrayList<>();
        Collections.addAll(smsBody, text);
        sms.sendMultipartTextMessage(phoneNumber,
                null,
                smsBody,
                new ArrayList<PendingIntent>(),
                new ArrayList<PendingIntent>());
    }

    public boolean onActivityResult(int requestCode, int resultCode, Intent data) {
        boolean result = false;
        if (requestCode == REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                if (data != null) {
                    Uri uri = data.getData();
                    if (uri != null) {
                        Cursor c = null;
                        try {
                            /**
                             * на android c API 10 замечена особенность в поле
                             * ContactsContract.CommonDataKinds.Phone.NORMALIZED_NUMBER
                             * - храниться номер в обратном порядке; видимо, так в обратном порядке
                             * быстрее хранить удобнее для ускорение выборки
                             *
                             * @see <a href="http://stackoverflow.com/questions/4579009/android-why-number-key-return-the-number-in-reverse-order">* на android c API 10 замечена особенность в поле ContactsContract.CommonDataKinds.Phone.NORMALIZED_NUMBER - храниться номер в обратном порядке; видимо, так в обратном порядке быстрее хранить удобнее для ускорение выборки </a>
                             */
                            String[] projection = new String[]{ContactsContract.CommonDataKinds.Phone.NUMBER,
                                    ContactsContract.CommonDataKinds.Phone.TYPE};
                            c = fragment.getActivity().getContentResolver().query(uri,
                                    projection,
                                    null,
                                    null,
                                    null);
                            if (c != null && c.moveToFirst()) {
                                String number = c.getString(0);
                                try {
                                    //todo
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

}
