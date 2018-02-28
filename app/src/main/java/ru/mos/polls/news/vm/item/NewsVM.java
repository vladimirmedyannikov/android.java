package ru.mos.polls.news.vm.item;

import android.view.View;

import ru.mos.polls.AGApplication;
import ru.mos.polls.R;
import ru.mos.polls.base.RecyclerBaseViewModel;
import ru.mos.polls.base.rxjava.Events;
import ru.mos.polls.databinding.ItemNewsBinding;
import ru.mos.polls.news.model.News;


public class NewsVM extends RecyclerBaseViewModel<News, ItemNewsBinding> {

    public NewsVM(News model) {
        super(model);
    }

    @Override
    public int getLayoutId() {
        return R.layout.item_news;
    }

    @Override
    public int getViewType() {
        return 0;
    }

    @Override
    public void onBind(ItemNewsBinding viewDataBinding) {
        super.onBind(viewDataBinding);
        viewDataBinding.setViewModel(this);
        viewDataBinding.executePendingBindings();
        AGApplication.getImageLoader().displayImage(model.getImg(), viewDataBinding.image);
        viewDataBinding.rootItem.setOnClickListener(view -> {
            AGApplication.bus().send(new Events.NewsEvents(model));
        });
    }

    public String getTitle() {
        return model.getTitle();
    }

    public String getValueFirst() {
        return model.getColumns() != null ? model.getColumns().size() > 0 ? model.getColumns().get(0) : "" : "";
    }

    public String getValueSecond() {
        return model.getColumns() != null ? model.getColumns().size() > 1 ? model.getColumns().get(1) : "" : "";
    }

    public int valueFirstIsVisible() {
        return model.getColumns() != null ? model.getColumns().size() > 0 ? View.VISIBLE : View.GONE : View.GONE;
    }

    public int valueSecondIsVisible() {
        return model.getColumns() != null ? model.getColumns().size() > 1 ? View.VISIBLE : View.GONE : View.GONE;
    }
}
