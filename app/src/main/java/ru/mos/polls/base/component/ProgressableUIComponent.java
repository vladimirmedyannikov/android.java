package ru.mos.polls.base.component;

import android.view.View;

import butterknife.BindView;
import butterknife.ButterKnife;
import ru.mos.polls.R;
import ru.mos.polls.rxhttp.rxapi.progreessable.Progressable;

public class ProgressableUIComponent extends UIComponent implements Progressable {
    @BindView(R.id.progress)
    protected View progressView;
    @BindView(R.id.root)
    protected View rootView;

    @Override
    public void onViewCreated(View layout) {
        super.onViewCreated(layout);
        ButterKnife.bind(this, layout);
    }

    @Override
    public void begin() {
        progressView.setVisibility(View.VISIBLE);
        rootView.setVisibility(View.GONE);
    }

    @Override
    public void end() {
        progressView.setVisibility(View.GONE);
        rootView.setVisibility(View.VISIBLE);
    }
}
