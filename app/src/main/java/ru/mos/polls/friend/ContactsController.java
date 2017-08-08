package ru.mos.polls.friend;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import ru.mos.polls.AGApplication;
import ru.mos.polls.friend.ui.FriendGuiUtils;
import ru.mos.polls.rxhttp.rxapi.handle.response.HandlerApiResponseSubscriber;
import ru.mos.polls.rxhttp.rxapi.model.friends.Friend;
import ru.mos.polls.rxhttp.rxapi.model.friends.service.FriendFind;
import ru.mos.polls.rxhttp.rxapi.model.friends.service.FriendMy;

/**
 * Логика поиска друзей {@link #silentFindFriends()}
 *
 * @author Sergey Elizarov (elizarov1988@gmail.com)
 *         on 07.08.17 17:35.
 */

public class ContactsController {
    private ContactsManager contactsManager;

    public ContactsController(Context context) {
        contactsManager = new ContactsManager(context);
    }

    /**
     * Поиск друзей {@link Friend} {@link FriendFind} по номерам телефонов из справочника {@link ContactsManager}
     * </br>
     * Поиск осуществляется только по номерам телефонов, которые найдены среди аккаунтов друзей
     */
    public void silentFindFriends() {
        HandlerApiResponseSubscriber<FriendMy.Response.Result> handler = new HandlerApiResponseSubscriber<FriendMy.Response.Result>() {
            @Override
            protected void onResult(FriendMy.Response.Result result) {
                onMyFriendsLoaded(result.getFriends());
            }
        };

        AGApplication
                .api
                .friendMy(new FriendMy.Request())
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(handler);

    }

    private void onMyFriendsLoaded(List<Friend> friends) {
        contactsManager.setCallback(new ContactsManager.Callback() {
            @Override
            public void onChooseContacts(String number) {
            }

            @Override
            public void onGetAllContacts(List<String> numbers) {
                List<String> result = new ArrayList<>();
                for (String number : numbers) {
                    number = FriendGuiUtils.formatPhone(number);
                    if (!isPhoneFriend(number, friends)
                            && FriendGuiUtils.isPhoneValid(number)
                            && !result.contains(number)) {
                        result.add(number);
                    }
                }
                List<List<String>> subLists = toSubLists(result, 20);
                for (List<String> subList : subLists) {
                    AGApplication
                            .api
                            .friendFind(new FriendFind.Request(subList))
                            .subscribeOn(Schedulers.newThread())
                            .subscribe();
                }
            }

            @Override
            public void onError(Exception e) {
            }
        });
        contactsManager.loadContacts();
    }

    private boolean isPhoneFriend(String phone, List<Friend> friends) {
        for (Friend friend : friends) {
            if (friend.getPhone().equalsIgnoreCase(phone)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Разбиение списка объектов на список списков объектов
     * @param source исходный список объектов
     * @param offset количество элементов в подсписке
     * @return список списков
     */
    private List<List<String>> toSubLists(List<String> source, int offset) {
        List<List<String>> result = new ArrayList<>();
        int index = 0;
        List<String> subList = new ArrayList<>();
        for (String item : source) {
            subList.add(item);
            if (index >= offset - 1) {
                index = 0;
                result.add(subList);
                subList = new ArrayList<>();
            }
            ++index;
        }
        return result;
    }

    public static class Manager {
        private static final String PREFS = "contacts_controller_prefs";
        private static final String UPDATE_TIME = "update_time";
        private static final int INTERVAL = 24 * 60 * 1000;

        public static boolean isNeedUpdate(Context context) {
            SharedPreferences prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
            return prefs.getLong(UPDATE_TIME, System.currentTimeMillis()) <= System.currentTimeMillis();
        }

        public static void increment(Context context) {
            SharedPreferences prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
            prefs.edit().putLong(UPDATE_TIME, System.currentTimeMillis() + INTERVAL);
        }
    }
}
