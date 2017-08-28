package ru.mos.polls.newprofile.vm;

import android.support.v7.widget.AppCompatImageView;
import android.view.View;

import ru.mos.polls.base.vm.BaseVM;
import ru.mos.polls.databinding.ItemBindSocialNewBinding;
import ru.mos.polls.social.model.AppBindItem;
import ru.mos.polls.social.model.AppSocial;

/**
 * Created by Trunks on 23.06.2017.
 */

public class SocialVM extends BaseVM<AppSocial,ItemBindSocialNewBinding> {
    AppCompatImageView unbindIcon;

    public SocialVM(AppSocial social, ItemBindSocialNewBinding binding) {
        super(social,binding);
        unbindIcon = binding.socialUnbind;
        setUnbindIconVisibility();
    }

    public int getIcon() {
        int drawableId = AppBindItem.getBindResId(model.getSocialId());
        if (!model.isLogon()) {
            drawableId = AppBindItem.getUnBindResId(model.getSocialId());
        }
        return drawableId;
    }

    public String getSocialName() {
        return model.getSocialName();
    }

    public void unBindSocial(View view) {
    }

    public void setUnbindIconVisibility() {
        unbindIcon.setVisibility(model.isLogon() ? View.VISIBLE : View.INVISIBLE);
    }
}
