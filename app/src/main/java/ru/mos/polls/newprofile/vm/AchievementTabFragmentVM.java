package ru.mos.polls.newprofile.vm;

import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import ru.mos.polls.AGApplication;
import ru.mos.polls.databinding.LayoutAchievementTabProfileBinding;
import ru.mos.polls.newprofile.model.Achievement;
import ru.mos.polls.newprofile.service.AchievementsSelect;
import ru.mos.polls.newprofile.ui.adapter.AchievementAdapter;
import ru.mos.polls.newprofile.ui.fragment.AchievementTabFragment;
import ru.mos.polls.rxhttp.rxapi.model.Page;

/**
 * Created by Trunks on 16.06.2017.
 */

public class AchievementTabFragmentVM extends BaseTabFragmentVM<AchievementTabFragment, LayoutAchievementTabProfileBinding> implements OnAchievementClickListener {
    private Page achivementPage = new Page();
    public AchievementTabFragmentVM(AchievementTabFragment fragment, LayoutAchievementTabProfileBinding binding) {
        super(fragment, binding);
    }

    @Override
    protected void initialize(LayoutAchievementTabProfileBinding binding) {
        recyclerView = binding.agUserProfileList;
        super.initialize(binding);
        mockAchievementList();
    }

    public void mockAchievementList() {
        List<Achievement> list = new ArrayList<>();

        list.add(new Achievement("1", "http://n1s2.hsmedia.ru/48/7b/36/487b36300c62c5f0cb905da52aa874b4/940x627_1_5a0bfdc1ca88097a61d2d64668c61ef9@940x627_0xc0a839a4_18087198581488362059.jpeg", "кот1", "кот1 тело", "описание", false, false));
        list.add(new Achievement("2", "https://www.ctclove.ru/upload/iblock/ed3/ed3f06615adace6dbff959b6d84b84ce.jpg", "кот2", "кот2 тело", "описание", false, false));
        list.add(new Achievement("3", "https://2ch.hk/b/src/144608408/14845653095130.jpg", "кот3", "кот4 тело", "описание", false, false));
        list.add(new Achievement("4", "http://giraf.media/i/a/140/img5767d9d6ec36a1.38005051.jpg", "кот4", "кот4 тело", "описание", false, false));
        list.add(new Achievement("5", "http://ic.pics.livejournal.com/citron78/73956900/84849/84849_original.jpg", "кот5", "кот5 тело", "описание", false, false));
        list.add(new Achievement("6", "http://byaki.net/uploads/posts/2008-10/1225141006_1-11.jpg", "кот6", "кот6 тело", "описание", false, false));
        list.add(new Achievement("7", "http://www.kulturologia.ru/files/u18476/cote.jpg", "кот7", "кот7 тело", "описание", false, false));
        list.add(new Achievement("8", "https://img3.goodfon.ru/original/1024x1024/7/3e/koshki-milye-kotiki.jpg", "кот8", "кот8 тело", "описание", false, false));
        list.add(new Achievement("9", "http://cdn.fishki.net/upload/post/201604/06/1909969/tn/cdc7cafbfe7efb922855d268fcb51df9.jpg", "кот9", "кот9 тело", "описание", false, false));

        AchievementAdapter adapter = new AchievementAdapter(list, this);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onAchivementClick(String id) {
        Toast.makeText(getActivity().getBaseContext(), "id = " + id, Toast.LENGTH_SHORT).show();
    }
    public void loadAchivements() {
        Observable<AchievementsSelect.Response> achievementRe
                = AGApplication.api.selectAchievements(new AchievementsSelect.Request(achivementPage));
        achievementRe
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(response -> {
                    achievementList = Observable.fromIterable(response.getResult().getAchievements());
                }, throwable -> throwable.printStackTrace());
    }

}
