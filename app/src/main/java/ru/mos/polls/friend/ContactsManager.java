package ru.mos.polls.friend;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.support.v4.app.Fragment;

import java.util.ArrayList;
import java.util.List;

import ru.mos.polls.util.SMSUtils;

/**
 * Работа с контактами устройства {@link ContactsContract}
 *
 * @author Sergey Elizarov (elizarov1988@gmail.com)
 *         on 01.08.17 22:14.
 */

public class ContactsManager {
    private static final int REQUEST_CODE = 1234;

    private Context context;
    private Callback callback;

    public ContactsManager(Context context) {
        this.context = context;
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
    public void chooseContact(Fragment fragment) {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType(ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE);
        fragment.startActivityForResult(intent, REQUEST_CODE);
    }

    /**
     * Отправка {@link android.provider.Telephony.Sms}
     *
     * @param phoneNumber {@link String} телефонный номер
     * @param text        массив строк сообщения
     */
    public void sms(String phoneNumber, String[] text) {
        SMSUtils.sendSMS(context, "+" + phoneNumber, text);
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
                            cursor = context.getContentResolver().query(uri,
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

    /**
     * Получение списка контактов {@link Callback#onGetAllContacts(List)}
     * Необхоимо наличие {@link android.Manifest.permission#READ_CONTACTS}
     */
    public List<String> loadContacts() {
        List<String> result = new ArrayList<>();
        Cursor cursor = context.getContentResolver()
                .query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            do {
                String phone = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                result.add(phone);
            } while (cursor.moveToNext());
            cursor.close();
        }
        if (callback != null) {
            callback.onGetAllContacts(result);
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
