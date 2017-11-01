package ru.mos.polls.newprofile.vm;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import com.android.volley2.VolleyError;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import ru.mos.elk.BaseActivity;
import ru.mos.elk.profile.AgUser;
import ru.mos.elk.profile.ProfileManager;
import ru.mos.elk.profile.flat.Flat;
import ru.mos.polls.AGApplication;
import ru.mos.polls.R;
import ru.mos.polls.base.component.ProgressableUIComponent;
import ru.mos.polls.base.component.PullableUIComponent;
import ru.mos.polls.base.component.RecyclerUIComponent;
import ru.mos.polls.base.component.UIComponentFragmentViewModel;
import ru.mos.polls.base.component.UIComponentHolder;
import ru.mos.polls.base.ui.rvdecoration.UIhelper;
import ru.mos.polls.databinding.FragmentAddressesPropertyBinding;
import ru.mos.polls.newprofile.service.ProfileSet;
import ru.mos.polls.newprofile.service.model.FlatsEntity;
import ru.mos.polls.newprofile.state.NewFlatState;
import ru.mos.polls.newprofile.ui.adapter.AddressesAdapter;
import ru.mos.polls.newprofile.ui.fragment.AddressesPropertyFragment;
import ru.mos.polls.rxhttp.rxapi.handle.response.HandlerApiResponseSubscriber;

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
        flatsList.add(new FlatsEntity.BaseFlat(id, true));
        doDeleteRequest(id);
    }

    public void refreshProfile() {
        getComponent(ProgressableUIComponent.class).begin();
        ProfileManager.AgUserListener agUserListener = new ProfileManager.AgUserListener() {
            @Override
            public void onLoaded(AgUser loadedAgUser) {
                try {
                    getComponent(ProgressableUIComponent.class).end();
                    getComponent(PullableUIComponent.class).end();
                    progressable.end();
                    clearAndSetAdapterItem();
                } catch (Exception ignored) {
                }
            }

            @Override
            public void onError(VolleyError error) {
                getComponent(ProgressableUIComponent.class).end();
                getComponent(PullableUIComponent.class).end();
                try {
                    Toast.makeText(getActivity(), error.getMessage(), Toast.LENGTH_SHORT).show();
                } catch (Exception ignored) {
                }
            }
        };
        ProfileManager.getProfile((BaseActivity) getActivity(), agUserListener);
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