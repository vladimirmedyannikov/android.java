package ru.mos.polls.newquests.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.view.MenuItem;

import com.android.databinding.library.baseAdapters.BR;

import ru.mos.polls.R;
import ru.mos.polls.base.ui.BindingFragment;
import ru.mos.polls.databinding.LayoutQuestsBinding;
import ru.mos.polls.newquests.vm.QuestsFragmentVM;

/**
 * @author matek3022 (semenovmm@altarix.ru)
 *         on 06.12.17.
 */

public class QuestsFragment extends BindingFragment<QuestsFragmentVM, LayoutQuestsBinding> {

    public static QuestsFragment instance() {
        return new QuestsFragment();
    }

    @Override
    protected QuestsFragmentVM onCreateViewModel(LayoutQuestsBinding binding) {
        return new QuestsFragmentVM(this, binding);
    }

    @Override
    public int getVariable() {
        return BR.viewModel;
    }

    @Override
    public int getLayoutResources() {
        return R.layout.layout_quests;
    }

//    @Override
//    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
//        super.onCreateOptionsMenu(menu, inflater);
//        this.menu = menu;
//        inflater.inflate(R.menu.main, menu);
//        hideNewsMenu();
//    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.hideNews:
                LocalBroadcastManager.getInstance(getContext()).sendBroadcast(new Intent(QuestsFragmentVM.ACTION_MENU_HIDE_NEWS_CLICK));
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
