package ru.mos.polls.friend.vm;

import android.Manifest;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;

import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;
import ru.mos.polls.AGApplication;
import ru.mos.polls.R;
import ru.mos.polls.badge.manager.BadgeManager;
import ru.mos.polls.base.activity.BaseActivity;
import ru.mos.polls.base.component.ProgressableUIComponent;
import ru.mos.polls.base.component.PullableUIComponent;
import ru.mos.polls.base.component.UIComponentFragmentViewModel;
import ru.mos.polls.base.component.UIComponentHolder;
import ru.mos.polls.base.rxjava.Events;
import ru.mos.polls.base.rxjava.RxEventDisposableSubscriber;
import ru.mos.polls.databinding.LayoutFriendsBinding;
import ru.mos.polls.friend.ContactsController;
import ru.mos.polls.friend.ContactsManager;
import ru.mos.polls.friend.service.FriendFind;
import ru.mos.polls.friend.service.FriendMy;
import ru.mos.polls.friend.state.FriendProfileState;
import ru.mos.polls.friend.ui.adapter.FriendsAdapter;
import ru.mos.polls.friend.ui.fragment.FriendsFragment;
import ru.mos.polls.friend.ui.utils.FriendGuiUtils;
import ru.mos.polls.friend.vm.list.FriendAddItemVW;
import ru.mos.polls.rxhttp.rxapi.handle.response.HandlerApiResponseSubscriber;
import ru.mos.polls.rxhttp.rxapi.progreessable.Progressable;
import ru.mos.polls.util.GuiUtils;
import ru.mos.polls.util.NetworkUtils;


public class FriendsFragmentVM extends UIComponentFragmentViewModel<FriendsFragment, LayoutFriendsBinding> {
    public static final int CONTACTS_PERMISSION_REQUEST_CODE = 987;
    public static final String[] CONTACTS_PERMS = {
            Manifest.permission.READ_CONTACTS
    };

    private FriendsAdapter adapter;
    private ContactsManager contactsManager;
    private Progressable progressable;

    public FriendsFragmentVM(FriendsFragment fragment, LayoutFriendsBinding binding) {
        super(fragment, binding);
    }

    @Override
    protected UIComponentHolder createComponentHolder() {
        return new UIComponentHolder.Builder()
                .with(new PullableUIComponent(() -> {
                    if (NetworkUtils.hasInternetConnection(getActivity())) {
                        progressable = getPullableProgressable();
                        adapter.clear();
                        adapter.notifyDataSetChanged();
                        loadMyFriends();
                    } else {
                        GuiUtils.displayOkMessage(getActivity(), R.string.default_no_internet_err, null);
                        getPullableProgressable().end();
                    }
                }))
                .with(new ProgressableUIComponent())
                .build();
    }

    @Override
    protected void initialize(LayoutFriendsBinding binding) {
        getFragment().getActivity().setTitle(R.string.mainmenu_friends_title);
        binding.list.setLayoutManager(new LinearLayoutManager(getActivity()));
        adapter = new FriendsAdapter();
        adapter.add(new FriendAddItemVW(() -> contactsManager.chooseContact(getFragment())));
        binding.list.setAdapter(adapter);
        subscribeEventsBus();
    }

    private void subscribeEventsBus() { //переснести
        disposables.add(AGApplication.bus().toObserverable()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new RxEventDisposableSubscriber() {
                    @Override
                    public void onNext(Object o) {
                        if (o instanceof Events.FriendEvents) {
                            Events.FriendEvents action = (Events.FriendEvents) o;
                            if (action.getId() == Events.FriendEvents.FRIEND_START_PROFILE) {
                                getFragment().navigateToActivityForResult(new FriendProfileState(action.getFriend()),0);
                            }
                        }
                    }
                }));
    }

    @Override
    public void onViewCreated() {
        super.onViewCreated();
        progressable = getProgressable();
        if (NetworkUtils.hasInternetConnection(getActivity())) {
            loadMyFriends();
            initContactsController();
            chooseContact();
            BadgeManager.uploadAllFriendsAsReaded((BaseActivity) getActivity());
            LocalBroadcastManager
                    .getInstance(getActivity())
                    .sendBroadcast(new Intent(BadgeManager.ACTION_RELOAD_BAGES_FROM_SERVER));
        } else {
            GuiUtils.displayOkMessage(getActivity(), R.string.default_no_internet_err, null);
        }

    }

    @AfterPermissionGranted(CONTACTS_PERMISSION_REQUEST_CODE)
    private void chooseContact() {
        if (ContactsController.Manager.isNeedUpdate(getActivity())) {
            if (EasyPermissions.hasPermissions(getFragment().getContext(), CONTACTS_PERMS)) {
                ContactsController contactsController = new ContactsController(getActivity());
                contactsController.silentFindFriends();
                ContactsController.Manager.increment(getActivity());
            } else {
                EasyPermissions.requestPermissions(getFragment(),
                        getFragment().getResources().getString(R.string.permission_contacts),
                        CONTACTS_PERMISSION_REQUEST_CODE,
                        CONTACTS_PERMS);
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        contactsManager.onActivityResult(requestCode, resultCode, data);
    }

    private void initContactsController() {
        contactsManager = new ContactsManager(getActivity());
        contactsManager.setCallback(new ContactsManager.Callback() {
            @Override
            public void onChooseContacts(String number) {
                number = FriendGuiUtils.formatPhone(number);
                if (!adapter.has(number)) {
                    findFriend(number);
                } else {
                    GuiUtils.displayOkMessage(getActivity(),
                            "Пользователь уже добавлен в друзья",
                            null);
                }
            }

            @Override
            public void onGetAllContacts(List<String> numbers) {
            }

            @Override
            public void onError(Exception e) {
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private void loadMyFriends() {
        HandlerApiResponseSubscriber<FriendMy.Response.Result> handler
                = new HandlerApiResponseSubscriber<FriendMy.Response.Result>(getFragment().getContext(), progressable) {

            @Override
            protected void onResult(FriendMy.Response.Result result) {
                adapter.add(result.getFriends());
                adapter.notifyDataSetChanged();
            }
        };

        AGApplication
                .api
                .friendMy(new FriendMy.Request())
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(handler);
    }

    private void findFriend(final String phone) {
        HandlerApiResponseSubscriber<FriendFind.Response.Result> handler
                = new HandlerApiResponseSubscriber<FriendFind.Response.Result>(getActivity(), getProgressable()) {
            @Override
            protected void onResult(FriendFind.Response.Result result) {
                if (result.hasInAdded(phone)) {
                    adapter.add(result.find(result.getAdd(), phone));
                    adapter.notifyDataSetChanged();
                } else {
                    onFail(phone);
                }
            }
        };
        AGApplication
                .api
                .friendFind(new FriendFind.Request(phone))
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(handler);
    }

    private void onFail(String phone) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("Ваш друг ещё не является пользователем системы \"Активный гражданин\". Вы хотите пригласить вашего друга?")
                .setPositiveButton(R.string.ag_yes, (dialog, which) -> {
                    String[] messages = new String[]{
                            "Я - Активный гражданин - А ТЫ? Давай вместе сделаем город лучше!",
                            "http://ag.mos.ru"
                    };
                    contactsManager.sms(phone, messages);
                })
                .setNegativeButton(R.string.ag_no, null)
                .show();
    }

}
