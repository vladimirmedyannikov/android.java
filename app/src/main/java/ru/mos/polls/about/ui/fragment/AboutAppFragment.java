package ru.mos.polls.about.ui.fragment;

import me.ilich.juggler.Navigable;
import ru.mos.polls.BR;
import ru.mos.polls.R;
import ru.mos.polls.base.ui.BindingFragment;
import ru.mos.polls.databinding.LayoutAboutAppBinding;
import ru.mos.polls.about.vm.AboutAppFragmentVM;

/**
 * Created by matek3022 on 21.09.17.
 */

public class AboutAppFragment extends BindingFragment<AboutAppFragmentVM, LayoutAboutAppBinding> {

    private AboutAppFragmentVM.SocialListener socialListener;

    public static AboutAppFragment instance(AboutAppFragmentVM.SocialListener socialListener) {
        AboutAppFragment f = new AboutAppFragment();
        f.setSocialListener(socialListener);
        return f;
    }

    private void setSocialListener(AboutAppFragmentVM.SocialListener socialListener) {
        this.socialListener = socialListener;
    }

    @Override
    protected AboutAppFragmentVM onCreateViewModel(LayoutAboutAppBinding binding) {
        return new AboutAppFragmentVM(this, binding, socialListener);
    }

    @Override
    public int getVariable() {
        return BR.viewModel;
    }

    @Override
    public int getLayoutResources() {
        return R.layout.layout_about_app;
    }

    @Override
    public Navigable navigateTo() {
        return super.navigateTo();
    }
}