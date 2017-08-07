package ru.mos.polls.friend;

import android.support.v4.app.Fragment;

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
 * @author Sergey Elizarov (elizarov1988@gmail.com)
 *         on 07.08.17 17:35.
 */

public class ContactsController {
    private ContactsManager contactsManager;

    public ContactsController(Fragment fragment) {
        contactsManager = new ContactsManager(fragment);
    }

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
                    boolean yetHas = false;
                    for (Friend friend : friends) {
                        if (number.equalsIgnoreCase(friend.getPhone())) {
                            yetHas = true;
                            break;
                        }
                    }
                    if (!yetHas) {
                        result.add(number);
                    }
                }
                if (result.size() > 0) {
                    AGApplication
                            .api
                            .friendFind(new FriendFind.Request(result.subList(0, 20)))
                            .subscribeOn(Schedulers.newThread())
                            .subscribe();
                }
            }

            @Override
            public void onError(Exception e) {
            }
        });
        contactsManager.loadContacs();
    }
}
