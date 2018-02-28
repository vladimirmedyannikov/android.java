package ru.mos.polls.webview.ui;

import ru.mos.polls.BR;
import ru.mos.polls.R;
import ru.mos.polls.base.ui.NavigateFragment;
import ru.mos.polls.databinding.FragmentWebviewBinding;
import ru.mos.polls.webview.vm.WebViewFragmentVM;

public class WebViewFragment extends NavigateFragment<WebViewFragmentVM, FragmentWebviewBinding> {
    @Override
    protected WebViewFragmentVM onCreateViewModel(FragmentWebviewBinding binding) {
        return new WebViewFragmentVM(this, binding);
    }

    @Override
    public int getVariable() {
        return BR.viewModel;
    }

    @Override
    public int getLayoutResources() {
        return R.layout.fragment_webview;
    }
}
