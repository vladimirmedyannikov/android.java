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
import ru.mos.elk.BaseActivity;
import ru.mos.polls.AGApplication;
import ru.mos.polls.R;
import ru.mos.polls.badge.manager.BadgeManager;
import ru.mos.polls.base.component.ProgressableUIComponent;
import ru.mos.polls.base.component.PullableUIComponent;
import ru.mos.polls.base.component.UIComponentFragmentViewModel;
import ru.mos.polls.base.component.UIComponentHolder;
import ru.mos.polls.databinding.LayoutFriendsBinding;
import ru.mos.polls.friend.ContactsController;
import ru.mos.polls.friend.ContactsManager;
import ru.mos.polls.friend.ui.FriendGuiUtils;
import ru.mos.polls.friend.ui.FriendsAdapter;
import ru.mos.polls.friend.ui.FriendsFragment;
import ru.mos.polls.rxhttp.rxapi.handle.response.HandlerApiResponseSubscriber;
import ru.mos.polls.rxhttp.rxapi.model.friends.service.FriendFind;
import ru.mos.polls.rxhttp.rxapi.model.friends.service.FriendMy;
import ru.mos.polls.rxhttp.rxapi.progreessable.Progressable;
import ru.mos.polls.util.GuiUtils;

/**
 * Created by Sergey Elizarov (sergey.elizarov@altarix.ru)
 * on 06.07.17 13:18.
 */

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
                    progressable = getPullableProgressable();
                    adapter.clear();
                    adapter.notifyDataSetChanged();
                    loadMyFriends();
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
    }

    @Override
    public void onViewCreated() {
        super.onViewCreated();
        progressable = getProgressable();
        loadMyFriends();
        initContactsController();
        chooseContact();

        BadgeManager.uploadAllFriendsAsReaded((BaseActivity) getActivity());
        LocalBroadcastManager
                .getInstance(getActivity())
                .sendBroadcast(new Intent(BadgeManager.ACTION_RELOAD_BAGES_FROM_SERVER));
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
                    String[] messages = new String[] {
                            "Я - Активный гражданин - А ТЫ? Давай вместе сделаем город лучше!",
                            "http://ag.mos.ru"
                    };
                    contactsManager.sms(phone, messages);
                })
                .setNegativeButton(R.string.ag_no, null)
                .show();
    }

 }
