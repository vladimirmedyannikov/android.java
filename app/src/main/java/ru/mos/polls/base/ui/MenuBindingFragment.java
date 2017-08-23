package ru.mos.polls.base.ui;

import android.databinding.ViewDataBinding;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import ru.mos.polls.BR;
import ru.mos.polls.base.vm.MenuFragmentVM;

/**
 * Created by Trunks on 05.07.2017.
 */

public abstract class MenuBindingFragment<VM extends MenuFragmentVM, B extends ViewDataBinding> extends NavigateFragment<VM, B> {
    /**
     * Базовый фрагмент с меню
     */
    protected Menu menu;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(getMenuResource(), menu);
        this.menu = menu;
        super.onCreateOptionsMenu(menu, inflater);
        getViewModel().onCreateOptionsMenu();
    }

    @Override
    public int getVariable() {
        return BR.viewModel;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        hideKeyboard(this);
        getViewModel().onOptionsItemSelected(item.getItemId());
        return true;
    }

    public void hideMenuItem(int menuId) {
        if (menu != null) {
            MenuItem menuItem = menu.findItem(menuId);
            if (menuItem != null) {
                menuItem.setVisible(false);
            }
        }
    }

    public void showMenuItem(int menuId) {
        if (menu != null) {
            MenuItem menuItem = menu.findItem(menuId);
            if (menuItem != null) {
                menuItem.setVisible(true);
            }
        }
    }

    public abstract int getMenuResource();
}
