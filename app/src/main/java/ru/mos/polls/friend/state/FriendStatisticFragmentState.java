package ru.mos.polls.friend.state;

import android.content.Context;
import android.support.annotation.Nullable;

import me.ilich.juggler.gui.JugglerFragment;
import me.ilich.juggler.states.ContentBelowToolbarState;
import me.ilich.juggler.states.State;
import ru.mos.polls.R;
import ru.mos.polls.base.ui.CommonToolbarFragment;
import ru.mos.polls.friend.model.Friend;
import ru.mos.polls.friend.ui.fragment.FriendStatisticFragment;


public class FriendStatisticFragmentState extends ContentBelowToolbarState<FriendStatisticFragmentState.FriendParams> {


    public FriendStatisticFragmentState(Friend friend) {
        super(new FriendStatisticFragmentState.FriendParams(friend));
    }

    @Nullable
    @Override
    public String getTitle(Context context, FriendStatisticFragmentState.FriendParams params) {
        return context.getResources().getString(R.string.profile_title);
    }

    @Override
    protected JugglerFragment onConvertContent(FriendStatisticFragmentState.FriendParams params, @Nullable JugglerFragment fragment) {
        return FriendStatisticFragment.newInstance(params.friend);
    }

    @Override
    protected JugglerFragment onConvertToolbar(FriendStatisticFragmentState.FriendParams params, @Nullable JugglerFragment fragment) {
        return CommonToolbarFragment.createBack();
    }

    static class FriendParams extends State.Params {
        Friend friend;
        public FriendParams(Friend friend) {
            this.friend = friend;
        }

    }
}