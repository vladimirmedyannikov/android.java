package ru.mos.polls.newprofile.vm;

import android.content.Context;
import android.support.v7.widget.RecyclerView;

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
import ru.mos.polls.base.component.ProgressableUIComponent;
import ru.mos.polls.base.component.PullableUIComponent;
import ru.mos.polls.base.component.UIComponentFragmentViewModel;
import ru.mos.polls.base.component.UIComponentHolder;
import ru.mos.polls.databinding.FragmentAchievementTabProfileBinding;
import ru.mos.polls.base.ui.RecyclerScrollableController;
import ru.mos.polls.base.ui.rvdecoration.UIhelper;
import ru.mos.polls.newprofile.service.AchievementsSelect;
import ru.mos.polls.newprofile.ui.adapter.AchievementAdapter;
import ru.mos.polls.newprofile.ui.fragment.AchievementTabFragment;
import ru.mos.polls.profile.gui.activity.AchievementActivity;
import ru.mos.polls.rxhttp.rxapi.handle.response.HandlerApiResponseSubscriber;
import ru.mos.polls.rxhttp.rxapi.model.Page;
import ru.mos.polls.util.StubUtils;

/**
 * Created by Trunks on 16.06.2017.
 */

public class AchievementTabFragmentVM extends UIComponentFragmentViewModel<AchievementTabFragment, FragmentAchievementTabProfileBinding> implements OnAchievementClickListener {
    private Page achivementPage;
    AchievementAdapter adapter;
    List<Achievements> list;
    boolean isPaginationEnable;
    RecyclerView recyclerView;

    public AchievementTabFragmentVM(AchievementTabFragment fragment, FragmentAchievementTabProfileBinding binding) {
        super(fragment, binding);
    }

    @Override
    protected void initialize(FragmentAchievementTabProfileBinding binding) {
        recyclerView = binding.agUserProfileList;
        UIhelper.setRecyclerList(recyclerView, getActivity());
        list = new ArrayList<>();
        achivementPage = new Page();
        isPaginationEnable = true;
    }

    @Override
    public void onResume() {
        super.onResume();
        loadAchivements();
    }

    @Override
    public void onViewCreated() {
        super.onViewCreated();
        progressable = getProgressable();
        adapter = new AchievementAdapter(list, this);
        recyclerView.setAdapter(adapter);
        recyclerView.addOnScrollListener(getScrollableListener());
    }

    @Override
    protected UIComponentHolder createComponentHolder() {
        return new UIComponentHolder.Builder()
                .with(new PullableUIComponent(() -> {
                    progressable = getPullableProgressable();
                    adapter.clear();
                    adapter.notifyDataSetChanged();
                    loadAchivements();
                }))
                .with(new ProgressableUIComponent())
                .build();
    }

    @Override
    public void onAchivementClick(String id) {
        AchievementActivity.startActivity(getActivity(), id);
    }

    public void loadAchivements() {
        HandlerApiResponseSubscriber<AchievementsSelect.Response.Result> handler =
                new HandlerApiResponseSubscriber<AchievementsSelect.Response.Result>(getActivity(), progressable) {
                    @Override
                    protected void onResult(AchievementsSelect.Response.Result result) {
                        adapter.add(result.getAchievements());
                        adapter.add(mockList(getActivity()));
                        adapter.notifyDataSetChanged();
                        progressable.end();
                        isPaginationEnable = true;
                    }
                };
        Observable<AchievementsSelect.Response> responseObservable = AGApplication.api
                .selectAchievements(new AchievementsSelect.Request(achivementPage))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
        disposables.add(responseObservable.subscribeWith(handler));
    }

    protected RecyclerView.OnScrollListener getScrollableListener() {
        RecyclerScrollableController.OnLastItemVisibleListener onLastItemVisibleListener
                = () -> {
            if (isPaginationEnable) {
                isPaginationEnable = false;
                achivementPage.increment();
                loadAchivements();
            }
        };
        return new RecyclerScrollableController(onLastItemVisibleListener);
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
