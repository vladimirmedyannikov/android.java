package ru.mos.polls.profile.vm;


import android.app.Activity;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;


import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import ru.mos.polls.profile.ProfileManagerRX;
import ru.mos.polls.profile.model.AgUser;
import ru.mos.polls.profile.model.flat.Flat;
import ru.mos.polls.AGApplication;
import ru.mos.polls.R;
import ru.mos.polls.base.component.ProgressableUIComponent;
import ru.mos.polls.base.component.PullableUIComponent;
import ru.mos.polls.base.component.RecyclerUIComponent;
import ru.mos.polls.base.component.UIComponentFragmentViewModel;
import ru.mos.polls.base.component.UIComponentHolder;
import ru.mos.polls.base.ui.rvdecoration.UIhelper;
import ru.mos.polls.databinding.FragmentAddressesPropertyBinding;
import ru.mos.polls.profile.service.ProfileSet;
import ru.mos.polls.profile.service.model.FlatsEntity;
import ru.mos.polls.profile.state.NewFlatState;
import ru.mos.polls.profile.ui.adapter.AddressesAdapter;
import ru.mos.polls.profile.ui.fragment.AddressesPropertyFragment;
import ru.mos.polls.rxhttp.rxapi.handle.response.HandlerApiResponseSubscriber;
import ru.mos.polls.util.GuiUtils;

/**
 * Created by Trunks on 25.10.2017.
 */

public class AddressesPropertyFragmentVM extends UIComponentFragmentViewModel<AddressesPropertyFragment, FragmentAddressesPropertyBinding> implements onAddressesDeleteIconClickListener {
    AddressesAdapter adapter;
    RecyclerView recyclerView;
    List<FlatsEntity.BaseFlat> flatsList;

    public AddressesPropertyFragmentVM(AddressesPropertyFragment fragment, FragmentAddressesPropertyBinding binding) {
        super(fragment, binding);
    }

    @Override
    protected void initialize(FragmentAddressesPropertyBinding binding) {
        recyclerView = binding.list;
        binding.setViewModel(this);
        binding.executePendingBindings();
        UIhelper.setRecyclerList(recyclerView, getActivity());
        adapter = new AddressesAdapter(this);
        recyclerView.setAdapter(adapter);
        flatsList = new ArrayList<>();

    }

    @Override
    public void onViewCreated() {
        super.onViewCreated();
        clearAndSetAdapterItem();
    }


    public void clearAndSetAdapterItem() {
        adapter.clear();
        adapter.add(AgUser.getOwnPropertyList(getActivity()));
    }

    @Override
    protected UIComponentHolder createComponentHolder() {
        return new UIComponentHolder.Builder()
                .with(new PullableUIComponent(() -> {
                    progressable = getPullableProgressable();
                    refreshProfile();
                }))
                .with(new ProgressableUIComponent())
                .with(new RecyclerUIComponent<>(adapter))
                .build();
    }

    public void doDeleteRequest(String id) {
        FlatsEntity entity = new FlatsEntity(flatsList);
        ProfileSet.Request request = new ProfileSet.Request(entity);
        HandlerApiResponseSubscriber<ProfileSet.Response.Result> handler
                = new HandlerApiResponseSubscriber<ProfileSet.Response.Result>(getActivity(), getComponent(ProgressableUIComponent.class)) {
            @Override
            protected void onResult(ProfileSet.Response.Result result) {
                adapter.removeItem(id);
                flatsList.clear();
                refreshProfile();
            }
        };
        Observable<ProfileSet.Response> responseObservabl =
                AGApplication.api.setProfile(request)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread());
        disposables.add(responseObservabl.subscribeWith(handler));
    }

    @Override
    public void onIconDeleteClick(String id) {
        for (AddressesPropertyVM iterator : adapter.getList()) {
            if (iterator.getFlatId().equals(id)) {
                GuiUtils.displayYesOrNotDialog(getActivity(),
                        String.format(getFragment().getString(R.string.remove_addresses_dialog_message), iterator.getPropertyAddress()),
                        (dialog, which) -> {
                            flatsList.add(new FlatsEntity.BaseFlat(id, true));
                            doDeleteRequest(id);
                        },
                        null);
                break;
            }
        }
    }

    public void refreshProfile() {
        getComponent(ProgressableUIComponent.class).begin();
        ProfileManagerRX.AgUserListener listener = new ProfileManagerRX.AgUserListener() {
            @Override
            public void onLoaded(AgUser agUser) {
                try {
                    getComponent(ProgressableUIComponent.class).end();
                    getComponent(PullableUIComponent.class).end();
                    progressable.end();
                    clearAndSetAdapterItem();
                } catch (Exception ignored) {
                }
            }

            @Override
            public void onError(String message, int code) {
                getComponent(ProgressableUIComponent.class).end();
                getComponent(PullableUIComponent.class).end();
                try {
                    Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
                } catch (Exception ignored) {
                }
            }
        };
        ProfileManagerRX.getProfile(disposables, getActivity(), listener);
    }

    public void addProperty() {
        Flat flat = new Flat();
        flat.setType(Flat.Type.OWN);
        getFragment().navigateToActivityForResult(new NewFlatState(flat, NewFlatFragmentVM.FLAT_TYPE_OWN), NewFlatFragmentVM.FLAT_TYPE_OWN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == NewFlatFragmentVM.FLAT_TYPE_OWN) {
            if (resultCode == Activity.RESULT_OK) {
                refreshProfile();
            }
        }
    }
}
