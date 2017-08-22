package ru.mos.polls.base.component;

import android.databinding.ViewDataBinding;

import me.ilich.juggler.gui.JugglerFragment;
import ru.mos.polls.newprofile.base.vm.FragmentViewModel;
import ru.mos.polls.rxhttp.rxapi.progreessable.Progressable;

public abstract class UIComponentFragmentViewModel<F extends JugglerFragment, B extends ViewDataBinding> extends FragmentViewModel<F, B> implements ComponentHolder {
    private UIComponentHolder uiComponentHolder;

    public UIComponentFragmentViewModel(F fragment, B binding) {
        super(fragment, binding);
    }

    public void onViewCreated() {
        uiComponentHolder = createComponentHolder();
        if (uiComponentHolder != null)
            uiComponentHolder.onViewCreated(getFragment().getContext(), getFragment().getView());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (uiComponentHolder != null)
            uiComponentHolder.onDestroyView();
    }

    public final UIComponentHolder getUiComponentHolder() {
        return uiComponentHolder;
    }

    @Override
    public <T extends UIComponent> T getComponent(Class<T> componentClass) {
        return uiComponentHolder.getComponent(componentClass);
    }

    protected Progressable getPullableProgressable() {
        Progressable result = getUiComponentHolder().getComponent(PullableUIComponent.class);
        if (result == null) {
            result = Progressable.STUB;
        }
        return result;
    }

    protected Progressable getProgressable() {
        Progressable result = getUiComponentHolder().getComponent(ProgressableUIComponent.class);
        if (result == null) {
            result = Progressable.STUB;
        }
        return result;
    }

    protected abstract UIComponentHolder createComponentHolder();
}
