package ru.mos.polls.profile.gui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley2.VolleyError;
import com.android.volley2.toolbox.ImageLoader;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import ru.mos.elk.BaseActivity;
import ru.mos.polls.R;
import ru.mos.polls.navigation.actionbar.ActionBarNavigationController;
import ru.mos.polls.profile.adapter.AchievementAdapter;
import ru.mos.polls.profile.controller.ProfileApiController;
import ru.mos.polls.profile.gui.activity.AchievementActivity;
import ru.mos.polls.profile.model.Achievement;


public class AchievementsFragment extends Fragment {
    public static Fragment newInstance() {
        return new AchievementsFragment();
    }

    @BindView(R.id.list)
    ListView listView;
    private AchievementAdapter achievementAdapter;
    private List<Achievement> achievements;
    private Unbinder unbinder;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.layout_list, null);
        unbinder = ButterKnife.bind(this, root);
        return root;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        findViews(view);
        loadAchievements();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ActionBarNavigationController
                .destroyNavigation((BaseActivity) getActivity());
    }

    private void init() {
        /**
         * Ранее использовалось для навигации
         * мкжду фрагментами через выпадающий
         * список вэкшен баре
         * пока оставим
         */
        achievements = new ArrayList<Achievement>();
        ImageLoader imageLoader = ((BaseActivity) getActivity()).createImageLoader();
        achievementAdapter = new AchievementAdapter(getActivity(), achievements, imageLoader);

    }

    private void findViews(View view) {
        TextView empty = ButterKnife.findById(view, R.id.empty);
        empty.setText(R.string.empty_list_of_achievement);
        listView.setEmptyView(empty);
        listView.setAdapter(achievementAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Achievement achievement = achievements.get(position);
                AchievementActivity.startActivity(getActivity(), achievement);
            }
        });
    }

    private void loadAchievements() {
        ProfileApiController.AchievementsListener listener = new ProfileApiController.AchievementsListener() {
            @Override
            public void onLoaded(List<Achievement> achievements) {
                refreshUI(achievements);
            }

            @Override
            public void onError(VolleyError volleyError) {
                if (volleyError != null) {
                    Toast.makeText(getActivity(), volleyError.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        };
        ProfileApiController.loadAchievements((BaseActivity) getActivity(), listener);
    }

    private void refreshUI(List<Achievement> achievements) {
        this.achievements.clear();
        this.achievements.addAll(achievements);
        achievementAdapter.notifyDataSetChanged();
    }
}
