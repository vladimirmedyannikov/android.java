package ru.mos.polls.base.vm;

import android.databinding.ViewDataBinding;

import me.ilich.juggler.gui.JugglerFragment;

public abstract class MenuFragmentVM<F extends JugglerFragment, B extends ViewDataBinding> extends FragmentViewModel<F, B> {
    /**
     * Базовый viewmodel для MenuBindingFragment
     */
    public MenuFragmentVM(F fragment, B binding) {
        super(fragment, binding);
    }

    public void onOptionsItemSelected(int menuItemId) {
    }

    public void onCreateOptionsMenu(){}
}
