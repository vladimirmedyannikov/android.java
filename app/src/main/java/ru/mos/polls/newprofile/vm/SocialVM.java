package ru.mos.polls.newprofile.vm;

import android.databinding.BaseObservable;
import android.view.View;
import android.widget.Toast;

import ru.mos.polls.databinding.ItemBindSocialNewBinding;
import ru.mos.polls.social.model.Social;
import ru.mos.polls.social.model.SocialBindItem;

/**
 * Created by Trunks on 23.06.2017.
 */

public class SocialVM extends BaseObservable {
    private Social social;
    private ItemBindSocialNewBinding binding;

    public SocialVM(Social social, ItemBindSocialNewBinding binding) {
        this.social = social;
        this.binding = binding;
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
        Toast.makeText(view.getContext(), "unbind", Toast.LENGTH_SHORT).show();
    }
}
