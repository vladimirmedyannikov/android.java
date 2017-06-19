package ru.mos.polls.newprofile.vm;

import android.databinding.ViewDataBinding;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import me.ilich.juggler.gui.JugglerFragment;
import ru.mos.elk.profile.AgUser;
import ru.mos.polls.R;
import ru.mos.polls.newprofile.base.vm.FragmentViewModel;

/**
 * Created by Trunks on 19.06.2017.
 */

public abstract class BaseTabFragmentVM<F extends JugglerFragment, B extends ViewDataBinding> extends FragmentViewModel<F, B> {
    protected RecyclerView recyclerView;
    protected AgUser changed, saved;

    public BaseTabFragmentVM(F fragment, B binding) {
        super(fragment, binding);
    }


    protected void initialize(B binding) {
        saved = new AgUser(getActivity());
        setRecyclerList();
    }

    protected void setRecyclerList() {
        recyclerView.setLayoutManager(new LinearLayoutManager(getFragment().getContext()));
//        recyclerView.setHasFixedSize(true);
        recyclerView.setNestedScrollingEnabled(false);
        Drawable dividerDrawable = ContextCompat.getDrawable(getFragment().getContext(), R.drawable.divider);
        DividerItemDecoration did = new DividerItemDecoration(getFragment().getContext(), DividerItemDecoration.VERTICAL);
        did.setDrawable(dividerDrawable);
        recyclerView.addItemDecoration(did);
    }
}
