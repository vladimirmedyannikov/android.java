package ru.mos.polls.shop.ui;

import android.view.KeyEvent;

import ru.mos.polls.BR;
import ru.mos.polls.MainActivity;
import ru.mos.polls.R;
import ru.mos.polls.base.ui.BindingFragment;
import ru.mos.polls.databinding.FragmentShopBinding;
import ru.mos.polls.shop.vm.WebShopFragmentVM;

/**
 * Магазин поощрений
 *
 * @since 1.9.2
 */

public class WebShopFragment extends BindingFragment<WebShopFragmentVM, FragmentShopBinding> implements MainActivity.Callback{
    public static WebShopFragment instance() {
        return new WebShopFragment();
    }

    @Override
    protected WebShopFragmentVM onCreateViewModel(FragmentShopBinding binding) {
        return new WebShopFragmentVM(this, binding);
    }

    @Override
    public int getVariable() {
        return BR.viewModel;
    }

    @Override
    public int getLayoutResources() {
        return R.layout.fragment_shop;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (getViewModel() == null) return false;
        return getViewModel().onKeyDown(keyCode, event);
    }
}
