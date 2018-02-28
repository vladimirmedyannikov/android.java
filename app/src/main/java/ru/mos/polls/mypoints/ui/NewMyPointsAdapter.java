package ru.mos.polls.mypoints.ui;

import java.util.ArrayList;
import java.util.List;

import ru.mos.polls.base.BaseRecyclerAdapter;
import ru.mos.polls.base.RecyclerBaseViewModel;
import ru.mos.polls.mypoints.model.Points;
import ru.mos.polls.mypoints.vm.PointsVM;

public class NewMyPointsAdapter extends BaseRecyclerAdapter<RecyclerBaseViewModel> {


    public void add(List<Points> points) {
        List<RecyclerBaseViewModel> content = new ArrayList<>();
        for (Points point : points) {
            content.add(new PointsVM(point));
        }
        addData(content);
    }
}
