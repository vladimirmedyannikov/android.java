package ru.mos.polls.mainbanner;


import java.util.ArrayList;
import java.util.List;

import ru.mos.polls.base.BaseRecyclerAdapter;

/**
 * Created by Trunks on 18.01.2018.
 */

public class BannerAdapter extends BaseRecyclerAdapter {

    public void add(List<BannerItem> items) {
        List<BannerItemVM> content = new ArrayList<>();
        for (BannerItem item : items) {
            content.add(new BannerItemVM(item));
        }
        addData(content);
    }
}
