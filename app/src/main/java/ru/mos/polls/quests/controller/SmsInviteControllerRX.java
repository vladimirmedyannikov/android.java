package ru.mos.polls.quests.controller;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import ru.mos.polls.AGApplication;
import ru.mos.polls.GoogleStatistics;
import ru.mos.polls.R;
import ru.mos.polls.Statistics;
import ru.mos.polls.base.activity.BaseActivity;
import ru.mos.polls.friend.ContactsManager;
import ru.mos.polls.friend.ui.utils.FriendGuiUtils;
import ru.mos.polls.quests.controller.service.SmsInviteNotice;
import ru.mos.polls.rxhttp.rxapi.handle.response.HandlerApiResponseSubscriber;
import ru.mos.polls.util.SMSUtils;

/**
 * @author matek3022 (semenovmm@altarix.ru)
 *         on 27.02.18.
 */

public class SmsInviteControllerRX {

    private static final int REQUEST_CODE = 1234;

    private BaseActivity activity;
    private ContactsManager contactsManager;
    private boolean isTask;

    public SmsInviteControllerRX(BaseActivity a) {
        activity = a;
        initContactsController();
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
                            contactsManager.chooseContact(activity);
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

    private void initContactsController() {
        contactsManager = new ContactsManager(activity);
        contactsManager.setCallback(new ContactsManager.Callback() {
            @Override
            public void onChooseContacts(String number) {
                number = FriendGuiUtils.formatPhone(number);
                sendSms("+" + number);
            }

            @Override
            public void onGetAllContacts(List<String> numbers) {
            }

            @Override
            public void onError(Exception e) {
            }
        });
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        contactsManager.onActivityResult(requestCode, resultCode, data);
    }

    private void sendSms(String phoneNumber) {
        ArrayList<String> smsBody = new ArrayList<String>(2);
        smsBody.add(activity.getString(R.string.sms_invite_text));
        smsBody.add(" " + activity.getString(R.string.sms_invate_url));
        SMSUtils.sendSMS(activity, phoneNumber, smsBody);
    }

    public void notifyBackEnd(String number) {
        number = number.replace("+", "").replace("(", "").replace(")", "").replace(" ", "").replace("-", "");
        List<String> phones = new ArrayList<>();
        phones.add(number);
        HandlerApiResponseSubscriber<SmsInviteNotice.Response.Result> handler = new HandlerApiResponseSubscriber<SmsInviteNotice.Response.Result>() {
            @Override
            protected void onResult(SmsInviteNotice.Response.Result result) {
                /**
                 * обработка успеха реализована в SMSUtils
                 */
                Statistics.afterSendInviteFriends(true);
                GoogleStatistics.QuestsFragment.afterSendInviteFriends(true);
            }

            @Override
            public void onError(Throwable throwable) {
                super.onError(throwable);
                Statistics.afterSendInviteFriends(false);
                GoogleStatistics.QuestsFragment.afterSendInviteFriends(false);
            }
        };
        activity.getDisposables().add(AGApplication
                .api
                .noticeSmsInvited(new SmsInviteNotice.Request(phones))
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(handler));
    }
}
