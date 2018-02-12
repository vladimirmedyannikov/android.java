package ru.mos.polls.quests.model.quest;

import android.app.Dialog;
import android.content.Context;

import ru.mos.elk.BaseActivity;
import ru.mos.polls.R;
import ru.mos.polls.quests.model.QuestFamilyElement;
import ru.mos.polls.quests.vm.QuestsFragmentVM;
import ru.mos.polls.social.controller.SocialUIController;
import ru.mos.polls.social.model.AppPostValue;

public class SocialQuest extends DetailsQuest {

    public static final String TYPE_SOCIAL = "social";
    public static final String ID_POST_IN_SOCIAL = "postInSocial";
    public static final String ID_INVITE_FRIENDS = "inviteFriends";
    public static final String ID_RATE_THIS_APP = "rateThisApplication";
    private static final String ID = "id";
    private int icon;

    public SocialQuest(long innerId, QuestFamilyElement questFamilyElement) {
        super(innerId, questFamilyElement);
        icon = getIcon();
    }

    public int getIcon() {
        int iconId;
        if (ID_INVITE_FRIENDS.equals(getId())) {
            iconId = R.drawable.image_icon_category_friends;
        } else if (ID_POST_IN_SOCIAL.equals(getId())) {
            iconId = R.drawable.image_icon_category_social;
        } else if (ID_RATE_THIS_APP.equals(getId())) {
            iconId = R.drawable.icon03;
        } else {
            iconId = R.drawable.image_icon_category_profile;
        }
        icon = iconId;
        return iconId;
    }

    @Override
    public void onClick(final Context context, final QuestsFragmentVM.Listener listener) {
        if (ID_POST_IN_SOCIAL.equals(getId())) {
            SocialUIController.showSocialsDialog((BaseActivity) context, new SocialUIController.SocialClickListener() {
                @Override
                public void onClick(Context context, Dialog dialog, AppPostValue appPostValue) {
                    listener.onSocialPost(appPostValue);
                }

                @Override
                public void onCancel() {
                    ((BaseActivity) context).finish();
                }
            });
        } else if (ID_INVITE_FRIENDS.equals(getId())) {
            listener.onInviteFriends(true);
        }
    }

}
