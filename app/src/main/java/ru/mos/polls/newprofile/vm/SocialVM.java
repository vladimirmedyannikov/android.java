package ru.mos.polls.newprofile.vm;

import android.databinding.BaseObservable;
import android.support.v7.widget.AppCompatImageView;
import android.view.View;

import ru.mos.polls.databinding.ItemBindSocialNewBinding;
import ru.mos.polls.social.model.Social;
import ru.mos.polls.social.model.SocialBindItem;

/**
 * Created by Trunks on 23.06.2017.
 */

public class SocialVM extends BaseObservable {
    private Social social;
    private ItemBindSocialNewBinding binding;
    AppCompatImageView unbindIcon;

    public SocialVM(Social social, ItemBindSocialNewBinding binding) {
        this.social = social;
        this.binding = binding;
        unbindIcon = binding.socialUnbind;
        setUnbindIconVisibilyty();
    }

    public int getIcon() {
        int drawableId = SocialBindItem.getBindResId(social.getSocialId());
        if (!social.isLogon()) {
            drawableId = SocialBindItem.getUnBindResId(social.getSocialId());
        }
        return drawableId;
    }

    public String getSocialName() {
        return social.getSocialName();
    }

    public void unBindSocial(View view) {
    }

    public void setUnbindIconVisibilyty() {
        unbindIcon.setVisibility(social.isLogon() ? View.VISIBLE : View.INVISIBLE);
    }
}
