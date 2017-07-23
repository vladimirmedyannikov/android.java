package ru.mos.polls.newprofile.vm;

import android.support.v7.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import ru.mos.polls.AGApplication;
import ru.mos.polls.R;
import ru.mos.polls.databinding.LayoutAchievementTabProfileBinding;
import ru.mos.polls.newprofile.base.ui.RecyclerScrollableController;
import ru.mos.polls.newprofile.model.Achievement;
import ru.mos.polls.newprofile.service.AchievementsSelect;
import ru.mos.polls.newprofile.ui.adapter.AchievementAdapter;
import ru.mos.polls.newprofile.ui.fragment.AchievementTabFragment;
import ru.mos.polls.profile.gui.activity.AchievementActivity;
import ru.mos.polls.rxhttp.rxapi.model.Page;
import ru.mos.polls.util.StubUtils;

/**
 * Created by Trunks on 16.06.2017.
 */

public class AchievementTabFragmentVM extends BaseTabFragmentVM<AchievementTabFragment, LayoutAchievementTabProfileBinding> implements OnAchievementClickListener {
    private Page achivementPage;
    AchievementAdapter adapter;
    List<Achievement> list;
    boolean isPaginationEnable;

    public AchievementTabFragmentVM(AchievementTabFragment fragment, LayoutAchievementTabProfileBinding binding) {
        super(fragment, binding);
    }

    @Override
    protected void initialize(LayoutAchievementTabProfileBinding binding) {
        recyclerView = binding.agUserProfileList;
        super.initialize(binding);
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
        adapter = new AchievementAdapter(list, this);
        recyclerView.setAdapter(adapter);
        recyclerView.addOnScrollListener(getScrollableListener());
    }

    @Override
    public void onAchivementClick(String id) {
        AchievementActivity.startActivity(getActivity(), id);
    }

    public void loadAchivements() {
        Observable<AchievementsSelect.Response> achievementRe
                = AGApplication.api.selectAchievements(new AchievementsSelect.Request(achivementPage));
        achievementRe
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(response -> {
                    list.addAll(response.getResult().getAchievements());
                    list.addAll(mockList());
                    adapter.notifyDataSetChanged();
                    isPaginationEnable = true;
                }, throwable -> throwable.printStackTrace());
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

    public List<Achievement> mockList() {
        Gson gson = new Gson();
        List<Achievement> content = gson.fromJson(
                StubUtils.fromRawAsJsonArray(getActivity(), R.raw.achievement).toString(),
                new TypeToken<List<Achievement>>() {
                }.getType()
        );
        return content;
    }
}
