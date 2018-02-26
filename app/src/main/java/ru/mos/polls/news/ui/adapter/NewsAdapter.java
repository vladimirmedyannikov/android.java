package ru.mos.polls.news.ui.adapter;

import java.util.ArrayList;
import java.util.List;

import ru.mos.polls.base.BaseRecyclerAdapter;
import ru.mos.polls.base.RecyclerBaseViewModel;
import ru.mos.polls.news.model.News;
import ru.mos.polls.news.vm.item.NewsVM;

/**
 * @author matek3022 (semenovmm@altarix.ru)
 *         on 26.02.18.
 */

public class NewsAdapter extends BaseRecyclerAdapter<RecyclerBaseViewModel> {

    public void add(List<News> list) {
        List<RecyclerBaseViewModel> rbvm = new ArrayList<>();
        for (News news : list) {
            rbvm.add(new NewsVM(news));
        }
        addData(rbvm);
    }
}
