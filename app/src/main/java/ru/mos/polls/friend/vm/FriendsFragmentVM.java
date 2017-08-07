package ru.mos.polls.friend.vm;

import android.Manifest;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;

import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;
import ru.mos.polls.AGApplication;
import ru.mos.polls.R;
import ru.mos.polls.databinding.LayoutFriendsBinding;
import ru.mos.polls.friend.ContactsController;
import ru.mos.polls.friend.ui.FriendGuiUtils;
import ru.mos.polls.friend.ui.FriendsAdapter;
import ru.mos.polls.friend.ui.FriendsFragment;
import ru.mos.polls.newprofile.base.vm.FragmentViewModel;
import ru.mos.polls.rxhttp.rxapi.handle.response.HandlerApiResponseSubscriber;
import ru.mos.polls.rxhttp.rxapi.model.friends.service.FriendFind;
import ru.mos.polls.rxhttp.rxapi.model.friends.service.FriendMy;
import ru.mos.polls.util.GuiUtils;

/**
 * Created by Sergey Elizarov (sergey.elizarov@altarix.ru)
 * on 06.07.17 13:18.
 */

public class FriendsFragmentVM extends FragmentViewModel<FriendsFragment, LayoutFriendsBinding> {
    public static final int CONTACTS_PERMISSION_REQUEST_CODE = 987;
    public static final String[] CONTACTS_PERMS = {
            Manifest.permission.READ_CONTACTS
    };

    private FriendsAdapter adapter;
    private ContactsController contactsController;

    public FriendsFragmentVM(FriendsFragment fragment, LayoutFriendsBinding binding) {
        super(fragment, binding);
    }

    @Override
    protected void initialize(LayoutFriendsBinding binding) {
        getFragment().getActivity().setTitle(R.string.mainmenu_friends_title);
        binding.list.setLayoutManager(new LinearLayoutManager(getActivity()));
        adapter = new FriendsAdapter();
        adapter.add(new FriendAddItemVW(this::chooseContact));
        binding.list.setAdapter(adapter);
        loadMyFriends();
        initContactsController();
    }

    @AfterPermissionGranted(CONTACTS_PERMISSION_REQUEST_CODE)
    private void chooseContact() {
        if (EasyPermissions.hasPermissions(getFragment().getContext(), CONTACTS_PERMS)) {
            contactsController.chooseContact();
        } else {
            EasyPermissions.requestPermissions(getFragment(),
                    getFragment().getResources().getString(R.string.permission_contacts),
                    CONTACTS_PERMISSION_REQUEST_CODE,
                    CONTACTS_PERMS);
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        contactsController.onActivityResult(requestCode, resultCode, data);
    }

    private void initContactsController() {
        contactsController = new ContactsController(getFragment());
        contactsController.setCallback(new ContactsController.Callback() {
            @Override
            public void onChooseContacts(String number) {
                FriendGuiUtils.formatPhone(number);
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
                = new HandlerApiResponseSubscriber<FriendMy.Response.Result>(getFragment().getContext()) {

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
                = new HandlerApiResponseSubscriber<FriendFind.Response.Result>(getActivity()) {
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
                    contactsController.sms(phone, messages);
                })
                .setNegativeButton(R.string.ag_no, null)
                .show();
    }

 }
