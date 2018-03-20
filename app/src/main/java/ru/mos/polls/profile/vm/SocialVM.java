package ru.mos.polls.profile.vm;

import android.support.v7.widget.AppCompatImageView;
import android.view.View;

import ru.mos.polls.R;
import ru.mos.polls.base.RecyclerBaseViewModel;
import ru.mos.polls.databinding.ItemBindSocialNewBinding;
import ru.mos.polls.profile.ui.adapter.SocialBindAdapter;
import ru.mos.polls.social.model.AppBindItem;
import ru.mos.polls.social.model.AppSocial;

public class SocialVM extends RecyclerBaseViewModel<AppSocial, ItemBindSocialNewBinding> {
    private AppCompatImageView unbindIcon;
    private SocialBindAdapter.Listener listener;

    public SocialVM(AppSocial social, SocialBindAdapter.Listener listener) {
        super(social);
        this.listener = listener;
    }

    @Override
    public void onBind(ItemBindSocialNewBinding viewDataBinding) {
        super.onBind(viewDataBinding);
        viewDataBinding.setViewModel(this);
        viewDataBinding.executePendingBindings();
        viewDataBinding.root.setOnClickListener(v -> listener.onBindClick(getModel()));
        unbindIcon = viewDataBinding.socialUnbind;
        unbindIcon.setOnClickListener(v -> listener.onCloseClick(getModel()));
        setUnbindIconVisibility();
    }

    @Override
    public int getViewType() {
        return 0;
    }

    @Override
    public int getLayoutId() {
        return R.layout.item_bind_social_new;
    }

    public int getIcon() {
        int drawableId = AppBindItem.getBindResId(model.getId());
        if (!model.isLogon()) {
            drawableId = AppBindItem.getUnBindResId(model.getId());
        }
        return drawableId;
    }

    public String getSocialName() {
        return getViewDataBinding().root.getContext().getString(AppBindItem.getTitle(model.getId()));
    }

    public void unBindSocial(View view) {
    }

    public void setUnbindIconVisibility() {
        unbindIcon.setVisibility(model.isLogon() ? View.VISIBLE : View.INVISIBLE);
    }
}
