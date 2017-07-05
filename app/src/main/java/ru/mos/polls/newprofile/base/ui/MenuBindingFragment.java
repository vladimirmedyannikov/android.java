package ru.mos.polls.newprofile.base.ui;

import android.databinding.ViewDataBinding;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import ru.mos.polls.R;
import ru.mos.polls.newprofile.base.vm.MenuFragmentVM;

/**
 * Created by Trunks on 05.07.2017.
 */

public abstract class MenuBindingFragment<VM extends MenuFragmentVM, B extends ViewDataBinding> extends BindingFragment<VM, B> {
    /**
     * Базовый фрагмент с меню
     */
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.confirm, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_confirm:
                getViewModel().confirmAction();
                return true;
        }
        return false;
    }

}
