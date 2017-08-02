package ru.mos.polls.friend;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.support.v4.app.Fragment;
import android.telephony.SmsManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Работа с контактами устройства {@link ContactsContract}
 *
 * @author Sergey Elizarov (elizarov1988@gmail.com)
 *         on 01.08.17 22:14.
 */

public class ContactsController {
    private static final int REQUEST_CODE = 1234;

    private Fragment fragment;
    private Callback callback;

    public ContactsController(Fragment fragment) {
        this.fragment = fragment;
    }

    public void setCallback(Callback callback) {
        if (callback == null) {
            this.callback = Callback.STUB;
        } else {
            this.callback = callback;
        }
    }

    /**
     * Выбор одного контакта из телефонной книги, используется
     * в сочетании с {@link #onActivityResult(int, int, Intent)}
     */
    public void chooseContact() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType(ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE);
        fragment.startActivityForResult(intent, REQUEST_CODE);
    }

    /**
     * Отправка {@link android.provider.Telephony.Sms}
     *
     * @param phoneNumber {@link String} телефонный номер
     * @param text        массив строк сообщения
     */
    public void sms(String phoneNumber, String[] text) {
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
                        Cursor cursor = null;
                        try {
                            String[] projection = new String[]{ContactsContract.CommonDataKinds.Phone.NUMBER,
                                    ContactsContract.CommonDataKinds.Phone.TYPE};
                            cursor = fragment.getActivity().getContentResolver().query(uri,
                                    projection,
                                    null,
                                    null,
                                    null);
                            if (cursor != null && cursor.moveToFirst()) {
                                String number = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                                callback.onChooseContacts(number);
                            }
                        } catch (Exception e) {
                            callback.onError(e);
                        } finally {
                            if (cursor != null) {
                                cursor.close();
                            }
                        }
                    }
                    result = true;
                }
            }
        }
        return result;
    }

    public interface Callback {
        Callback STUB = new Callback() {
            @Override
            public void onChooseContacts(String number) {
            }

            @Override
            public void onGetAllContacts(List<String> numbers) {
            }

            @Override
            public void onError(Exception e) {
            }
        };

        void onChooseContacts(String number);

        void onGetAllContacts(List<String> numbers);

        void onError(Exception e);
    }

}
