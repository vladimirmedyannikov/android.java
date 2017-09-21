package ru.mos.polls.newprofile.vm;

import android.content.Context;
import android.os.Bundle;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import ru.mos.elk.profile.Achievements;
import ru.mos.polls.AGApplication;
import ru.mos.polls.R;
import ru.mos.polls.base.vm.PullableFragmentVM;
import ru.mos.polls.databinding.FragmentAchievementTabProfileBinding;
import ru.mos.polls.newprofile.service.AchievementsSelect;
import ru.mos.polls.newprofile.ui.adapter.AchievementAdapter;
import ru.mos.polls.newprofile.ui.fragment.AchievementTabFragment;
import ru.mos.polls.profile.gui.activity.AchievementActivity;
import ru.mos.polls.rxhttp.rxapi.handle.response.HandlerApiResponseSubscriber;
import ru.mos.polls.util.StubUtils;

/**
 * Created by Trunks on 16.06.2017.
 */

public class AchievementTabFragmentVM extends PullableFragmentVM<AchievementTabFragment, FragmentAchievementTabProfileBinding, AchievementAdapter> implements OnAchievementClickListener {
    List<Achievements> list;
    int friendId;

    public AchievementTabFragmentVM(AchievementTabFragment fragment, FragmentAchievementTabProfileBinding binding) {
        super(fragment, binding);
    }

    protected void initialize(FragmentAchievementTabProfileBinding binding) {
        recyclerView = binding.list;
        list = new ArrayList<>();
        adapter = new AchievementAdapter(list, this);
        super.initialize(binding);
        Bundle extras = getFragment().getArguments();
        if (extras != null) {
            friendId = extras.getInt(AchievementTabFragment.ARG_FRIEND_ID);
        }
    }

    @Override
    public void onViewCreated() {
        super.onViewCreated();
    }

    @Override
    public void onAchievementClick(String id) {
        AchievementActivity.startActivity(getActivity(), id);
    }

    public void doRequest() {
        HandlerApiResponseSubscriber<AchievementsSelect.Response.Result> handler =
                new HandlerApiResponseSubscriber<AchievementsSelect.Response.Result>(getActivity(), progressable) {
                    @Override
                    protected void onResult(AchievementsSelect.Response.Result result) {
                        adapter.add(result.getAchievements());
                        progressPull();
                    }
                };
        AchievementsSelect.Request requestBody = new AchievementsSelect.Request(page);
        if (friendId != 0) {
            requestBody.setId(friendId);
        }
        Observable<AchievementsSelect.Response> responseObservable = AGApplication.api
                .selectAchievements(requestBody)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
        disposables.add(responseObservable.subscribeWith(handler));
    }


    public static List<Achievements> mockList(Context context) {
        Gson gson = new Gson();
        List<Achievements> content = gson.fromJson(
                StubUtils.fromRawAsJsonArray(context, R.raw.achievement).toString(),
                new TypeToken<List<Achievements>>() {
                }.getType()
        );
        return content;
    }
}
